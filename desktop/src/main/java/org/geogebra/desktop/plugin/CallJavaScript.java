package org.geogebra.desktop.plugin;

import org.geogebra.common.jre.headless.GgbAPIHeadless;
import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;
import org.mozilla.javascript.ClassShutter;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Context.ClassShutterSetter;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.NativeFunction;
import org.mozilla.javascript.Scriptable;

public class CallJavaScript {

	/**
	 * Evaluates the global script for the current construction and returns a
	 * scope object for this script.
	 * 
	 * @param app application
	 * @return global scope
	 */
	public static Scriptable evalGlobalScript(App app) {
		if (!ContextFactory.hasExplicitGlobal()) {
			ContextFactory.initGlobal(SandboxContextFactory.getInstance());
		}

		// create new scope
		Context cx = Context.enter();
		cx.getWrapFactory().setJavaPrimitiveWrap(false);

		Scriptable scope = cx.initStandardObjects();

		ClassShutterSetter setter = cx.getClassShutterSetter();
		if (setter != null) {
			setter.setClassShutter(sandboxClassShutter);
		}

		// Initialize GgbApi functions, eg ggbApplet.evalCommand()
		GeoGebraGlobal.initStandardObjects(app, scope, null, false);

		// Evaluate the global string
		try {
			cx.evaluateString(scope,
					app.getKernel().getLibraryJavaScript(),
					app.getLocalization().getMenu("ErrorAtLine"), 1, null);
		} catch (Throwable t) {
			// ignore
		}
		Context.exit();
		return scope;
	}

	/**
	 * Evaluates a local script using the global scope from the current
	 * construction.
	 * 
	 * @param app application
	 * @param script script content
	 */
	public static void evalScript(App app, String script) {
		Context cx = Context.enter();
		cx.getWrapFactory().setJavaPrimitiveWrap(false);
		cx.initStandardObjects();
		Scriptable newScope = getScope(app, cx);

		// Evaluate the script.
		cx.evaluateString(newScope, script,
				app.getLocalization().getMenu("ErrorAtLine"), 1, null);

		Context.exit();

	}

	private static final SandboxClassShutter sandboxClassShutter = new SandboxClassShutter();

	/**
	 * @param nativeRunnable native JS function
	 * @param args arguments
	 * @param app application
	 */
	public static void evalFunction(NativeFunction nativeRunnable, Object[] args, App app) {
		Context cx = Context.enter();
		cx.getWrapFactory().setJavaPrimitiveWrap(false);
		cx.initStandardObjects();
		Scriptable newScope = getScope(app, cx);
		// Evaluate the script.
		nativeRunnable.call(cx, newScope, nativeRunnable, args);

		Context.exit();
	}

	private static Scriptable getScope(App app, Context cx) {
		Scriptable globalScope = ((ScriptManagerD) app.getScriptManager())
				.getGlobalScopeMap().get(app.getKernel().getConstruction());
		ClassShutterSetter setter = cx.getClassShutterSetter();
		if (setter != null) {
			setter.setClassShutter(sandboxClassShutter);
		}

		// Create a new scope that shares the global scope
		Scriptable newScope = cx.newObject(globalScope);
		newScope.setPrototype(globalScope);
		newScope.setParentScope(null);
		return newScope;
	}

	/**
	 * Allow access only to whitelist of allowed Java classes
	 */
	public static class SandboxClassShutter implements ClassShutter {

		@Override
		public boolean visibleToScripts(String fullClassName) {
			Log.debug("Rhino attempting to use class " + fullClassName);
			
			return fullClassName.equals(org.geogebra.desktop.plugin.GgbAPID.class.getName())
					|| fullClassName.equals(GgbAPIHeadless.class.getName())
					// needed for setTimeout() emulation
					// https://gist.github.com/murkle/f4d0c02aa595f404df143d0bd31b6b88
					|| fullClassName.equals(java.util.Timer.class.getName())
					|| fullClassName.equals(java.util.TimerTask.class.getName())
					// eg java.lang.String
					|| fullClassName.startsWith("java.lang")
					// needed for TimerTask
					|| fullClassName.equals("adapter1");
		}
	}

}
