package org.geogebra.web.html5.util;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class JSON {

	public static native String stringify(JavaScriptObject obj) /*-{
		return $wnd.JSON.stringify(obj);
	}-*/;

	public static native JavaScriptObject parse(String obj) /*-{
		return $wnd.JSON.parse(obj);
	}-*/;

	public static native String get(JavaScriptObject obj, String key) /*-{
		return obj[key];
	}-*/;

	public static native void put(JavaScriptObject obj, String key, String value) /*-{
		obj[key] = value;
	}-*/;

	public static native void putObject(JavaScriptObject obj, String key,
	        JavaScriptObject value) /*-{
		obj[key] = value;
	}-*/;

	public static native void put(JavaScriptObject obj, String key,
	        JsArray<JavaScriptObject> value) /*-{
		obj[key] = value;
	}-*/;

	public static native boolean getAsBoolean(JavaScriptObject obj, String key) /*-{
		return !!obj[key];
	}-*/;

}
