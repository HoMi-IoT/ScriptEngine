package org.homi.plugins.scripting.vsse;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import org.graalvm.polyglot.Context;

import deviceRegistrySpec.Device;

import org.graalvm.polyglot.Source;
import org.homi.plugin.api.exceptions.InternalPluginException;
import org.graalvm.polyglot.Context.Builder;


public class ScriptHandler implements Consumer<Path> {

	private ARWrapper arw;
	private Builder cb;
	private ExecutorService es;
	
	public ScriptHandler(ARWrapper arw) {
		es = Executors.newCachedThreadPool();
		this.arw = arw;
		this.cb = Context.newBuilder("js").allowAllAccess(true);
//			    .allowHostAccess(HostAccess.ALL)
//			    .allowCreateThread(true)
//			    .allowHostClassLookup(className -> true)
//			    .allowAllAccess(true)
//			    .allowPolyglotAccess(PolyglotAccess.ALL);
	}
	
	public Object eval(String script) throws InternalPluginException {
		try {
			OutputStream myOut = new OutputStream() {
				ByteArrayOutputStream contents = new ByteArrayOutputStream();
				@Override
				public void write(int b) throws IOException {
					if(contents != null)
						contents.write(b);
					System.out.write(b);
					
				}
				
				@Override
				public String toString() {
					String tmp;
					if(contents != null) {
						tmp = this.contents.toString();
						this.contents.reset();
					}
					else
						tmp = System.out.toString();
					return tmp;
				}
			};
			
			var context = cb.out(myOut).err(myOut).build();
			context.getBindings("js").putMember("ActionRegistry", this.arw);
			context.getBindings("js").putMember("ObserverFactory", new ObserverFactory(context));
			context.getBindings("js").putMember("timer", new JSTimer(context));
			context.getBindings("js").putMember("Device", Device.class);
			
			synchronized (context) {
				context.enter();
				context.eval("js", script);
				context.leave();
			}
			String myOutput = myOut.toString();
			return myOutput;
		} catch(Exception e) {
			throw new InternalPluginException(e);
		}
	}
	
	@Override
	public void accept(Path path) {
		es.execute( ()->{
			Source s;
			var context = cb.build();
			context.getBindings("js").putMember("ActionRegistry", this.arw);
			context.getBindings("js").putMember("ObserverFactory", new ObserverFactory(context));
			context.getBindings("js").putMember("timer", new JSTimer(context));
			context.getBindings("js").putMember("Device", Device.class);
			
			try {
				File f = new File("/home/nicolas/cmpe295/Testing/scripts/"+path.toString());
				s = Source.newBuilder("js", f).build();
				System.out.println("evaluating Script " + s.toString());
				synchronized (context) {
					context.enter();
					context.eval(s);
					context.leave();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

}
