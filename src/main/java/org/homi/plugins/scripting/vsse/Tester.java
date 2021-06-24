package org.homi.plugins.scripting.vsse;


import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyArray;
import org.graalvm.polyglot.proxy.ProxyExecutable;
import org.homi.plugin.api.observer.IObserver;

import deviceRegistrySpec.Device;


public class Tester {
		
	public static void main(String[] args) {
		IObserver[] l = new IObserver[1];
		var cb = Context.newBuilder("js").allowAllAccess(true)
				.allowHostAccess(HostAccess.newBuilder(HostAccess.ALL).targetTypeMapping(
				List.class, Object.class, null, (v)->v).build());
//				.allowHostAccess(HostAccess.newBuilder().allowPublicAccess(true).targetTypeMapping(
//				Value.class, Object[].class, (v)-> v.hasArrayElements(), (v)->v.as(Object[].class)).build());
		
		ProxyExecutable[] pe = new ProxyExecutable[1];
//		System.out.println(pe[0] instanceof Serializable );
		Device d;
		var context = cb.build();
		context.getBindings("js").putMember("of", new ObserverFactory(context));
		context.getBindings("js").putMember("Device", Device.class);
		context.getBindings("js").putMember("l", l);
		context.getBindings("js").putMember("pe", pe);
		context.getBindings("js").putMember("timer", new JSTimer(context));
		context.getBindings("js").putMember("th", new TesterHelper());
		
//		synchronized (context) {
//			context.enter();
//			context.eval("js","var JArray = Java.type(\"java.lang.reflect.Array\");"
//					+ "let d = new Device(\"test\");"
//					+ "d.addGroup(\"omris\");"
//					+ "console.log(d.getGroups().size());"
//					+ "console.log(\"Hey Omri\");"
//					+ "try{"
//					+ "l[0] = of.makeObserver( (val)=> {\n"
//					+ "\n         console.log(val);    "
//					+ "           console.log(\"bye Omri\");"
//					+ "        });"
//					+ "} catch(e){console.log(e);}"
////					+ "pe[0] = (val1, val2)=>{ return [1,2,3,4,5];}"
////					+ "pe[0] = (val1, val2)=>{ return {name: \"Mason\", age:10};}"
//					+ "var JArray = Java.type(\"java.lang.reflect.Array\");\n"
//					+ "var JByte = Java.type(\"java.lang.Byte\");\n"
//					+ "\n"
//					+ "\n"
//					+ "var on = JArray.newInstance(JByte, 1);\n"
//					+ "on[0] = new JByte(01);\n"
//					+ "\n"
//					+ "var off = JArray.newInstance(JByte, 1);\n"
//					+ "off[0] = new JByte(00);"
////					+ "pe[0] = (val1, val2)=>{ return { 0: \"D3:CC:D7:6E:86:F2\", 1: off , 2: \"932c32bd-0000-47a2-835a-a8d455b859dd\",3:\"932c32bd-0002-47a2-835a-a8d455b859dd\"};}"
//					+ "pe[0] = (val1, val2)=>{ return [\"D3:CC:D7:6E:86:F2\", off , \"932c32bd-0000-47a2-835a-a8d455b859dd\",\"932c32bd-0002-47a2-835a-a8d455b859dd\"];}"
////					+ "pe[0] = (val)=>{ return val>17.3;}"
//					+ "");
//			context.leave();
//		}
//		System.out.println(l[0]);
//		l[0].update(new ScriptingEnginePlugin());

//		Map<String, Object> vals = (Map<String, Object>) pe[0].execute();
//		System.out.println(Arrays.toString(vals.entrySet().toArray()));

//		ProxyArray vals =  ProxyArray.fromArray(pe[0].execute());
//		Value  vals =  context.asValue(pe[0].execute());
//		System.out.println(Arrays.toString(vals.as(Object[].class)));
		//after host access
//		var  vals = context.asValue(pe[0].execute());
//		System.out.println(Value.asValue(vals.as(Object[].class)[1]).isHostObject());
		

//		Value  vals =  pe[0].execute();
		
//		var  vals =  pe[0].execute(Value.asValue(Float.valueOf(13.5f)));
//		System.out.println(vals);
		
		
//		Double vals = (Double) pe[0].execute();
//		System.out.println(vals);
		
		
		synchronized (context) {
			context.enter();
			context.eval("js","var JArray = Java.type(\"java.lang.reflect.Array\");"
					+ "th.expose(\"ScriptCommand\", [\"firstArg\", \"Second Tag\", 16], (args)=>{ console.log(\":::::::-----------:::::::::::::::called script action:::::::-----------:::::::::::::::\");});"
					+ "");
			context.leave();
		}
	}
}
