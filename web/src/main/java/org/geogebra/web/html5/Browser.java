package org.geogebra.web.html5;

import org.geogebra.common.kernel.Kernel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Window.Location;

public class Browser {
	private static boolean webWorkerSupported = false;

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
			@org.geogebra.common.util.debug.Log::debug(Ljava/lang/String;)("INIT: worker not supported in Firefox, fallback for simple js");
			return false;
		}
		if (navigator.userAgent.toLowerCase().indexOf("safari") != -1
			&& navigator.userAgent.toLowerCase().indexOf("chrome") == -1) {
			@org.geogebra.common.util.debug.Log::debug(Ljava/lang/String;)("INIT: worker not supported in Safari, fallback for simple js");
			return false;
		}
		
	    try {
	    	var worker = new $wnd.Worker(workerpath+"js/workercheck.js");
	    } catch (e) {
	    	@org.geogebra.common.util.debug.Log::debug(Ljava/lang/String;)("INIT: worker not supported (no worker at " + workerpath + "), fallback for simple js");
	    	
	    	return false;
	    }
	    @org.geogebra.common.util.debug.Log::debug(Ljava/lang/String;)("INIT: workers are supported");
	    	
	    worker.terminate();
	    return true;
	}-*/;

	public static native boolean checkIfFallbackSetExplicitlyInArrayBufferJs() /*-{
		if ($wnd.zip.useWebWorkers === false) {
			//we set this explicitly in arraybuffer.js
			@org.geogebra.common.util.debug.Log::debug(Ljava/lang/String;)("INIT: workers maybe supported, but fallback set explicitly in arraybuffer.js");
			return true;
			;
		}
		return false;
	}-*/;

	/**
	 * @return whether we are running under iOS
	 */
	public static native String getMobileOperatingSystem()/*-{
		var userAgent = navigator.userAgent || navigator.vendor || window.opera;

		//iOS detection from: http://sackoverflow.com/a/9039885/177710
		if (/Mac|iPad|iPhone|iPod/.test(userAgent) && !window.MSStream) {
			return "iOS";
		}
		return "unknown";
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
		return !Browser.isIE() || Browser.isHTTP();
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

		return "https://" + url;

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
	 * @return whether we are running this from another website (local install
	 *         of app bundle)
	 */
	public static boolean runningLocal() {
		return Location.getProtocol().startsWith("http")
				&& Location.getHost() != null
				&& !Location.getHost().contains("geogebra.org");
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

	public static native boolean isMobile()/*-{
		return !!(/Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i
				.test($wnd.navigator.userAgent));
	}-*/;

	public native static boolean isIE10plus() /*-{
		return !!$wnd.MSBlobBuilder;
	}-*/;

	public static native float getPixelRatio() /*-{
		var testCanvas = document.createElement("canvas"), testCtx = testCanvas
				.getContext("2d");
		devicePixelRatio = $wnd.devicePixelRatio || 1;
		backingStorePixelRatio = testCtx.webkitBackingStorePixelRatio
				|| testCtx.mozBackingStorePixelRatio
				|| testCtx.msBackingStorePixelRatio
				|| testCtx.oBackingStorePixelRatio
				|| testCtx.backingStorePixelRatio || 1;
		return devicePixelRatio / backingStorePixelRatio;
	}-*/;

	public static native void exportImage(String url, String title) /*-{
		//idea from http://stackoverflow.com/questions/16245767/creating-a-blob-from-a-base64-string-in-javascript/16245768#16245768

		if ($wnd.navigator.msSaveBlob) {
			var sliceSize = 512;

			var byteCharacters = atob(url
					.substring("data:image/png;base64,".length));
			var byteArrays = [];

			for (var offset = 0; offset < byteCharacters.length; offset += sliceSize) {
				var slice = byteCharacters.slice(offset, offset + sliceSize);

				var byteNumbers = new Array(slice.length);
				for (var i = 0; i < slice.length; i++) {
					byteNumbers[i] = slice.charCodeAt(i);
				}

				var byteArray = new Uint8Array(byteNumbers);

				byteArrays.push(byteArray);
			}

			var blob = new Blob(byteArrays, {
				type : "image/png"
			});

			//works for internet explorer

			$wnd.navigator.msSaveBlob(blob, title);
		} else {
			//works for firefox
			var a = $doc.createElement("a");
			$doc.body.appendChild(a);
			a.style = "display: none";
			a.href = url;
			a.download = title;
			$wnd.setTimeout(function() {
				a.click()
			}, 1000);
		}

	}-*/;

	public static void changeUrl(String string) {
		if (Location.getHost() != null
				&& Location.getHost().contains("geogebra.org")) {
			nativeChangeUrl(string);
		}

	}

	private static native void nativeChangeUrl(String name) /*-{
		if (name && $wnd.history && $wnd.history.pushState) {
			try {
				$wnd.parent.history.pushState({}, "GeoGebra", "/" + name);
			} catch (e) {
				$wnd.history.pushState({}, "GeoGebra", "/" + name);
			}
		}
	}-*/;

	/**
	 * Opens GeoGebraTube material in a new window
	 * 
	 * @param url
	 *            GeoGebraTube url
	 */
	public native static void openTubeWindow(String url)/*-{
		$wnd.open(url);
	}-*/;

	/**
	 * Returns a string based on base 64 encoded value
	 * 
	 * @param base64
	 *            a base64 encoded string
	 * 
	 * @return decoded string
	 */
	public static native String decodeBase64(String base64)/*-{
		return atob(base64);
	}-*/;

	/**
	 * 
	 * Returns a base64 encoding of the specified (binary) string
	 * 
	 * @param text
	 *            A binary string (obtained for instance by the FileReader API)
	 * @return a base64 encoded string.
	 */
	public static native String encodeBase64(String text)/*-{
		return btoa(text);
	}-*/;

	public static native void removeDefaultContextMenu(Element element) /*-{

		function eventOnElement(e) {

			x1 = @org.geogebra.web.html5.main.AppW::getAbsoluteLeft(Lcom/google/gwt/dom/client/Element;)(element);
			x2 = @org.geogebra.web.html5.main.AppW::getAbsoluteRight(Lcom/google/gwt/dom/client/Element;)(element);
			y1 = @org.geogebra.web.html5.main.AppW::getAbsoluteTop(Lcom/google/gwt/dom/client/Element;)(element);
			y2 = @org.geogebra.web.html5.main.AppW::getAbsoluteBottom(Lcom/google/gwt/dom/client/Element;)(element);

			if ((e.pageX < x1) || (e.pageX > x2) || (e.pageY < y1)
					|| (e.pageY > y2)) {
				return false;
			}
			return true;
		}

		if ($doc.addEventListener) {
			element.addEventListener("MSHoldVisual", function(e) {
				e.preventDefault();
			}, false);
			$doc.addEventListener('contextmenu', function(e) {
				if (eventOnElement(e))
					e.preventDefault();
			}, false);
		} else {
			$doc.attachEvent('oncontextmenu', function() {
				if (eventOnElement(e))
					$wnd.event.returnValue = false;
			});
		}
	}-*/;

	public static native void removeDefaultContextMenu() /*-{

		if ($doc.addEventListener) {
			$doc.addEventListener('contextmenu', function(e) {
				e.preventDefault();
			}, false);
			$doc.addEventListener("MSHoldVisual", function(e) {
				e.preventDefault();
			}, false);

		} else {
			$doc.attachEvent('oncontextmenu', function() {
				$wnd.event.returnValue = false;
			});
		}
	}-*/;

	public static native boolean isChromeWebApp() /*-{
		if ($doc.isChromeWebapp()) {
			return true;
		}
		return false;
	}-*/;

	public static native boolean isXWALK() /*-{
		return !!$wnd.ggbExamXWalkExtension;
	}-*/;

	public native static boolean isAndroid()/*-{
		var userAgent = navigator.userAgent;
		if (userAgent) {
			return navigator.userAgent.indexOf("Android") != -1;
		}
		return false;
	}-*/;

	public native static boolean isIPad()/*-{
		var userAgent = navigator.userAgent;
		if (userAgent) {
			return navigator.userAgent.indexOf("iPad") != -1;
		}
		return false;
	}-*/;

	public static boolean isTabletBrowser() {
		return (isAndroid() || isIPad());
	}

	public static void setWebWorkerSupported(boolean b) {
		webWorkerSupported = b;
	}

	public static boolean webWorkerSupported() {
		return webWorkerSupported;
	}

}
