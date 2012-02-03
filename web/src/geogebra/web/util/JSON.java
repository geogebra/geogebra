package geogebra.web.util;

import geogebra.web.jso.JsUint8Array;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayInteger;

public class JSON {

    public static native String stringify(JavaScriptObject obj) /*-{ 
		return $wnd.JSON.stringify(obj); 
    }-*/;

	public static native String stringify(JsUint8Array array) /*-{
		//$wnd.console.log(array.length);
	    return ""+array.length;//FIXME	$wnd.JSON.stringify(array.length);
    }-*/;

	public static native String stringify(JsArrayInteger array) /*-{
		//$wnd.console.log(array.length);
    	return ""+array.length;//FIXME	$wnd.JSON.stringify(array.length);
	}-*/;
}
