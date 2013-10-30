package geogebra.html5.util;

import geogebra.common.GeoGebraConstants;

/**
 * @author gabor
 * Helper methods to register custom elements, see:
 * 
 */
public final class CustomElements {
	
	public static boolean supported = false;
	
	
	
	/**
	 * Register the <geogebra-web /> element in the DOM
	 */
	public static void registerGeoGebraWebElement() {
		register(GeoGebraConstants.WEB_CUSTOM_HTML_ELEMENT_NAME);
	}

	private static native void register(String element) /*-{
	    if ($doc && $doc.register) {
	    	$doc.reqister(element);
	    	@geogebra.html5.util.CustomElements::supported = true;
	    } else {
	    	@geogebra.html5.util.CustomElements::supported = false;
	    	@geogebra.common.main.App::debug(Ljava/lang/String;)("Custom elements are not supported");
	    }
    }-*/;

}
