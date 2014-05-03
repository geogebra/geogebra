package geogebra.plugin;

import geogebra.common.main.App;
import geogebra.main.AppD;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class CallJavaScript {

	private Context cx;
	private Scriptable scope;

	/**  Singleton instance that preserves globals. */
	public final static CallJavaScript instance = new CallJavaScript();

	/** Private constructor to force use of singleton instance. */
	private CallJavaScript() {
		
		cx = Context.enter();
		scope = cx.initStandardObjects();
	}

	/**
	 * Evaluates a global script. No result is expected, this simply updates
	 * the global functions and objects stored in the scope field.
	 * 
	 * @param app
	 */
	public void evalGlobalScript(App app) {

		// Evaluate the global string
		Object result = cx.evaluateString(scope, ((AppD) app).getKernel()
				.getLibraryJavaScript(), app.getPlain("ErrorAtLine"), 1, null);
	}

	/**
	 * Evaluates a local script. Global functions and objects are already present in
	 * the scope field.
	 * 
	 * @param app
	 * @param script
	 * @param arg
	 */
	public void evalScript(App app, String script, String arg) {

		// Initialize the JavaScript variable applet so that we can call
		// GgbApi functions, eg ggbApplet.evalCommand()

		GeoGebraGlobal.initStandardObjects((AppD) app, scope, arg, false);

		// No class loader for unsigned applets so don't try and optimize.
		// http://www.mail-archive.com/batik-dev@xmlgraphics.apache.org/msg00108.html
		if (!AppD.hasFullPermissions()) {
			cx.setOptimizationLevel(-1);
			Context.setCachingEnabled(false);
		}

		// Evaluate the script.
		Object result = cx.evaluateString(scope, script,
				app.getPlain("ErrorAtLine"), 1, null);

		// Convert the result to a string and print it.
		// Application.debug("script result: "+(Context.toString(result)));
	}

}
