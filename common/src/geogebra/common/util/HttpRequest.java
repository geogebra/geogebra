package geogebra.common.util;

/**
 * @author Zoltan Kovacs <zoltan@geogebra.org>
 * Common abstract class for HttpRequest, implemented by
 * different ways in desktop and web
 */
public abstract class HttpRequest {

	/**
	 * @param url full URL to be opened
	 * @return the full textual content of the result after the request processed (the output page itself)
	 * Gets a response from a remote HTTP server
	 */
	public abstract String getResponse(String url); 
}
