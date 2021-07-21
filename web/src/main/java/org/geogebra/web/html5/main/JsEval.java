package org.geogebra.web.html5.main;

import org.geogebra.common.plugin.GgbAPI;
import org.geogebra.web.html5.util.JsConsumer;

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

	public static boolean isUndefined(Object object) {
		return "undefined".equals(Js.typeof(object));
	}

	public static boolean isJSString(Object object) {
		return "string".equals(Js.typeof(object));
	}
}
