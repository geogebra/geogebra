package geogebra.web.util;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayInteger;

public class JSON {

	public static native String stringify(JavaScriptObject obj) /*-{
	    return	$wnd.JSON.stringify(obj);
    }-*/;

}
