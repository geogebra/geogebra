package org.geogebra.web.html5.main;

import elemental2.core.Function;
import elemental2.dom.DomGlobal;
import jsinterop.base.Js;

public class JsEval {
	/**
	 * 
	 * @param script
	 *            script to execute
	 * @param appletID
	 *            eg ggbApplet or ggbApplet12345
	 */
	public static native void evalScriptNative(String script,
			String appletID) /*-{

		var oldAlert = $wnd.alert;
		$wnd.alert = function(a) {
			$wnd[appletID] && $wnd[appletID].showTooltip(a)
		};
		try {
			$wnd.eval(script);
		} finally {
			$wnd.alert = oldAlert;
		}
	}-*/;

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
