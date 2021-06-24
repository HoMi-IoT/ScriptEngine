package org.homi.plugins.scripting.vsse;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import org.graalvm.polyglot.Context;

import deviceRegistrySpec.Device;

import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
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
			Value v;
			var context = cb.build();
			context.getBindings("js").putMember("ActionRegistry", this.arw);
			context.getBindings("js").putMember("ObserverFactory", new ObserverFactory(context));
			context.getBindings("js").putMember("timer", new JSTimer(context));
			context.getBindings("js").putMember("Device", Device.class);
			
			synchronized (context) {
				context.enter();
				v = context.eval("js", script);
				context.leave();
			}
			return v;
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
