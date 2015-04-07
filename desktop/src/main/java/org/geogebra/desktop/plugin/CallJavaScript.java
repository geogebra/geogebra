package org.geogebra.desktop.plugin;

import org.geogebra.common.main.App;
import org.geogebra.desktop.main.AppD;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class CallJavaScript {

	/**
	 * Evaluates the global script for the current construction and returns a
	 * scope object for this script.
	 * 
	 * @param app
	 * @return
	 */
	public static Scriptable evalGlobalScript(App app) {

		// create new scope
		Context cx = Context.enter();

		// No class loader for unsigned applets so don't try and optimize.
		// http://www.mail-archive.com/batik-dev@xmlgraphics.apache.org/msg00108.html
		if (!AppD.hasFullPermissions()) {
			cx.setOptimizationLevel(-1);
			Context.setCachingEnabled(false);
		}

		Scriptable scope = cx.initStandardObjects();

		// Initialize GgbApi functions, eg ggbApplet.evalCommand()
		GeoGebraGlobal.initStandardObjects((AppD) app, scope, null, false);

		// Evaluate the global string
		try {
			Object result = cx.evaluateString(scope, ((AppD) app).getKernel()
					.getLibraryJavaScript(), app.getPlain("ErrorAtLine"), 1,
					null);
		} catch (Throwable t) {

		}
		cx.exit();
		return scope;

	}

	/**
	 * Evaluates a local script using the global scope from the current
	 * construction.
	 * 
	 * @param app
	 * @param script
	 * @param arg
	 */
	public static void evalScript(App app, String script, String arg) {

		// get the global scope for the current construction
		Scriptable globalScope = ((ScriptManagerD) app.getScriptManager())
				.getGlobalScopeMap().get(app.getKernel().getConstruction());

		Context cx = Context.enter();

		// No class loader for unsigned applets so don't try and optimize.
		// http://www.mail-archive.com/batik-dev@xmlgraphics.apache.org/msg00108.html
		if (!AppD.hasFullPermissions()) {
			cx.setOptimizationLevel(-1);
			Context.setCachingEnabled(false);
		}

		// Create a new scope that shares the global scope
		Scriptable newScope = cx.newObject(globalScope);
		newScope.setPrototype(globalScope);
		newScope.setParentScope(null);

		// Evaluate the script.
		Object result = cx.evaluateString(newScope, script,
				app.getPlain("ErrorAtLine"), 1, null);

		cx.exit();

	}

}
