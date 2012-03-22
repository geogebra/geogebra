package geogebra.common.util;

/**
 * Common HttpRequest
 * @author Zoltan Kovacs <zoltan@geogebra.org>
 *
 */
public abstract class HttpRequest {
	/**
	 * Gets a response from a remote HTTP server
	 * @param url full URL to be opened
	 * @return the full textual content of the result after the request processed (the output page itself)
	 */
	public abstract String getResponse(String url); 
}
