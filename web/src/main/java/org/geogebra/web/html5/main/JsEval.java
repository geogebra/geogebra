package org.geogebra.web.html5.main;

import com.google.gwt.core.client.JavaScriptObject;

public class JsEval {
	public static native void evalScriptNative(String script) /*-{
		$wnd.eval(script);
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
}
