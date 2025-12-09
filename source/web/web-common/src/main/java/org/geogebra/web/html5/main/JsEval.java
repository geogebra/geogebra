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

package org.geogebra.web.html5.main;

import org.geogebra.common.plugin.GgbAPI;
import org.geogebra.gwtutil.JsConsumer;

import elemental2.core.Function;
import elemental2.dom.DomGlobal;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

public class JsEval {
	/**
	 * 
	 * @param script
	 *            script to execute
	 * @param api
	 *            applet API
	 */
	public static void evalScriptNative(String script,
			GgbAPI api) {
		JsPropertyMap<Object> wnd = Js.asPropertyMap(DomGlobal.window);
		Object oldAlert = wnd.get("alert");
		wnd.set("alert", (JsConsumer<String>) api::showTooltip);
		try {
			JsConsumer<String> evalFn = Js.uncheckedCast(wnd.get("eval"));
			evalFn.accept(script);
		} finally {
			wnd.set("alert", oldAlert);
		}
	}

	/**
	 * @param funcname global function name
	 * @param args arguments
	 */
	public static void callNativeGlobalFunction(String funcname, Object... args) {
		Object globalFunction = Js.asPropertyMap(DomGlobal.window).get(funcname);
		callNativeFunction(globalFunction, args);
	}

	/**
	 * Safely call function with given arguments
	 * @param funcObject function
	 * @param args arguments
	 */
	public static void callNativeFunction(Object funcObject, Object... args) {
		if (isFunction(funcObject)) {
			((Function) funcObject).apply(DomGlobal.window, args);
		}
	}

	/**
	 * @param object object
	 * @return whether object is a JS function
	 */
	public static boolean isFunction(Object object) {
		return "function".equals(Js.typeof(object));
	}

	/**
	 * Check if object is undefined.
	 * @param object object
	 * @return whether it's undefined
	 */
	public static boolean isUndefined(Object object) {
		return "undefined".equals(Js.typeof(object));
	}

	/**
	 * TODO with recent GWT this is just `instanceof String`
	 * @param object object
	 * @return whether it's JS string
	 */
	public static boolean isJSString(Object object) {
		return "string".equals(Js.typeof(object));
	}
}
