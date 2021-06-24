module org.homi.plugins.scripting.vsse {
	requires org.homi.plugin.api;
	requires org.graalvm.sdk;
	requires org.homi.plugins.actionRegistry.specification;
	requires org.homi.plugin.specification;
	requires org.homi.plugins.scriptingengine.specification;
	requires deviceRegistrySpec;
	
	exports org.homi.plugins.scripting.vsse;
	
	provides org.homi.plugin.api.basicplugin.IBasicPlugin
		with org.homi.plugins.scripting.vsse.ScriptingEnginePlugin;
}