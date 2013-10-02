package geogebra.html5.util;

import geogebra.common.main.App;
import geogebra.html5.move.ggtapi.operations.LoginOperationW;
import geogebra.web.main.AppW;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Window;

/**
 * @author gabor
 *	windowReference for GGT API
 */
public class WindowReference {
	
	private JavaScriptObject wnd = null;;
	private static WindowReference instance = null;

	/**
	 * protected constructor as superclass of js object
	 */
	private WindowReference() {
		
	}
	
	public native void close() /*-{
		var wnd = this.@geogebra.html5.util.WindowReference::wnd;
		if (wnd) {
			wnd.close();
		}
	}-*/;
	
	/**
	 * @param app Application
	 * @return reference to this object
	 */
	public static WindowReference createSignInWindow(App app) {
		if (instance == null) {
			instance = new WindowReference();
			int  width = 900;
			int height = 500;
			int left = (Window.getClientWidth() / 2) - (width / 2);
			int top = (Window.getClientHeight() / 2) - (height / 2);
			LoginOperationW lOW = ((LoginOperationW) app.getLoginOperation());
					instance.wnd = WindowW.open(lOW.getOpenerUrl() +
					"?redirect=" +
					lOW.getLoginURL(((AppW) app).getLocalization().getLanguage()) +
					"&callback=" +
					lOW.getCallbackUrl(),
					"GeoGebraTube" ,
						"resizable," +
						"toolbar=no," +
						"location=no," +
						"scrollbars=no, " + 
						"statusbar=no, " +
						"titlebar=no, " + 
						"width=" + width +"," +
						"height=" + height + "," +
						"left=" + left + ", " +
						"top=" + top);		
			}
		return instance;
	}
	
	/**
	 * @return the window instance wrapper
	 */
	public static WindowReference get() {
		return instance;
	}
	
	/**
	 * @return the closed state of the
	 */
	public native boolean closed() /*-{
		var wnd = this.@geogebra.html5.util.WindowReference::wnd;
		if (wnd) {
			return wnd.closed;
		}
		return false;
	}-*/;

}
