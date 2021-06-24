package org.homi.plugins.scripting.vsse;

import java.util.Timer;
import java.util.TimerTask;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.proxy.ProxyExecutable;

public class JSTimer {
	private Timer t;
	private Context cx;
	
	public JSTimer(Context cx) {
		this.cx = cx;
		this.t = new Timer(true);
	}
	
	public void setInterval(ProxyExecutable task, long period) {
		this.t.schedule(new TimerTask() {
			@Override
			public void run() {
				synchronized (cx) {
					cx.enter();
					task.execute();
					cx.leave();
				}
			}
		}, 0, period);
	}
	
	public void setTimeout(ProxyExecutable task, long period) {
		this.t.schedule(new TimerTask() {
			@Override
			public void run() {
				synchronized (cx) {
					cx.enter();
					task.execute();
					cx.leave();
				}
			}
		}, period);
	}
}