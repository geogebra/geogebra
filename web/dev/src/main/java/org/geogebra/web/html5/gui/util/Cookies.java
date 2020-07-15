package org.geogebra.web.html5.gui.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import elemental2.core.Global;
import elemental2.dom.DomGlobal;

/**
 * Provides access to browser cookies stored on the client. Because of browser
 * restrictions, you will only be able to access cookies associated with the
 * current page's domain.
 */
public class Cookies {

	/**
	 * Cached copy of cookies.
	 */
	static HashMap<String, String> cachedCookies = null;

	/**
	 * Raw cookie string stored to allow cached cookies to be invalidated on
	 * write.
	 */
	static String rawCookies;

	private Cookies() {
		// utility class
	}

	/**
	 * Gets the cookie associated with the given name.
	 *
	 * @param name the name of the cookie to be retrieved
	 * @return the cookie's value, or <code>null</code> if the cookie doesn't
	 *         exist
	 */
	public static String getCookie(String name) {
		Map<String, String> cookiesMap = ensureCookies();
		return cookiesMap.get(name);
	}

	/**
	 * Removes the cookie associated with the given name.
	 *
	 * @param name the name of the cookie to be removed
	 */
	public static void removeCookie(String name) {
		DomGlobal.document.cookie = Global.encodeURIComponent(name)
				+ "=;expires=Fri, 02-Jan-1970 00:00:00 GMT";
	}

	/**
	 * Removes the cookie associated with the given name.
	 *
	 * @param name the name of the cookie to be removed
	 * @param path the path to be associated with this cookie (which should match
	 *          the path given in {@link #setCookie})
	 */
	public static void removeCookie(String name, String path) {
		DomGlobal.document.cookie = Global.encodeURIComponent(name)
				+ "=;path="
				+ path
				+ ";expires=Fri, 02-Jan-1970 00:00:00 GMT";
	}

	/**
	 * Sets a cookie. The cookie will expire when the current browser session is
	 * ended.
	 *
	 * @param name the cookie's name
	 * @param value the cookie's value
	 */
	public static void setCookie(String name, String value) {
		setCookie(name, value, null, null, null);
	}

	/**
	 * Sets a cookie.
	 *
	 * @param name the cookie's name
	 * @param value the cookie's value
	 * @param expires when the cookie expires
	 */
	public static void setCookie(String name, String value, Date expires) {
		setCookie(name, value, expires, null, null);
	}

	/**
	 * Sets a cookie.
	 *
	 * @param name the cookie's name
	 * @param value the cookie's value
	 * @param expires when the cookie expires
	 * @param domain the domain to be associated with this cookie
	 * @param path the path to be associated with this cookie
	 */
	public static void setCookie(String name, String value, Date expires,
			String domain, String path) {
		String cookie = Global.encodeURIComponent(name)
				+ '='
				+ Global.encodeURIComponent(value);
		if (expires != null)
			cookie += ";expires=" + expires.toGMTString();
		if (domain != null)
			cookie += ";domain=" + domain;
		if (path != null)
			cookie += ";path=" + path;

		cookie += ";secure;samesite=Strict";

		DomGlobal.document.cookie = cookie;
	}

	private static HashMap<String, String> loadCookies() {
		HashMap<String, String> cookieMap = new HashMap<>();
		String docCookie = DomGlobal.document.cookie;
		if (docCookie != null && !docCookie.isEmpty()) {
			String[] crumbs = docCookie.split("; ");
			for (int i = crumbs.length - 1; i >= 0; --i) {
				String name, value;
				int eqIdx = crumbs[i].indexOf('=');
				if (eqIdx == -1) {
					name = crumbs[i];
					value = "";
				} else {
					name = crumbs[i].substring(0, eqIdx);
					value = crumbs[i].substring(eqIdx + 1);
				}

				name = Global.decodeURIComponent(name);
				value = Global.decodeURIComponent(value);

				cookieMap.put(name, value);
			}
		}

		return cookieMap;
	}

	private static HashMap<String, String> ensureCookies() {
		if (cachedCookies == null || needsRefresh()) {
			cachedCookies = loadCookies();
		}
		return cachedCookies;
	}

	private static boolean needsRefresh() {
		String docCookie = DomGlobal.document.cookie;

		// Check to see if cached cookies need to be invalidated.
		if (!Objects.equals(docCookie, rawCookies)) {
			rawCookies = docCookie;
			return true;
		} else {
			return false;
		}
	}
}
