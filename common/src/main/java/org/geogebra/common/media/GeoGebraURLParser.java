package org.geogebra.common.media;

import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;

/**
 * Helper methods for getting material ID from URLs
 */
public final class GeoGebraURLParser {

	/**
	 * @param url
	 *            URL
	 * @return whether URL belongs to GeoGebra
	 */
	public static boolean isGeoGebraURL(String url) {
		String urlNoProtocol = removeProtocol(url);
		if (StringUtil.empty(urlNoProtocol)) {
			return false;
		}
		String host = urlNoProtocol.split("/")[0];
		return "geogebra.org".equals(host) || "ggbm.at".equals(host)
				|| "ggbtu.be".equals(host) || host.endsWith(".geogebra.org");
	}

	/**
	 * @param processedUrlString0
	 *            GeoGebra URL
	 * @return material sharing key (or numeric ID)
	 */
	public static String getIDfromURL(String processedUrlString0) {
		String processedUrlString = removeProtocol(processedUrlString0);
		final String material = "/material/show/id/";

		// remove hostname
		processedUrlString = processedUrlString.substring(processedUrlString.indexOf('/'));

		String id;

		// determine the start position of ID in the URL
		int start;
		if (processedUrlString.startsWith(material)) {
			start = material.length();
		} else if (processedUrlString.startsWith("/m/")) {
			start = "/m/".length();
		} else {
			start = processedUrlString.lastIndexOf("/m") + 2;
		}

		// no valid URL?
		if (start == -1) {
			Log.debug("problem parsing: " + processedUrlString);
			return null;
		}

		// the end position is either before the next slash or at the
		// end of the string
		int end = -1;
		if (start > -1) {
			end = processedUrlString.indexOf('/', start);
		}

		if (end == -1) {
			end = processedUrlString.length();
		}
		// fetch ID
		id = processedUrlString.substring(start, end);
		return id;
	}

	/**
	 * remove eg http:// if it's there
	 */
	private static String removeProtocol(String processedUrlString) {
		if (processedUrlString.contains("://")) {
			return processedUrlString.substring(processedUrlString.indexOf("://") + 3);
		}
		return processedUrlString;
	}
}
