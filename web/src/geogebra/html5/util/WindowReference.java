package geogebra.html5.util;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author gabor
 *	windowReference for GGT API
 */
public class WindowReference extends JavaScriptObject {
	
	/**
	 * protected constructor as superclass of js object
	 */
	protected WindowReference() {
		
	}
	
	/**
	 * @return the login token from GGT
	 */
	final public native String checkForLoginToken() /*-{
		//get login token from window.
		$wnd.console.log("window reference:");
		$wnd.console.log(this);
	}-*/;
	
	/**
	 * @return wheter the window closed or not
	 */
	final public native boolean isClosed() /*-{
		return this.closed;
	}-*/;
	
	/**
	 * closes the window
	 */
	final public native void close() /*-{
		this.close();
	}-*/;

}
