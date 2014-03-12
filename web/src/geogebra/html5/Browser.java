package geogebra.html5;

import com.google.gwt.core.client.GWT;

public class Browser {
	public static boolean webWorkerSupported = false;

	public static native boolean isFirefox() /*-{
		// copying checking code from the checkWorkerSupport method
		// however, this is not necessarily the best method to decide
		if (navigator.userAgent.toLowerCase().indexOf("firefox") != -1) {
			return true;
		}
		return false;
	}-*/;

	public static native boolean isIE() /*-{
		// copying checking code from isFirefox() and checked from web
		// however, this is not necessarily the best method to decide
		if (navigator.userAgent.toLowerCase().indexOf("msie") > -1) {
			return true;
		}
		return false;
	}-*/;
	
	public native static boolean externalCAS() /*-{
		return typeof $wnd.evalGeoGebraCASExternal == 'function';
	}-*/;

	public static  boolean checkWorkerSupport(String workerpath){
		if("touch".equals(GWT.getModuleName())){
			return false;
		}
		return nativeCheckWorkerSupport(workerpath);
	}

	public static native boolean nativeCheckWorkerSupport(String workerpath) /*-{
		// Worker support in Firefox is incompatible at the moment for zip.js,
		// see http://gildas-lormeau.github.com/zip.js/ for details:
		if (navigator.userAgent.toLowerCase().indexOf("firefox") != -1) {
			@geogebra.common.main.App::debug(Ljava/lang/String;)("INIT: worker not supported in Firefox, fallback for simple js");
			return false;
		}
		if (navigator.userAgent.toLowerCase().indexOf("safari") != -1
			&& navigator.userAgent.toLowerCase().indexOf("chrome") == -1) {
			@geogebra.common.main.App::debug(Ljava/lang/String;)("INIT: worker not supported in Safari, fallback for simple js");
			return false;
		}
		
	    try {
	    	var worker = new $wnd.Worker(workerpath+"js/workercheck.js");
	    } catch (e) {
	    	@geogebra.common.main.App::debug(Ljava/lang/String;)("INIT: worker not supported (no worker at " + workerpath + "), fallback for simple js");
	    	
	    	return false;
	    }
	    @geogebra.common.main.App::debug(Ljava/lang/String;)("INIT: workers are supported");
	    	
	    worker.terminate();
	    return true;
	}-*/;
	
	public static native boolean checkIfFallbackSetExplicitlyInArrayBufferJs() /*-{
		if ($wnd.zip.useWebWorkers === false) {
			//we set this explicitly in arraybuffer.js
			@geogebra.common.main.App::debug(Ljava/lang/String;)("INIT: workers maybe supported, but fallback set explicitly in arraybuffer.js");
			return true;;
		}
		return false;
	}-*/;
	private static boolean float64supported = true;
	
	/**
	 * Checks whether browser supports float64. Must be called before a polyfill kicks in.
	 */
	public static void checkFloat64() {
	    float64supported = doCheckFloat64();
    }
	
	public static boolean isFloat64supported(){
		return float64supported;
	}
	
	private static native boolean doCheckFloat64()/*-{
		var floatSupport = 'undefined' !== typeof Float64Array;
		return 'undefined' !== typeof Float64Array;
	}-*/;

	public static native boolean supportsPointerEvents()/*-{
	    return window.navigator.msPointerEnabled ? true : false;
    }-*/;
}
