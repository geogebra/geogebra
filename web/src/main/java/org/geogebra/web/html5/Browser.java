package org.geogebra.web.html5;

import java.util.Locale;

import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.html5.webcam.WebCamAPI;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.Window.Navigator;

import elemental2.core.Global;
import elemental2.dom.DomGlobal;

public class Browser {
	public static final String ACTION_RESET_URL = "{\"action\": \"resetUrl\"}";
	private static boolean webWorkerSupported = false;
	private static Boolean webglSupported = null;

	/**
	 * UA string check, may not be reliable
	 * @return whether the app is running in Firefox
	 */
	public static boolean isFirefox() {
		return doesUserAgentContainRegex("firefox");
	}

	/**
	 * Check if browser is Internet Explorer
	 *
	 * (Note: only IE11 is supported now)
	 *
	 * @return true if IE
	 */
	public static boolean isIE() {
		// check if app is running in IE5 or greater
		return doesUserAgentContainRegex("msie |trident/");
	}

	private static boolean doesUserAgentContainRegex(String regex) {
		String userAgent = Navigator.getUserAgent().toLowerCase(Locale.US);
		return userAgent.matches(".*(" + regex + ").*");
	}

	/**
	 * Check if browser is Safari on iOS
	 *
	 * check isiOS() && isSafari() if you want just iOS browser & not webview
	 *
	 * (Note: returns true for Chrome on iOS as that's really an iOS Webview)
	 *
	 * @return true if iOS (WebView or Safari browser)
	 */
	public static boolean isiOS() {
		return doesUserAgentContainRegex("iphone|ipad|ipod")
				// only iPhones iPads and iPods support multitouch
				|| ("MacIntel".equals(Navigator.getPlatform()) && getMaxPointTouch() > 1);
	}

	private static native int getMaxPointTouch() /*-{
		return $wnd.navigator.maxTouchPoints;
	}-*/;

	/**
	 * Check if browser is Safari. Note: user agent string contains Safari also
	 * in Chrome =&gt; use vendor instead
	 *
	 * check isiOS() && isSafari() if you want just iOS browser & not webview
	 *
	 * @return true if Safari browser
	 */
	public static native boolean isSafariByVendor() /*-{
		return "Apple Computer, Inc." === $wnd.navigator.vendor;
	}-*/;

	public native static boolean externalCAS() /*-{
		return typeof $wnd.evalGeoGebraCASExternal == 'function'
				&& $wnd.evalGeoGebraCASExternal("1+1") == "2";
	}-*/;

	/**
	 * @param workerpath
	 *            JS folder with workers
	 * @return whether workers are supported
	 */
	public static boolean checkWorkerSupport(String workerpath) {
		if ("tablet".equals(GWT.getModuleName())
				|| "tabletWin".equals(GWT.getModuleName())) {
			return false;
		}
		return nativeCheckWorkerSupport(workerpath);
	}

	private static native boolean nativeCheckWorkerSupport(
			String workerpath) /*-{
		// Web workers are not supported in cross domain situations, and the
		// following check only correctly detects them in chrome, so this
		// condition must stay here until the end of times.
		if (navigator.userAgent.toLowerCase().indexOf("firefox") != -1
			|| navigator.userAgent.toLowerCase().indexOf("safari") != -1
			&& navigator.userAgent.toLowerCase().indexOf("chrome") == -1) {
			@org.geogebra.common.util.debug.Log::debug(Ljava/lang/String;)("INIT: workers might not be supported");
			return false;
		}

		try {
			var worker = new $wnd.Worker(workerpath+"js/workercheck.js");
		} catch (e) {
			@org.geogebra.common.util.debug.Log::debug(Ljava/lang/String;)("INIT: workers are not supported (no worker at " + workerpath + "), fallback for simple js");
			return false;
		}

		@org.geogebra.common.util.debug.Log::debug(Ljava/lang/String;)("INIT: workers are supported");
		worker.terminate();
		return true;
	}-*/;

	public static native boolean zipjsLoadedWithoutWebWorkers() /*-{
		return !!($wnd.zip && $wnd.zip.useWebWorkers === false);
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

	public static native boolean supportsPointerEvents() /*-{
		return !!$wnd.PointerEvent;
	}-*/;

	private static boolean isHTTP() {
		return !"file:".equals(Location.getProtocol());
	}

