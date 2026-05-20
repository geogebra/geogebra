/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.desktop.plugin;

import org.geogebra.common.jre.plugin.ScriptUtil;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.ClassShutter;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Context.ClassShutterSetter;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.NativeFunction;
import org.mozilla.javascript.Scriptable;

public class CallJavaScript {
	private static final SandboxClassShutter sandboxClassShutter = new SandboxClassShutter();

	/**
	 * Evaluates the global script for the current construction and returns a
	 * scope object for this script.
	 * 
	 * @param app application
	 * @return global scope
	 */
	public static Scriptable evalGlobalScript(App app) {
		// create new scope
		try (Context cx = Context.enter()) {
			cx.getWrapFactory().setJavaPrimitiveWrap(false);

			Scriptable scope = cx.initSafeStandardObjects();

			ClassShutterSetter setter = cx.getClassShutterSetter();
			if (setter != null) {
				setter.setClassShutter(sandboxClassShutter);
			}

			// Initialize GgbApi functions, eg ggbApplet.evalCommand()
			GeoGebraGlobal.initStandardObjects(app, scope);

			// Evaluate the global string
			try {
				cx.evaluateString(scope,
						app.getKernel().getLibraryJavaScript(),
						app.getLocalization().getMenu("ErrorAtLine"), 1, null);
			} catch (Throwable t) {
				// ignore
			}
			return scope;
		}
	}

	/**
	 * Evaluates a local script using the global scope from the current
	 * construction.
	 * 
	 * @param loc localization
	 * @param script script content
	 */
	public static void evalScript(Scriptable globalScope, String script, Localization loc) {
		try (Context context = Context.enter()) {
			context.getWrapFactory().setJavaPrimitiveWrap(false);
			Scriptable newScope = getScope(globalScope, context);

			// Evaluate the script.
			context.evaluateString(newScope, script,
					loc.getMenu("ErrorAtLine"), 1, null);
		}
	}

	/**
	 * @param nativeRunnable native JS function
	 * @param args arguments
	 * @param globalScope application
	 */
	public static void evalFunction(BaseFunction nativeRunnable, Object[] args,
			Scriptable globalScope) {
		try (Context cx = Context.enter()) {
			cx.getWrapFactory().setJavaPrimitiveWrap(false);
			Scriptable newScope = getScope(globalScope, cx);
			// Evaluate the script.
			nativeRunnable.call(cx, newScope, nativeRunnable, args);
		}
	}

	private static Scriptable getScope(Scriptable globalScope, Context cx) {
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
			return GgbAPID.class.getName().equals(fullClassName)
					|| ScriptUtil.isVisibleToScripts(fullClassName);
		}
	}

}
