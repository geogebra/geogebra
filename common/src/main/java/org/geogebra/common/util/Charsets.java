package org.geogebra.common.util;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

import org.geogebra.common.util.debug.Log;

public class Charsets {

	public static final String UTF_8 = "UTF-8";

	/**
	 * @return UTF charset
	 */
	public static Charset getUtf8() {
		try {
			return Charset.forName(UTF_8);
		} catch (UnsupportedCharsetException ex) {
			Log.warn("UTF-8 not available");
		}
		return Charset.defaultCharset();
	}

}
