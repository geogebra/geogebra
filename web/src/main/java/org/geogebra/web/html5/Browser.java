package org.geogebra.web.html5;

import org.geogebra.common.util.DoubleUtil;
import org.geogebra.web.html5.main.StringHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Window.Location;

public class Browser {
	private static boolean webWorkerSupported = false;
	private static boolean float64supported = true;
	private static Boolean webglSupported = null;

	public static native boolean isFirefox() /*-{
		// copying checking code from the checkWorkerSupport method
		// however, this is not necessarily the best method to decide
		if (navigator.userAgent.toLowerCase().indexOf("firefox") != -1) {
			return true;
		}
		return false;
	}-*/;

	/**
	 * Check if browser is Internet Explorer
	 * 
	 * (Note: only IE11 is supported now)
	 * 
	 * @return true if IE
	 */
	public static native boolean isIE() /*-{
		// check if app is running in IE5 or greater
		// clipboardData object is available from IE5 and onwards
		var userAgent = $wnd.navigator.userAgent.toLowerCase();
		if ((userAgent.indexOf('msie ') > -1)
				|| (userAgent.indexOf('trident/') > -1)) {
			return true;
		}
		return false;
	}-*/;

	public native static boolean externalCAS() /*-{
		return typeof $wnd.evalGeoGebraCASExternal == 'function'
				&& $wnd.evalGeoGebraCASExternal("1+1") == "2";
	}-*/;

