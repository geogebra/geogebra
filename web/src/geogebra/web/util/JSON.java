package geogebra.web.util;

import geogebra.web.jso.JsUint8Array;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayInteger;

public class JSON {

    public static native String stringify(JavaScriptObject obj) /*-{ 
		return $wnd.JSON.stringify(obj); 
    }-*/;

	public static native JavaScriptObject parse(String obj) /*-{
		return $wnd.JSON.parse(obj);
	}-*/;
}
