package geogebra.common.util;

/**
 * Common URLEncoder
 * @author Zoltan Kovacs <zoltan@geogebra.org>
 *
 */
public abstract class URLEncoder {
	/**
	 * Encodes a string to be forwarded to a web server as an URL
	 * @param decodedURL the unencoded string to be encoded
	 * @return the encoded string
	 */
	public abstract String encode(String decodedURL);
}

