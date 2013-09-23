package geogebra.html5.util;


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
	public native static WindowReference open(String url, String name, String features) /*-{
		return $wnd.open(url, name, features);
	}-*/;

}
