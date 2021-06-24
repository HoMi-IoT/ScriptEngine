package org.homi.plugins.scripting.vsse;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.proxy.ProxyExecutable;
import org.homi.plugin.api.exceptions.PluginException;
import org.homi.plugin.specification.exceptions.ArgumentLengthException;
import org.homi.plugin.specification.exceptions.InvalidArgumentException;
import org.homi.plugins.ar.specification.actions.ScriptActionDefinition;

public class TesterHelper {
	@HostAccess.Export
	public void expose(String command, List<Object> actionDef, ProxyExecutable pe) {
		System.out.println(command);
		System.out.println(Arrays.toString(actionDef.toArray()));
		System.out.println(pe);
		
	}
}
