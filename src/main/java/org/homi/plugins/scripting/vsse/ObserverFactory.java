package org.homi.plugins.scripting.vsse;


import java.util.function.Consumer;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyExecutable;
import org.homi.plugin.api.observer.IObserver;

public class ObserverFactory {

	private Context cx;
	
	public ObserverFactory(Context cx) {
		this.cx = cx;
	}
	
	public IObserver makeObserver(ProxyExecutable handler) {
		return new IObserver() {
			@Override
			public void update(Object... args) {
				try {
					Value[] values = new Value[args.length];
					for(int i=0; i<args.length; i++) {
						values[i] = Value.asValue(args[i]);
					}
					synchronized (cx) {
						handler.execute(values);
					}
				}catch (Exception e) {
					e.printStackTrace();
				}
			}};
	}
	

	
	public Consumer<?> makeConsumer(ProxyExecutable handler) {
		return new Consumer<Object>() {
			@Override
			public void accept(Object value) {
				try {
					synchronized (cx) {
						handler.execute(Value.asValue(value));
					}
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
	}
}
