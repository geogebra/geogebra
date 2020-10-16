package org.geogebra.desktop.plugin;

import java.util.ArrayList;
import java.util.HashMap;

import org.geogebra.common.jre.plugin.ScriptManagerJre;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.main.App;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeFunction;
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
		ensureGlobalScript(app);
		CallJavaScript.evalScript(app, script, arg);
	}

	@Override
	protected void callNativeListener(Object nativeRunnable, Object[] args) {
		ensureGlobalScript(app);
		callNativeFunction(nativeRunnable, args);
	}

	@Override
	protected void callListener(String globalFunctionName, Object[] args) {
		ensureGlobalScript(app);
		Scriptable scriptable = globalScopeMap.get(app.getKernel().getConstruction());
		Object nativeRunnable = scriptable.get(globalFunctionName, scriptable);
		callNativeFunction(nativeRunnable, args);
	}

	private void ensureGlobalScript(App app) {
		if (globalScopeMap.get(app.getKernel().getConstruction()) == null) {
			setGlobalScript();
		}
	}

	private void callNativeFunction(Object nativeRunnable, Object[] args) {
		if (nativeRunnable instanceof org.mozilla.javascript.NativeFunction) {
			NativeFunction nativeFunction = (NativeFunction) nativeRunnable;
			CallJavaScript.evalFunction(nativeFunction, args, app);
		}
	}

	@Override
	protected Object toNativeArray(ArrayList<String> args) {
		ensureGlobalScript(app);
		Scriptable scriptable = globalScopeMap.get(app.getKernel().getConstruction());
		return Context.enter().newArray(scriptable, args.toArray(new Object[0]));
	}
}
