package geogebra.common.factories;

public abstract class UtilFactory {
	public static UtilFactory prototype;

	/**
	 * Sends an HTTP request to a server by calling an URL.
	 * Used for using outsourced services (e.g. Singular webservice).
	 * @author Zoltan Kovacs <zoltan@geogebra.org>
	 * 
	 * @param url The http URL
	 * @return The response string
	 */
	public abstract String newHttpRequestResponse(String url);
}