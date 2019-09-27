package org.geogebra.web.html5.main;

import com.google.gwt.core.client.JavaScriptObject;

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

	public static native void callNativeJavaScript(String funcname) /*-{
		if ($wnd[funcname]) {
			$wnd[funcname]();
		}
	}-*/;

	public static native void callNativeJavaScript(String funcname,
			JavaScriptObject arg) /*-{
		if ($wnd[funcname]) {
			$wnd[funcname](arg);
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

	public static native void callNativeJavaScript(String funcname,
			String arg0, String arg1) /*-{
		if ($wnd[funcname]) {
			$wnd[funcname](arg0, arg1);
		}
	}-*/;

	public static native void callNativeJavaScript(JavaScriptObject funcObject,
			JavaScriptObject param) /*-{
		if (typeof funcObject === "function") {
			funcObject(param);
		}
	}-*/;

	public static native void callNativeJavaScript(JavaScriptObject funcObject,
			String arg) /*-{
		if (typeof funcObject === "function") {
			funcObject(arg);
		}
	}-*/;

	public static native void callNativeJavaScript(JavaScriptObject funcObject,
		   String arg0, String arg1) /*-{
		if (typeof funcObject === "function") {
			funcObject(arg0, arg1);
		}
	}-*/;
}
