package org.geogebra.web.html5.util;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "window")
public class GlobalFunctions {
	/**
	 * Decode a base-64 encoded string
	 *
	 * @param encoded Required. The string which has been encoded by the btoa() method
	 * @return Decoded a base-64 encoded string
	 */
	public static native String atob(String encoded);

	/**
	 * Encodes a string in base-64.
	 *
	 * This method uses the "A-Z", "a-z", "0-9", "+", "/" and "=" characters to encode the string.
	 *
	 * @param str Required. The string to be encoded
	 * @return A String, representing the base-64 encoded string
	 */
	public static native String btoa(String str);

	/**
	 * @param str input string
	 * @return string without escaped sequences
	 */
	public static native String unescape(String str);

	/**
	 * @param str input string
	 * @return string with escaped sequences
	 */
	public static native String escape(String str);
}
