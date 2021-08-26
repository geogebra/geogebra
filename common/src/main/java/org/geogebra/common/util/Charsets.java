package org.geogebra.common.util;

import java.nio.charset.Charset;

/**
 * Constants for specifying Charsets.
 * 
 * Workaround for missing StandardCharsets on Android API 18-
 * 
 */
public class Charsets {

	/**
	 * Name of the UTF-8 charset
	 */
	public static final String UTF_8 = "UTF-8";

	/**
	 * Returns UTF-8 charset, guaranteed to work on all Java implementations
	 * 
	 * @return UTF charset
	 */
	public static Charset getUtf8() {
		return Charset.forName(UTF_8);
	}

}
