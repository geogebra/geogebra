package org.geogebra.common.util;

/**
 * @author Zoltan Kovacs <zoltan@geogebra.org> Common abstract class for
 *         URLEncoder, implemented by different ways in desktop and web
 */
public abstract class URLEncoder {
	/**
	 * Encodes a string to be forwarded to a web server as an URL
	 * 
	 * @param decodedURL
	 *            the unencoded string to be encoded
	 * @return the encoded string
	 */
	public abstract String encode(String decodedURL);
}
