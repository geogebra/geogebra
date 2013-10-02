package geogebra.html5.util;

import com.google.gwt.core.client.JavaScriptObject;


/**
 * @author gabor
 * 
 * Own window handling methods
 *
 */
public class WindowW {
	
	/**
	 * @param url the url to open
	 * @param name the name of the window
	 * @param features what to show and what not
	 * @return the reference to the window
	 */
	public native static JavaScriptObject open(String url, String name, String features) /*-{
		return (window.geogebbratubewindow = window.open(url, name, features));
	}-*/;

}
