package org.geogebra.common.util;

/**
 * Common abstract class for URLEncoder, implemented by different ways in
 * desktop and web
 * 
 * @author Zoltan Kovacs
 */
public interface URLEncoder {
	/**
	 * Encodes a string to be forwarded to a web server as URL parameter
	 * 
	 * @param urlComponent
	 *            the unencoded string to be encoded
	 * @return the encoded string
	 */
	String encode(String urlComponent);
}
