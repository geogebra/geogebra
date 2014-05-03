package geogebra.plugin;

import geogebra.common.main.App;
import geogebra.main.AppD;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class CallJavaScript {

	private Context cx;
	private Scriptable scope;

	/** Singleton instance that preserves globals. */
	public final static CallJavaScript instance = new CallJavaScript();

	/** Private constructor to force use of singleton instance. */
	private CallJavaScript() {

		cx = Context.enter();
		scope = cx.initStandardObjects();
	}

	/**
	 * Evaluates a global script. No result is expected, this simply updates the
	 * global functions and objects stored in the scope field.
	 * 
	 * @param app
	 */
	public void evalGlobalScript(App app) {

		// create new scope 
		scope = cx.initStandardObjects();
		
		// Initialize GgbApi functions, eg ggbApplet.evalCommand()
		GeoGebraGlobal.initStandardObjects((AppD) app, scope, null, false);

		// No class loader for unsigned applets so don't try and optimize.
		// http://www.mail-archive.com/batik-dev@xmlgraphics.apache.org/msg00108.html
		if (!AppD.hasFullPermissions()) {
			cx.setOptimizationLevel(-1);
			Context.setCachingEnabled(false);
		}

		// Evaluate the global string
		Object result = cx.evaluateString(scope, ((AppD) app).getKernel()
				.getLibraryJavaScript(), app.getPlain("ErrorAtLine"), 1, null);

	}

	/**
	 * Evaluates a local script. 
	 * 
	 * @param app
	 * @param script
	 * @param arg
	 */
	public void evalScript(App app, String script, String arg) {

		// Create a new scope that shares the global scope
		Scriptable newScope = cx.newObject(scope);
		newScope.setPrototype(scope);
		newScope.setParentScope(null);

		// Evaluate the script.
		Object result = cx.evaluateString(newScope, script,
				app.getPlain("ErrorAtLine"), 1, null);

	}

}
