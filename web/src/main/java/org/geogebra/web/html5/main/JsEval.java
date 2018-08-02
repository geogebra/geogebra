package org.geogebra.web.html5.main;

import com.google.gwt.core.client.JavaScriptObject;

public class JsEval {
	/**
	 * 
	 * @param script
	 * @param appletID
	 *            eg ggbApplet or ggbApplet12345
	 * @param arg
	 *            argument
	 */
	public static native void evalScriptNative(String script,
			String appletID, String arg) /*-{

		var oldAlert = $wnd.alert;
		var oldApplet = $wnd.ggbApplet;

		$wnd.ggbApplet = $wnd[appletID];
		$wnd.alert = function(a) {
			$wnd[appletID] && $wnd[appletID].showTooltip(a)
		};
		try {
			$wnd.eval(script);
		} finally {
			$wnd.alert = oldAlert;
			$wnd.ggbApplet = oldApplet;
		}
	}-*/;

	public static native void callNativeJavaScript(String funcname) /*-{
		if ($wnd[funcname]) {
			$wnd[funcname]();
		}
	}-*/;

	public static native void callNativeJavaScript(String funcname,
			String arg) /*-{
		if ($wnd[funcname]) {
			$wnd[funcname](arg);
		}
	}-*/;

	public static native void callNativeJavaScriptMultiArg(String funcname,
			JavaScriptObject arg) /*-{
		if ($wnd[funcname]) {
			$wnd[funcname](arg);
		}
	}-*/;

	public static native void callNativeJavaScriptMultiArg(String funcname,
			String arg0, String arg1) /*-{
		if ($wnd[funcname]) {
			$wnd[funcname](arg0, arg1);
		}
	}-*/;

	public static native void runCallback(JavaScriptObject onLoadCallback,
			JavaScriptObject ref) /*-{
		if (typeof onLoadCallback === "function") {
			onLoadCallback(ref);
		}
	}-*/;

	public static native void runCallback(JavaScriptObject onLoadCallback,
			String ref) /*-{
		if (typeof onLoadCallback === "function") {
			onLoadCallback(ref);
		}
	}-*/;

	/**
	 * @param fun
	 *            JS function name
	 * @param arg0
	 *            first argument
	 * @param arg1
	 *            second argument
	 */
	public static void callAppletJavaScript(String fun, Object arg0,
			Object arg1) {
		if (arg0 == null && arg1 == null) {
			JsEval.callNativeJavaScript(fun);
		} else if (arg0 != null && arg1 == null) {
			// Log.debug("calling function: " + fun + "(" + arg0.toString()
			// + ")");
			JsEval.callNativeJavaScript(fun, arg0.toString());
		} else if (arg0 != null && arg1 != null) {
			JsEval.callNativeJavaScriptMultiArg(fun, arg0.toString(),
					arg1.toString());
		}

	}
}
