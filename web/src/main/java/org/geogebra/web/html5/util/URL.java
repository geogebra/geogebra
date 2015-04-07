package org.geogebra.web.html5.util;

import com.google.gwt.user.client.Window;

/**
 * @author gabor creates some url helper
 */
public class URL {

	/**
	 * @param param
	 *            the url parameter
	 * @return url param as String
	 */
	public static String getQueryParameterAsString(String param) {
		return getUrlParameter(param);
	}

	private static String getUrlParameter(String param) {
		return Window.Location.getParameter(param);
	}

	/**
	 * @param param
	 *            the url parameter
	 * @return url parameter as a float
	 */
	public static int getQueryParameterAsInteger(String param) {
		return Integer.parseInt(getUrlParameter(param));
	}

	/**
	 * @param param
	 *            the url parameter
	 * @return url parameter's value as a double
	 */
	public static double getQueryParameterAsDouble(String param) {
		return Double.parseDouble(getUrlParameter(param));
	}

}
