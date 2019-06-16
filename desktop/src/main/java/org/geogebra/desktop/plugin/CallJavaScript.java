package org.geogebra.desktop.plugin;

import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;
import org.mozilla.javascript.ClassShutter;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class CallJavaScript {

	/**
	 * Evaluates the global script for the current construction and returns a
	 * scope object for this script.
	 * 
	 * @param app
	 * @return
	 */
	public static Scriptable evalGlobalScript(App app) {

		ContextFactory.initGlobal(SandboxContextFactory.getInstance());

		// create new scope
		Context cx = Context.enter();

		// No class loader for unsigned applets so don't try and optimize.
		// http://www.mail-archive.com/batik-dev@xmlgraphics.apache.org/msg00108.html
		// if (!AppD.hasFullPermissions()) {
		// cx.setOptimizationLevel(-1);
		// Context.setCachingEnabled(false);
		// }

		Scriptable scope = cx.initStandardObjects();//
		// ScriptableObject scope = cx.initSafeStandardObjects();
		boolean sealed = false;

		if (!cx.hasClassShutter) {
			cx.setClassShutter(sandboxClassShutter);
		}

		// Initialize GgbApi functions, eg ggbApplet.evalCommand()
		GeoGebraGlobal.initStandardObjects(app, scope, null, false);

		// Evaluate the global string
		try {
			cx.evaluateString(scope,
					app.getKernel().getLibraryJavaScript(),
					app.getLocalization().getMenu("ErrorAtLine"), 1, null);
		} catch (Throwable t) {

		}
		Context.exit();
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

		ScriptableObject scope = cx.initStandardObjects();

		if (!cx.hasClassShutter) {
			cx.setClassShutter(sandboxClassShutter);
		}

		// Create a new scope that shares the global scope
		Scriptable newScope = cx.newObject(globalScope);
		newScope.setPrototype(globalScope);
		newScope.setParentScope(null);

		// Evaluate the script.
		cx.evaluateString(newScope, script,
				app.getLocalization().getMenu("ErrorAtLine"), 1, null);

		Context.exit();

	}

	private static final SandboxClassShutter sandboxClassShutter = new SandboxClassShutter();


	/**
	 * 
	 * Allow access only to whitelist of allowed Java classes
	 *
	 */
	public static class SandboxClassShutter implements ClassShutter {

		@Override
		public boolean visibleToScripts(String fullClassName) {


			Log.debug("Rhino attempting to use class " + fullClassName);
			
			return fullClassName.equals(org.geogebra.desktop.plugin.GgbAPID.class.getName())
					// needed for setTimeout() emulation
					// https://gist.github.com/murkle/f4d0c02aa595f404df143d0bd31b6b88
					|| fullClassName.equals(java.util.Timer.class.getName())
					|| fullClassName.equals(java.util.TimerTask.class.getName())
					// needed for TimerTask
					|| fullClassName.equals("adapter1");


		}

	}

}
