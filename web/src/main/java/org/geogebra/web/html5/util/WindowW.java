package org.geogebra.web.html5.util;

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

	/**
	 * Post message using message API
	 * 
	 * @param gifWnd
	 *            window reference
	 * @param message
	 *            message
	 */
	public native static void postMessage(JavaScriptObject gifWnd,
	        String message) /*-{
		if (gifWnd && gifWnd.postMessage) {
			gifWnd.postMessage(message, '*');
		}
	}-*/;

	/**
	 * 
	 * @param data
	 *            HTML text (not encoded)
	 * @return window reference
	 */
	public native static JavaScriptObject openFromData(String data) /*-{
		// TODO Auto-generated method stub
		return $wnd.open("data:text/html," + encodeURIComponent(data),
				"_blank", "");
	}-*/;
}
