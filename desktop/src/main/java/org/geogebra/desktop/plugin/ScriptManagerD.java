package org.geogebra.desktop.plugin;

import java.util.HashMap;

import org.geogebra.common.jre.plugin.ScriptManagerJre;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.main.App;
import org.mozilla.javascript.Scriptable;

public class ScriptManagerD extends ScriptManagerJre {

	protected HashMap<Construction, Scriptable> globalScopeMap;

	public ScriptManagerD(App app) {
		super(app);
		globalScopeMap = new HashMap<>();
	}

	public HashMap<Construction, Scriptable> getGlobalScopeMap() {
		return globalScopeMap;
	}

	@Override
	protected void evalJavaScript(String jsFunction) {
		evalJavaScript(app, jsFunction, null);
	}

	@Override
	public void setGlobalScript() {
		Scriptable globalScope = CallJavaScript.evalGlobalScript(app);
		globalScopeMap.put(app.getKernel().getConstruction(), globalScope);
	}

	public void evalJavaScript(App app, String script, String arg) {
		if (globalScopeMap.get(app.getKernel().getConstruction()) == null) {
			setGlobalScript();
		}

		CallJavaScript.evalScript(app, script, arg);
	}
}
