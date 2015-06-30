package org.geogebra.web.html5;

import org.geogebra.common.kernel.Kernel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Window.Location;

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

	public static native boolean isIE9() /*-{
		// copying checking code from isFirefox() and checked from web
		// however, this is not necessarily the best method to decide
		if (navigator.userAgent.toLowerCase().indexOf("msie") > -1
				&& navigator.userAgent.toLowerCase().indexOf("9.0") > -1) {
			return true;
		}
		return false;
	}-*/;

	/**
	 * Better solution, copied from CopyPasteCutW originally TODO: add
	 * toLowerCase() call to it!
	 * 
	 * @return
	 */
	public static native boolean isInternetExplorer() /*-{
		// check if app is running in IE5 or greater
		// clipboardData object is available from IE5 and onwards
		var userAgent = $wnd.navigator.userAgent;
		if ((userAgent.indexOf('MSIE ') > -1)
				|| (userAgent.indexOf('Trident/') > -1)) {
			return true;
		}
		return false;
	}-*/;

	public native static boolean externalCAS() /*-{
		return typeof $wnd.evalGeoGebraCASExternal == 'function';
	}-*/;

	public static boolean checkWorkerSupport(String workerpath) {
		if ("tablet".equals(GWT.getModuleName())
		        || "phone".equals(GWT.getModuleName())) {
			return false;
		}
		return nativeCheckWorkerSupport(workerpath);
	}

	public static native boolean nativeCheckWorkerSupport(String workerpath) /*-{
		// Worker support in Firefox is incompatible at the moment for zip.js,
		// see http://gildas-lormeau.github.com/zip.js/ for details:
		if (navigator.userAgent.toLowerCase().indexOf("firefox") != -1) {
			@org.geogebra.common.main.App::debug(Ljava/lang/String;)("INIT: worker not supported in Firefox, fallback for simple js");
			return false;
		}
		if (navigator.userAgent.toLowerCase().indexOf("safari") != -1
			&& navigator.userAgent.toLowerCase().indexOf("chrome") == -1) {
			@org.geogebra.common.main.App::debug(Ljava/lang/String;)("INIT: worker not supported in Safari, fallback for simple js");
			return false;
		}
		
	    try {
	    	var worker = new $wnd.Worker(workerpath+"js/workercheck.js");
	    } catch (e) {
	    	@org.geogebra.common.main.App::debug(Ljava/lang/String;)("INIT: worker not supported (no worker at " + workerpath + "), fallback for simple js");
	    	
	    	return false;
	    }
	    @org.geogebra.common.main.App::debug(Ljava/lang/String;)("INIT: workers are supported");
	    	
	    worker.terminate();
	    return true;
	}-*/;

	public static native boolean checkIfFallbackSetExplicitlyInArrayBufferJs() /*-{
		if ($wnd.zip.useWebWorkers === false) {
			//we set this explicitly in arraybuffer.js
			@org.geogebra.common.main.App::debug(Ljava/lang/String;)("INIT: workers maybe supported, but fallback set explicitly in arraybuffer.js");
			return true;
			;
		}
		return false;
	}-*/;

	private static boolean float64supported = true;

	/**
	 * Checks whether browser supports float64. Must be called before a polyfill
	 * kicks in.
	 */
	public static void checkFloat64() {
		float64supported = doCheckFloat64();
	}

	public static boolean isFloat64supported() {
		return float64supported;
	}

	private static native boolean doCheckFloat64()/*-{
		var floatSupport = 'undefined' !== typeof Float64Array;
		return 'undefined' !== typeof Float64Array;
	}-*/;

	public static native boolean supportsPointerEvents()/*-{
		return $wnd.navigator.msPointerEnabled ? true : false;
	}-*/;

	private static native boolean isHTTP() /*-{
		return $wnd.location.protocol != 'file:';
	}-*/;

	public static boolean supportsSessionStorage() {
		return (!Browser.isFirefox() && !Browser.isIE()) || Browser.isHTTP();
	}

	public static String normalizeURL(String thumb) {
		if (thumb.startsWith("data:")) {
			return thumb;
		}
		String url;
		if (thumb.startsWith("http://") || thumb.startsWith("file://")) {
			url = thumb.substring("http://".length());
		} else if (thumb.startsWith("https://")) {
			url = thumb.substring("https://".length());
		} else if (thumb.startsWith("//")) {
			url = thumb.substring("//".length());
		} else {
			url = thumb;
		}
		if ("https:".equals(Location.getProtocol())) {
			return "https://" + url;
		}
		return "http://" + url;
	}

	private static Boolean webglSupported = null;

	public static boolean supportsWebGL() {
		if (webglSupported == null) {
			webglSupported = supportsWebGLNative();
		}
		return webglSupported.booleanValue();
	}

	/*
	 * http://stackoverflow.com/questions/11871077/proper-way-to-detect-webgl-
	 * support
	 */
	public static native boolean supportsWebGLNative()/*-{
		try {
			var canvas = $wnd.document.createElement('canvas');
			var ret = !!$wnd.WebGLRenderingContext
					&& (canvas.getContext('webgl') || canvas
							.getContext('experimental-webgl'));
			return !!ret;
		} catch (e) {
			return false;
		}
	}-*/;

	public static native boolean supportsWebGLTriangleFan()/*-{
		return $wnd.WebGLRenderingContext
				&& (!!$wnd.WebGLRenderingContext.TRIANGLE_FAN);
	}-*/;

	/**
	 * @return whether we are running this from our own website
	 */
	public static boolean runningLocal() {
		return Location.getProtocol().startsWith("http")
		        && !Location.getHost().contains("geogebra.org")
		        && !Location.getHost().contains("geogebratube.org");
	}

	public native static String navigatorLanguage() /*-{
		return $wnd.navigator.language || "en";
	}-*/;

	public static native boolean isAndroidVersionLessThan(double d) /*-{
		var navString = navigator.userAgent.toLowerCase();
		if (navString.indexOf("android") < 0) {
			return false;
		}
		if (parseFloat(navString.substring(navString.indexOf("android") + 8)) < d) {
			return true;
		}
		return false;

	}-*/;

	/**
	 * Returns a base64 encoding of the specified binary string
	 * 
	 * @param str
	 *            A binary string (obtained for instance by the FileReader API)
	 * @return a base64 encoded string.
	 */
	public static native String base64encode(String str) /*-{
		return $wnd.btoa(str);
	}-*/;

	public static void scale(Element parent, double externalScale, int x, int y) {
		if (externalScale < 0) {
			return;
		}

		String transform = "scale(" + externalScale + "," + externalScale + ")";

		if (Kernel.isEqual(externalScale, 1)) {
			transform = "none";
		}
		String pos = x + "% " + y + "%";
		parent.getStyle().setProperty("webkitTransform", transform);
		parent.getStyle().setProperty("mozTransform", transform);
		parent.getStyle().setProperty("msTransform", transform);
		parent.getStyle().setProperty("transform", transform);
		parent.getStyle().setProperty("msTransformOrigin", pos);
		parent.getStyle().setProperty("mozTransformOrigin", pos);
		parent.getStyle().setProperty("webkitTransformOrigin", pos);
		parent.getStyle().setProperty("transformOrigin", pos);

	}

	public static native boolean supportsWebcam() /*-{
		if ($wnd.navigator.getUserMedia || $wnd.navigator.webkitGetUserMedia
				|| $wnd.navigator.mozGetUserMedia
				|| $wnd.navigator.msGetUserMedia) {
			return true;
		}
		return false;
	}-*/;

	/**
	 * @return true if Javascript CAS is supported.
	 */
	public static boolean supportsJsCas() {
		return Browser.isFloat64supported()
				&& !Browser.isAndroidVersionLessThan(4.0);
	}

	public native static boolean isIE10plus() /*-{
		return !!$wnd.MSBlobBuilder;
	}-*/;

	public static native float getPixelRatio() /*-{
		// TODO Auto-generated method stub
		var testCanvas = document.createElement("canvas"), testCtx = testCanvas
				.getContext("2d");
		devicePixelRatio = window.devicePixelRatio || 1;
		backingStorePixelRatio = testCtx.webkitBackingStorePixelRatio
				|| testCtx.mozBackingStorePixelRatio
				|| testCtx.msBackingStorePixelRatio
				|| testCtx.oBackingStorePixelRatio
				|| testCtx.backingStorePixelRatio || 1;
		return devicePixelRatio / backingStorePixelRatio;
	}-*/;
}
