package geogebra.web.html5;

import com.google.gwt.core.client.JavaScriptObject;

public class FormData extends JavaScriptObject {
	
	public static native FormData create() /*-{
		var fd = new $wnd.FormData();
		return fd;
	}-*/;
	
	protected FormData() {
		
	}
	
	public native final void append(String key, String value) /*-{
		this.append(key, value);
	}-*/;

}
