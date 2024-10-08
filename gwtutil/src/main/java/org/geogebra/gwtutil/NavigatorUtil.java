package org.geogebra.gwtutil;

import java.util.Locale;

import com.himamis.retex.editor.share.util.KeyCodes;

import elemental2.dom.DomGlobal;
import elemental2.dom.URLSearchParams;
import jsinterop.base.Js;

public class NavigatorUtil {

	/**
	 * @return whether app is running in a mobile browser
	 */
	public static boolean isMobile() {
		String browsers = "android|webos|blackberry|iemobile|opera mini";
		return doesUserAgentContainRegex(browsers) || isiOS();
	}

	/**
	 * UA string check, may not be reliable
	 * @return whether the app is running in Firefox
	 */
	public static boolean isFirefox() {
		return doesUserAgentContainRegex("firefox");
	}

	/**
	 * Check if browser is Internet Explorer (not supported)
	 * @return true if IE
	 */
	public static boolean isIE() {
		// check if app is running in IE5 or greater
		return doesUserAgentContainRegex("msie |trident/");
	}

	/**
	 * Check if browser is Safari on iOS
	 *
	 * check isiOS() &amp;&amp; isSafari() if you want just iOS browser and not webview
	 *
	 * (Note: returns true for Chrome on iOS as that's really an iOS Webview)
	 *
	 * @return true if iOS (WebView or Safari browser)
	 */
	public static boolean isiOS() {
		return doesUserAgentContainRegex("iphone|ipad|ipod")
				// only iPhones iPads and iPods support multitouch
				|| ("MacIntel".equals(DomGlobal.navigator.platform) && getMaxPointTouch() > 1);
	}

	private static boolean doesUserAgentContainRegex(String regex) {
		String userAgent = DomGlobal.navigator.userAgent.toLowerCase(Locale.US);
		return userAgent.matches(".*(" + regex + ").*");
	}

	private static int getMaxPointTouch() {
		Object touchPoints =  Js.asPropertyMap(DomGlobal.navigator).get("maxTouchPoints");
		return touchPoints == null ? 0 : Js.asInt(touchPoints);
	}

	/**
	 * @return whether we're running in a Mac browser
	 */
	public static boolean isMacOS() {
		return DomGlobal.navigator.userAgent.contains("Macintosh")
				|| DomGlobal.navigator.userAgent.contains("Mac OS");
	}

	/**
	 * @param name parameter name
	 * @return parameter value; null if not present
	 */
	public static String getUrlParameter(String name) {
		if ("".equals(DomGlobal.location.search)) {
			return null;
		}
		return new URLSearchParams(DomGlobal.location.search).get(name);
	}

	public static int getWindowWidth() {
		return DomGlobal.document.documentElement.clientWidth;
	}

	public static int getWindowHeight() {
		return DomGlobal.document.documentElement.clientHeight;
	}

	public static int getWindowScrollLeft() {
		return (int) DomGlobal.document.documentElement.scrollLeft;
	}

	public static int getWindowScrollTop() {
		return (int) DomGlobal.document.documentElement.scrollTop;
	}

	/**
	 * @param gwtKeyCode native key code
	 * @return KeyCodes wrapper
	 */
	public static KeyCodes translateGWTcode(int gwtKeyCode) {
		// Special case for Mac: Translate Context Menu Key (93) to Meta key
		if (gwtKeyCode == 93 && isMacOS()) {
			return KeyCodes.META;
		}
		for (KeyCodes l : KeyCodes.values()) {
			if (l.getGWTKeyCode() == gwtKeyCode) {
				return l;
			}
		}
		return KeyCodes.UNKNOWN;
	}
}