	/**
	 * Check this to avoid exceptions thrown from Storage.get*StorageIfSupported
	 * 
	 * @return whether session storage is supported
	 */
	public static boolean supportsSessionStorage() {
		return !Browser.isIE() || Browser.isHTTP();
	}

	/**
	 * @param thumb
	 *            original URL
	 * @return URL using appropriate protocol (data or https)
	 */
	public static String normalizeURL(String thumb) {
		if (thumb.startsWith("data:") || (thumb.startsWith("http://")
				&& "http:".equals(Location.getProtocol()))) {
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

	/**
	 * @return whether WebGL is supported
	 */
	public static boolean supportsWebGL() {
		if (webglSupported == null) {
			webglSupported = supportsWebGLNative();
		}
		return webglSupported.booleanValue();
	}

	/**
	 * For tests, circumvent the WebGL availability check
	 */
	public static void mockWebGL() {
		webglSupported = true;
	}

	/**
	 * Native check for WebGL support based on
	 * http://stackoverflow.com/questions/11871077/proper-way-to-detect-webgl-
	 * support
	 *
	 * @return whether WebGL is supported
	 */
	public static native boolean supportsWebGLNative()/*-{
		try {
			var canvas = $doc.createElement('canvas');
			var ret = !!$wnd.WebGLRenderingContext
					&& (canvas.getContext('webgl') || canvas
							.getContext('experimental-webgl'));
			return !!ret;
		} catch (e) {
			return false;
		}
	}-*/;

	/**
	 * @return whether TRIANGLE_FAN is supported in WebGL
	 */
	public static native boolean supportsWebGLTriangleFan()/*-{
		return $wnd.WebGLRenderingContext
				&& (!!$wnd.WebGLRenderingContext.TRIANGLE_FAN);
	}-*/;

	/**
	 * @return whether we are running on geogebra.org
	 */
	public static boolean isGeoGebraOrg() {
		String host = Location.getHost();
		return host != null && host.contains("geogebra.org");
	}

	/**
	 * @return right now GraspableMath is only enabled on geogebra.org and
	 * development hosts
	 */
	public static boolean isGraspableMathEnabled() {
		String host = Location.getHost();
		return host != null
				&& (host.contains("geogebra.org")
					|| host.contains("localhost")
					|| host.contains("apps-builds.s3-eu-central-1.amazonaws.com"));
	}

	public native static String navigatorLanguage() /*-{
		return $wnd.navigator.language || "en";
	}-*/;

	public static native boolean isAndroidVersionLessThan(double d) /*-{
		var navString = $wnd.navigator.userAgent.toLowerCase();
		if (navString.indexOf("android") < 0) {
			return false;
		}
		if (parseFloat(navString.substring(navString.indexOf("android") + 8)) < d) {
			return true;
		}
		return false;

	}-*/;

	/**
	 * @param parent
	 *            element to be scaled
	 * @param externalScale
	 *            scale
	 * @param x
	 *            origin x-coord in %
	 * @param y
	 *            origin y-coord in %
	 */
	public static void scale(Element parent, double externalScale, int x, int y) {
		if (externalScale < 0 || parent == null) {
			return;
		}

		if (isSafariByVendor()) {
			zoom(parent, externalScale);
			return;
		}

		String transform = "scale(" + externalScale + ")";
		parent.addClassName("ggbTransform");

		if (DoubleUtil.isEqual(externalScale, 1)) {
			transform = "none";
		}
		String pos = x + "% " + y + "%";

		Style style = parent.getStyle();
		if (style != null) {
			style.setProperty("transform", transform);
			style.setProperty("transformOrigin", pos);
		}
	}

	private static void zoom(Element parent, double externalScale) {
		Style style = parent.getStyle();
		if (style == null) {
			return;
		}
		style.setProperty("transform", "none");
		int zoomPercent = (int) Math.round(externalScale * 100);
		style.setProperty("zoom", zoomPercent + "%");
	}

	/**
	 * @return whether webcam input is supported in the browser
	 */
	public static boolean supportsWebcam() {
		return WebCamAPI.isSupported();
	}

	/**
	 * @return true if Javascript CAS is supported.
	 */
	public static boolean supportsJsCas() {
		return !Browser.isAndroidVersionLessThan(4.0);
	}

	/**
	 * @return whether app is running in a mobile browser
	 */
	public static boolean isMobile() {
		String browsers = "android|webos|blackberry|iemobile|opera mini";
		return doesUserAgentContainRegex(browsers) || isiOS();
	}

	/**
	 * @return CSS pixel ratio
	 */
	public static native double getPixelRatio() /*-{
		var testCanvas = $doc.createElement("canvas"), testCtx = testCanvas
				.getContext("2d");
		devicePixelRatio = $wnd.devicePixelRatio || 1;
		backingStorePixelRatio = testCtx.webkitBackingStorePixelRatio
				|| testCtx.mozBackingStorePixelRatio
				|| testCtx.msBackingStorePixelRatio
				|| testCtx.oBackingStorePixelRatio
				|| testCtx.backingStorePixelRatio || 1;
		return devicePixelRatio / backingStorePixelRatio;
	}-*/;

	/**
	 *
	 * Returns a base64 encoding of the specified (binary) string
	 *
	 * extra encoding needed for file from Generator: Adobe Illustrator 11.0,
	 * SVG Export Plug-In
	 *
	 * xpacket begin='\uFEFF'
	 *
	 * @param svg
	 *            A binary string (obtained for instance by the FileReader API)
	 * @return a base64 encoded string.
	 */
	public static String encodeSVG(String svg) {
		// can't use data:image/svg+xml;utf8 in IE11 / Edge
		// so encode as Base64
		return StringUtil.svgMarker + DomGlobal.btoa(
				Global.unescape(URL.encodePathSegment(svg)));
	}

	public static native String encodeURIComponent(String txt) /*-{
		return $wnd.encodeURIComponent(txt);
	}-*/;

	public static native void exportImage(String url, String title) /*-{
		//idea from http://stackoverflow.com/questions/16245767/creating-a-blob-from-a-base64-string-in-javascript/16245768#16245768

		var extension;
		var header;

		// IE11 doesn't have String.startsWith()
		var startsWith = function(data, input) {
			return data.substring(0, input.length) === input;
		}
		//global function in Chrome Kiosk App
		if (typeof $wnd.ggbExportFile == "function") {
			$wnd.ggbExportFile(url, title);
			return;
		}

		var base64encoded = true;

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
			base64encoded = false;
		} else if (startsWith(url,
				@org.geogebra.common.util.StringUtil::txtMarkerForSafari)) {
			extension = "application/octet-stream";
			header = @org.geogebra.common.util.StringUtil::txtMarkerForSafari;
			base64encoded = false;
		} else if (startsWith(url,
				@org.geogebra.common.util.StringUtil::htmlMarker)) {
			extension = "text/html";
			header = @org.geogebra.common.util.StringUtil::htmlMarker;
			base64encoded = false;
		} else {
			$wnd.console.log("unknown extension " + url.substring(0, 30));
			return;
		}

		// $wnd.android is set for Android, iOS, Win8
		// Yes, really!
		if ($wnd.android) {
			$wnd.android.share(url, title, extension);
			return;
		}

		// no downloading on iOS so just open image/file in new tab
		if (@org.geogebra.web.html5.Browser::isiOS()()) {
			@org.geogebra.web.html5.Browser::openWindow(Ljava/lang/String;)(url);
			return;
		}

		// Chrome limits to 2Mb so use Blob
		// https://stackoverflow.com/questions/695151/data-protocol-url-size-limitations/41755526#41755526
		// https://stackoverflow.com/questions/38781968/problems-downloading-big-filemax-15-mb-on-google-chrome/38845151#38845151

		// msSaveBlob: IE11, Edge
		if ($wnd.navigator.msSaveBlob
				|| $wnd.navigator.userAgent.toLowerCase().indexOf("chrome") > -1) {
			var sliceSize = 512;

			var byteCharacters = url.substring(header.length);

			if (base64encoded) {
				byteCharacters = $wnd.atob(byteCharacters);
			}

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

			if ($wnd.navigator.msSaveBlob) {
				// IE11, Edge
				$wnd.navigator.msSaveBlob(blob, title);
			} else {
				// Chrome
				var url2 = $wnd.URL.createObjectURL(blob);
				var a = $doc.createElement("a");
				a.download = title;
				a.href = url2;

				a.onclick = function() {
					requestAnimationFrame(function() {
						$wnd.URL.revokeObjectURL(url2);
					})
				};

				$wnd.setTimeout(function() {
					a.click()
				}, 10);
			}
		} else {
			@org.geogebra.web.html5.Browser::downloadDataURL(Ljava/lang/String;Ljava/lang/String;)(url, title);
		}

	}-*/;

	public static native void downloadDataURL(String url, String title) /*-{
		// Firefox, Safari
		var a = $doc.createElement("a");
		$doc.body.appendChild(a);
		a.style = "display: none";
		a.href = window.encodeURI(url);
		a.download = title;
		$wnd.setTimeout(function() {
			a.click()
		}, 10);
	}-*/;

	/**
	 * Change URL if we are running on geogebra.org
	 *
	 * @param string
	 *            new URL
	 */
	public static void changeUrl(String string) {
		if (isAppsServer() || string.startsWith("?")) {
			nativeChangeUrl(string);
		}
	}

	private static boolean isAppsServer() {
		String host = Location.getHost();
		return host != null
				&& (host.contains("geogebra.org") || host.equals("localhost"))
				&& !Location.getPath().contains(".html");
	}

	public static native void changeMetaTitle(String title) /*-{
		$wnd.changeMetaTitle && $wnd.changeMetaTitle(title);
	}-*/;

	private static native void nativeChangeUrl(String name) /*-{
		if (name && $wnd.history && $wnd.history.pushState) {
			try {
				$wnd.history.pushState({}, "GeoGebra", name);
			} catch (e) {
				// on dev server trying to push production URL
			}
		}
	}-*/;

	/**
	 * resets url to base: no materials or query string.
	 */
	public static void resetUrl() {
		DomGlobal.window.parent.postMessage(ACTION_RESET_URL, "*");
	}

	/**
	 * Opens GeoGebraTube material in a new window
	 *
	 * @param url
	 *            GeoGebraTube url
	 */
	public native static void openWindow(String url)/*-{
		$wnd.open(url, '_blank');
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
		return decodeURIComponent(escape($wnd.atob(base64)));
	}-*/;

	public static void removeDefaultContextMenu(Element element) {
		setAllowContextMenu(element, false);
	}

	/**
	 * Allow or diallow context menu for an element.
	 *
	 * @param element
	 *            element
	 * @param allow
	 *            whether to allow context menu
	 */
	public static native void setAllowContextMenu(Element element,
			boolean allow) /*-{
		if (element.addEventListener) {
			element.addEventListener("MSHoldVisual", function(e) {
				allow ? e.stopPropagation() : e.preventDefault();
			}, false);
			element.addEventListener('contextmenu', function(e) {
				allow ? e.stopPropagation() : e.preventDefault();
			}, false);
		}
	}-*/;

	public static native boolean isXWALK() /*-{
		return !!$wnd.ggbExamXWalkExtension;
	}-*/;

	public native static boolean isAndroid()/*-{
		var userAgent = $wnd.navigator.userAgent;
		if (userAgent) {
			return userAgent.indexOf("Android") != -1;
		}
		return false;
	}-*/;

	/**
	 * @deprecated you most likely want to use isiOS() instead
	 * @return check this is an iPad browser
	 */
	@Deprecated
	public native static boolean isIPad()/*-{
		var userAgent = $wnd.navigator.userAgent;
		if (userAgent) {
			return userAgent.indexOf("iPad") != -1;
		}
		return false;
	}-*/;

	/**
	 * it's true for android phones and iPads but false for iPhone
	 * 
	 * @deprected isMobile might work better, alternatively isiOS() ||
	 *            isAndroid()
	 * @return whether this is iPad or Android
	 */
	@Deprecated
	public static boolean isTabletBrowser() {
		return isAndroid() || isiOS();
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

	/**
	 * @param full
	 *            whether to go fullscreen
	 * @param element
	 *            element to be scaled
	 */
	public static native void toggleFullscreen(boolean full,
			JavaScriptObject element)/*-{
		var el = element || $doc.documentElement;
		if (full) { // current working methods
			if (el.requestFullscreen) {
				el.requestFullscreen();
			} else if ($doc.documentElement.msRequestFullscreen) {
				el.msRequestFullscreen();
			} else if ($doc.documentElement.mozRequestFullScreen) {
				el.mozRequestFullScreen();
			} else if ($doc.documentElement.webkitRequestFullScreen) {
				el.style.setProperty("width", "100%", "important");
				el.style.setProperty("height", "100%", "important");
				el.webkitRequestFullScreen();
				//Element.ALLOW_KEYBOARD_INPUT);
			}
		} else {
			if ($doc.exitFullscreen) {
				$doc.exitFullscreen();
			} else if ($doc.msExitFullscreen) {
				$doc.msExitFullscreen();
			} else if ($doc.mozCancelFullScreen) {
				$doc.mozCancelFullScreen();
			} else if ($doc.webkitCancelFullScreen) {
				$doc.webkitCancelFullScreen();
			}
		}
	}-*/;

	/**
	 * Register handler for fullscreen event.
	 *
	 * @param callback
	 *            callback for fullscreen event
	 */
	public static native void addFullscreenListener(
			AsyncOperation<String> callback) /*-{
		function listen(pfx, eventName) {
			$doc
					.addEventListener(
							eventName,
							function(e) {
								var fsElement = $doc[pfx + "FullscreenElement"];
								// mozFullScreen still needed for FF60 ESR
								var fsState = (fsElement
										|| $doc.fullscreenElement || $doc.mozFullScreen) ? "true"
										: "false";
								callback.@org.geogebra.common.util.AsyncOperation::callback(*)(fsState);
							});
		}

		if (typeof document.onfullscreenchange === "undefined") {
			listen("webkit", "webkitfullscreenchange");
			listen("ms", "MSFullscreenChange");
			listen("moz", "mozfullscreenchange");
		} else {
			listen("", "fullscreenchange");
		}

	}-*/;

	/**
	 * @return whether current window covers whole screen
	 */
	public static native boolean isCoveringWholeScreen()/*-{
		var height = $wnd.innerHeight;
		var width = $wnd.innerWidth;

		var screenHeight = screen.height - 5;
		var screenWidth = screen.width - 5;

		//$wnd.console.log("height: " + height, screenHeight);
		//$wnd.console.log("width: " + width, screenWidth);

		return height >= screenHeight && width >= screenWidth;
	}-*/;

	/**
	 * Add mutation observer to element and all its parents.
	 *
	 * @param el
	 *            target element
	 * @param asyncOperation
	 *            callback
	 */
	public static native void addMutationObserver(Element el,
			AsyncOperation<String> asyncOperation) /*-{
		try {
			var current = el;
			while (current) {
				var observer = new MutationObserver(
						function(mutations) {
							mutations
									.forEach(function(mutation) {
										asyncOperation.@org.geogebra.common.util.AsyncOperation::callback(*)(mutation.type);
									});
						});
				observer.observe(current, {
					attributes : true,
					attributeFilter : [ "class", "style" ]
				});
				current = current.parentElement;
			}
		} catch (ex) {
			//Mutation observer not supported
		}
	}-*/;

	/**
	 * gets keycodes of iOS arrow keys iOS arrows have a different identifier
	 * than win and android
	 *
	 * @param event
	 *            native key event
	 * @return JavaKeyCodes of arrow keys, -1 if pressed key was not an arrow
	 */
	public native static int getIOSArrowKeys(NativeEvent event) /*-{

		var key = event.key;
		@org.geogebra.common.util.debug.Log::debug(Ljava/lang/String;)("KeyDownEvent: " + key);
		switch (key) {
		case "UIKeyInputUpArrow":
			return @com.himamis.retex.editor.share.util.GWTKeycodes::KEY_UP;
		case "UIKeyInputDownArrow":
			return @com.himamis.retex.editor.share.util.GWTKeycodes::KEY_DOWN;
		case "UIKeyInputLeftArrow":
			return @com.himamis.retex.editor.share.util.GWTKeycodes::KEY_LEFT;
		case "UIKeyInputRightArrow":
			return @com.himamis.retex.editor.share.util.GWTKeycodes::KEY_RIGHT;
		default:
			return -1;
		}
	}-*/;

	/**
	 * @return whether current browser is Chrome
	 */
	public static boolean isChrome() {
		// yep, Edge UA string contains Chrome too
		return Navigator.getUserAgent().matches(".*Chrome/.*") && !isEdge();
	}

	/**
	 * @return whether we're running in a Mac browser
	 */
	public static boolean isMacOS() {
		return Navigator.getUserAgent().contains("Macintosh")
				|| Navigator.getUserAgent().contains("Mac OS");
	}

	/**
	 * @param txt
	 *            plain text file in UTF-8
	 * @return valid data URL, browser dependent
	 */
	public static String addTxtMarker(String txt) {
		return isiOS() && isSafariByVendor()
				? StringUtil.txtMarkerForSafari + encodeURIComponent(txt)
				: StringUtil.txtMarker + txt;
	}

	/**
	 * Checks for screen readers that don't support keyboard event handling in
	 * canvas.
	 * 
	 * @return whether emulator of tab handler is needed
	 */
	public static boolean needsAccessibilityView() {
		return isMobile();
	}
}
