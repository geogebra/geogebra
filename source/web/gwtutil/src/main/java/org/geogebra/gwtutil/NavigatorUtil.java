/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.gwtutil;

import java.util.Locale;

import org.geogebra.editor.share.util.KeyCodes;

import elemental2.dom.DomGlobal;
import elemental2.dom.URLSearchParams;

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
		return DomGlobal.navigator.maxTouchPoints;
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
