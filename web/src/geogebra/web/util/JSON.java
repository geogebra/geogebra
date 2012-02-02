package geogebra.web.util;

import geogebra.web.jso.JsUint8Array;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayInteger;

public class JSON {

	public static native String stringify(JavaScriptObject array) /*-{
		//$wnd.console.log(array.length);
	    return	$wnd.JSON.stringify(array.length);
    }-*/;
}
