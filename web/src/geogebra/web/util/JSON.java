package geogebra.web.util;

import com.google.gwt.core.client.JavaScriptObject;

public class JSON {

    public static native String stringify(JavaScriptObject obj) /*-{ 
		return $wnd.JSON.stringify(obj); 
    }-*/;

	public static native JavaScriptObject parse(String obj) /*-{
		return $wnd.JSON.parse(obj);
	}-*/;

	public static native String get(JavaScriptObject obj, String attr) /*-{
	   return obj[attr] ;
    }-*/;

	public static native void put (JavaScriptObject file, String attr,
            String value) /*-{
	   file[attr] = value;
    }-*/;
}