	public static boolean checkWorkerSupport(String workerpath) {
		if ("tablet".equals(GWT.getModuleName())
				|| "tabletWin".equals(GWT.getModuleName())) {
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
		if ($wnd.zip && $wnd.zip.useWebWorkers === false) {
			//we set this explicitly in arraybuffer.js
			@org.geogebra.common.util.debug.Log::debug(Ljava/lang/String;)("INIT: workers maybe supported, but fallback set explicitly in arraybuffer.js");
			return true;
		}
		return false;
	}-*/;

	/**
	 * @return whether we are running under iOS
	 */
	public static native String getMobileOperatingSystem()/*-{
		var userAgent = $wnd.navigator.userAgent;

		//iOS detection from: http://stackoverflow.com/a/9039885/177710
		if (/Mac|iPad|iPhone|iPod/.test(userAgent) && !$wnd.MSStream) {
			return "iOS";
		}
		return "unknown";
	}-*/;

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

	/**
	 * 
	 * @return true if WebAssembly supported
	 */
	public static native boolean webAssemblySupported()/*-{

		// currently iOS11 giac.wasm gives slightly wrong results
		// eg Numeric(fractionalPart(2.7)) gives 0.6999999999999 rather than 0.7
		var iOS = /iPad|iPhone|iPod/.test($wnd.navigator.userAgent)
				&& !$wnd.MSStream;

		return !iOS && !!$wnd.WebAssembly;
	}-*/;

	public static native boolean supportsPointerEvents(boolean usePen)/*-{
		//$wnd.console.log("PEN SUPPORT" + usePen + "," + (!!$wnd.PointerEvent));
		if (usePen && $wnd.PointerEvent) {
			return true;
		}
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

	public static boolean supportsWebGL() {
		if (webglSupported == null) {
			webglSupported = supportsWebGLNative();
		}
		return webglSupported.booleanValue();
	}

	public static void mockWebGL() {
		webglSupported = true;
	}

	/**
	 * Native check for WebGL support based on
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

		if (DoubleUtil.isEqual(externalScale, 1)) {
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

	public static native double getPixelRatio() /*-{
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

	public static native String encodeSVG(String svg) /*-{
		// can't use data:image/svg+xml;utf8 in IE11 / Edge
		// so encode as Base64
		return @org.geogebra.common.util.StringUtil::svgMarker
				+ btoa(unescape(encodeURIComponent(svg)));
	}-*/;
		

	public static native void exportImage(String url, String title) /*-{
		//idea from http://stackoverflow.com/questions/16245767/creating-a-blob-from-a-base64-string-in-javascript/16245768#16245768

		var extension;
		var header;

		// IE11 doesn't have String.startsWith()
		var startsWith = function(data, input) {
			return data.substring(0, input.length) === input;
		}

		if (startsWith(url, @org.geogebra.common.util.StringUtil::pngMarker)) {
			extension = "image/png";
			header = @org.geogebra.common.util.StringUtil::pngMarker;
		} else if (startsWith(url,
				@org.geogebra.common.util.StringUtil::svgMarker)) {
			extension = "image/svg+xml";
			header = @org.geogebra.common.util.StringUtil::svgMarker;
		} else if (startsWith(url,
				@org.geogebra.common.util.StringUtil::gifMarker)) {
			extension = "image/gif";
			header = @org.geogebra.common.util.StringUtil::gifMarker;
		} else if (startsWith(url,
				@org.geogebra.common.util.StringUtil::pdfMarker)) {
			extension = "application/pdf";
			header = @org.geogebra.common.util.StringUtil::pdfMarker;
		} else if (startsWith(url,
				@org.geogebra.common.util.StringUtil::txtMarker)) {
			extension = "text/plain";
			header = @org.geogebra.common.util.StringUtil::txtMarker;
		} else {
			$wnd.console.log("unknown extension " + url.substring(0, 20));
			return;
		}

		// 

		if ($wnd.navigator.msSaveBlob) {
			var sliceSize = 512;

			var byteCharacters = atob(url.substring(header.length));
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
				type : extension
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
		if ((Location.getHost() != null
				&& Location.getHost().contains("geogebra.org")
				&& !Location.getHost().contains("autotest"))
				|| string.startsWith("#")) {
			nativeChangeUrl(string);
		}
	}

	public static native void changeMetaTitle(String title) /*-{
		$wnd.changeMetaTitle && $wnd.changeMetaTitle(title);
	}-*/;

	private static native void nativeChangeUrl(String name) /*-{
		if (name && $wnd.history && $wnd.history.pushState) {
			try {
				$wnd.parent.history.pushState({}, "GeoGebra", name);
			} catch (e) {
				$wnd.history.pushState({}, "GeoGebra", name);
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
		if (element.addEventListener) {
			element.addEventListener("MSHoldVisual", function(e) {
				e.preventDefault();
			}, false);
			element.addEventListener('contextmenu', function(e) {
				e.preventDefault();
			}, false);
		}
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

	public static native int getScreenWidth() /*-{
		return $wnd.screen.width;
	}-*/;

	public static native int getScreenHeight() /*-{
		return $wnd.screen.height;
	}-*/;

	public static native boolean isEdge() /*-{
		return $wnd.navigator.userAgent.indexOf("Edge") > -1;
	}-*/;

	public static native void toggleFullscreen(boolean full,
			JavaScriptObject element)/*-{
		var el = element || $doc.documentElement;
		if (full) { // current working methods
			if (el.requestFullscreen) {
				el.requestFullscreen();
			} else if (document.documentElement.msRequestFullscreen) {
				el.msRequestFullscreen();
			} else if (document.documentElement.mozRequestFullScreen) {
				el.mozRequestFullScreen();
			} else if (document.documentElement.webkitRequestFullScreen) {
				el.style.setProperty("width", "100%", "important");
				el.style.setProperty("height", "100%", "important");
				el.webkitRequestFullScreen();
				//Element.ALLOW_KEYBOARD_INPUT);
			}
		} else {
			if ($doc.exitFullscreen) {
				$doc.exitFullscreen();
			} else if (document.msExitFullscreen) {
				$doc.msExitFullscreen();
			} else if (document.mozCancelFullScreen) {
				$doc.mozCancelFullScreen();
			} else if (document.webkitCancelFullScreen) {
				$doc.webkitCancelFullScreen();
			}
		}
	}-*/;

	public static native void addFullscreenListener(
			StringHandler callback) /*-{
		var prefixes = [ "webkit", "ms", "moz" ];
		for ( var i in prefixes) {
			var prefix = prefixes[i];
			$doc
					.addEventListener(
							prefix + "fullscreenchange",
							(function(pfx) {
								return function(e) {
									callback.@org.geogebra.web.html5.main.StringHandler::handle(Ljava/lang/String;)(($doc[pfx+"FullscreenElement"] || $doc.mozFullScreen) ?  "true" : "false");
								}
							})(prefix));
		}

	}-*/;

	public static native boolean isCoveringWholeScreen()/*-{
		var height = $wnd.innerHeight;
		var width = $wnd.innerWidth;

		var screenHeight = screen.height - 5;
		var screenWidth = screen.width - 5;

		//$wnd.console.log("height: " + height, screenHeight);
		//$wnd.console.log("width: " + width, screenWidth);

		return height >= screenHeight && width >= screenWidth;
	}-*/;

}
