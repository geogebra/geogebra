package org.geogebra.web.html5.util;

/**
 * @author gabor Helper methods to register custom elements, see:
 * 
 */
public final class CustomElements {

	/**
	 * wheter custom elements supported or not
	 */
	public static boolean supported = checkSupport();

	/**
	 * Register the <geogebra-web /> element in the DOM
	 */
	public static void registerGeoGebraWebElement() {
		// register(GeoGebraConstants.WEB_CUSTOM_HTML_ELEMENT_NAME);
	}

	private static native boolean checkSupport() /*-{
		return ("register" in $doc);
	}-*/;

	private static native void register(String element) /*-{
		if (@org.geogebra.web.html5.util.CustomElements::supported) {
			$doc.register(element);
		} else {
			//TODO: fallback
			$wnd.console.log("custom elements are not supported");
		}
	}-*/;

}
