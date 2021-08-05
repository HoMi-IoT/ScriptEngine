package org.homi.plugins.scripting.vsse;

import java.util.Arrays;
import java.util.Map;

import org.homi.plugin.api.PluginID;
import org.homi.plugin.api.basicplugin.AbstractBasicPlugin;
import org.homi.plugin.api.commander.Commander;
import org.homi.plugin.api.commander.CommanderBuilder;
import org.homi.plugin.api.commander.IReceiver;
import org.homi.plugin.api.exceptions.InternalPluginException;
import org.homi.plugin.api.exceptions.PluginException;
import org.homi.plugin.specification.exceptions.ArgumentLengthException;
import org.homi.plugin.specification.exceptions.InvalidArgumentException;
import org.homi.plugins.ar.specification.actions.Action;
import org.homi.plugins.scriptingengine.specification.SESpec;


@PluginID(id="ScriptingEngine")
public class ScriptingEnginePlugin extends AbstractBasicPlugin {

	DirectoryWatcher dw;
	ScriptHandler sh;
	
	@Override
	public void setup() {

		Action.setPluginProvider(this.getPluginProvider());
		ARWrapper arw = null;
		try {
			arw = new ARWrapper();	
			sh = new ScriptHandler(arw);
			dw = new DirectoryWatcher("/home/nicolas/cmpe295/Testing/scripts/", sh);
		} catch (PluginException | InvalidArgumentException | ArgumentLengthException e) {
			throw new RuntimeException(e);
		}
		CommanderBuilder<SESpec> cb = new CommanderBuilder<>(SESpec.class);
		Commander<SESpec> c = cb
				.onCommandEquals(SESpec.INVOKE_SCRIPT_ACTION, this::invokeScriptAction)
				.onCommandEquals(SESpec.EVAL_SCRIPT, this::eval)
				.build(); 
		
		this.addCommander(SESpec.class, c);
		this.addWorker(dw);
	}
	
	private Object eval(Object...args) throws InternalPluginException {
		try {
			return sh.eval((String)args[0]);
		} catch(Exception e) {
			throw new InternalPluginException(e);
		}
	}
	
	private Object invokeScriptAction(Object...args) throws InternalPluginException {
		try {

			return ARWrapper.invoke((String)args[0], (Map<String,Object>)args[1]);
		} catch(Exception e) {
			throw new InternalPluginException(e);
		}
	}

	@Override
	public void teardown() {
		DirectoryWatcher.stop = true;
	}

}
