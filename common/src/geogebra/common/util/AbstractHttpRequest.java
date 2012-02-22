package geogebra.common.util;

/**
 * Sends a HTTP request to a server by calling an URL.
 * Used for using outsourced services (e.g. Singular webservice).
 * @author Zoltan Kovacs <zoltan@geogebra.org>
 *
 */
public abstract class AbstractHttpRequest {
	
	public abstract String getResponse(String url); 
	
}
