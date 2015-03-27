package geogebra.html5.util;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author gabor
 * 
 *         Own window handling methods
 *
 */
public class WindowW {

	/**
	 * @param url
	 *            the url to open
	 * @param name
	 *            the name of the window
	 * @param features
	 *            what to show and what not
	 * @return the reference to the window
	 */
	public native static JavaScriptObject open(String url, String name,
	        String features) /*-{
		return $wnd.open(url, name, features);
	}-*/;

	public native static void postMessage(JavaScriptObject gifWnd,
	        String message) /*-{
		var domain = $wnd.location.origin;
		gifWnd.postMessage(message, domain);
	}-*/;
}
