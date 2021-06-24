package org.homi.plugins.scripting.vsse;

import org.homi.plugin.api.exceptions.PluginException;
import org.homi.plugin.specification.exceptions.ArgumentLengthException;
import org.homi.plugin.specification.exceptions.InvalidArgumentException;
import org.homi.plugins.ar.specification.actions.Action;
import org.homi.plugins.ar.specification.actions.ActionQuery;
import org.homi.plugins.ar.specification.actions.ActionQuery.TYPE;
import org.homi.plugins.ar.specification.actions.ScriptActionDefinition;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyExecutable;

public class ARWrapper {
	
	public Action<Void> defineAction;
	public static Map<String, ProxyExecutable> actions = new HashMap<>();
	
	public ARWrapper() throws InvalidArgumentException, ArgumentLengthException, PluginException {
		ActionQuery aq = new ActionQuery().type(TYPE.SPECIFICATION).pluginID("ActionRegistry").specificationID("ARSpec").command("DEFINE");
		this.defineAction = Action.getAction(aq);
	}

	private ActionQuery getSelfQuery() {
		return new ActionQuery().type(TYPE.SPECIFICATION).pluginID("ScriptingEngine").specificationID("SESpec").command("INVOKE_SCRIPT_ACTION");
	}
	
	@HostAccess.Export
	public Object call(String specId, String command, List<Object> args) {
		try {
			ActionQuery aq = new ActionQuery().type(TYPE.SPECIFICATION).specificationID(specId).command(command);
			Action<?> action = Action.getAction(aq);
			
			System.out.println(Arrays.toString(args.toArray()));
			Map<String, Object> arguments = new HashMap<>();
			for(int i=0; i<args.size(); i++) {
				arguments.put(Integer.toString(i), args.get(i));
			}
			return action.run(arguments);
			
		} catch (InvalidArgumentException | ArgumentLengthException | PluginException e) {
			throw new RuntimeException(e);
		}
	}
	
	@HostAccess.Export
	public void expose(String command, List<String> actionDef, ProxyExecutable pe) {
		try {
			ARWrapper.actions.put(command, pe);
//			defineAction.set("0", new ScriptActionDefinition(command, actionDef, this.getSelfQuery()));
//			System.out.println("in EXPOSE exposing: " + command);
			this.defineAction.run(new ScriptActionDefinition(command, actionDef, this.getSelfQuery()));
		} catch (InvalidArgumentException | ArgumentLengthException | PluginException e) {
			e.printStackTrace();
		}
	}

	public static Object invoke(String command, Map<String, Object> arguments) throws PluginException {
		try {
			return ARWrapper.actions.get(command).execute(Value.asValue(arguments));
		} catch(Exception e) {
			throw new PluginException(e);
		}
	}
}
