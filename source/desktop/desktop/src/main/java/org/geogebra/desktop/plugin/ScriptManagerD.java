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

	/**
	 * @param app application
	 */
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

	/**
	 * @param app application
	 * @param script script content
	 * @param arg argument TODO unused
	 */
	public void evalJavaScript(App app, String script, String arg) {
		ensureGlobalScript(app);
		CallJavaScript.evalScript(app, script);
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
		Context cx = Context.enter();
		cx.getWrapFactory().setJavaPrimitiveWrap(false);
		return cx.newArray(scriptable, args.toArray(new Object[0]));
	}
}
