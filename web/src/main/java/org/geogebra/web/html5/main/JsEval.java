package org.geogebra.web.html5.main;

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

	public static native void callNativeJavaScript(String funcname, String... args) /*-{
		if ($wnd[funcname]) {
			$wnd[funcname].apply(null, args);
		}
	}-*/;

	public static native void callNativeJavaScript(Object funcObject,
			String... args) /*-{
		if (typeof funcObject === "function") {
			funcObject.apply(null, args);
		}
	}-*/;

	public static native void callNativeJavaScript(String funcname, Object arg) /*-{
		if ($wnd[funcname]) {
			$wnd[funcname](arg);
		}
	}-*/;

	public static native void callNativeJavaScript(Object funcObject, Object param) /*-{
		if (typeof funcObject === "function") {
			funcObject(param);
		}
	}-*/;
}
