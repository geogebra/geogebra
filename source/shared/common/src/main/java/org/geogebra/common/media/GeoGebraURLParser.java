package org.geogebra.common.media;

import javax.annotation.CheckForNull;

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
	 * @param url
	 *            GeoGebra URL
	 * @return material sharing key (or numeric ID); null if URL does not specify any ID
	 */
	public static @CheckForNull String getIDfromURL(String url) {
		String urlNoProtocol = removeProtocol(url);
		final String material = "/material/show/id/";

		// remove hostname
		int beginIndex = urlNoProtocol.indexOf('/');
		if (beginIndex < 0) {
			return null;
		}
		String pathAndQuery = urlNoProtocol.substring(beginIndex);

		String id;

		// determine the start position of ID in the URL
		int start = -1;
		if (pathAndQuery.startsWith(material)) {
			start = material.length();
		} else if (pathAndQuery.startsWith("/m/")) {
			start = "/m/".length();
		} else if (!url.contains("geogebra.org") || pathAndQuery.contains("/m")) {
			// support short URLs but be a bit picky with geogebra.org
			start = pathAndQuery.lastIndexOf("/m") + 2;
		}

		// no valid URL?
		if (start == -1) {
			Log.debug("problem parsing: " + pathAndQuery);
			return null;
		}

		// the end position is either before the next slash or at the
		// end of the string
		int end = pathAndQuery.indexOf('/', start);

		if (end == -1) {
			end = pathAndQuery.length();
		}
		// fetch ID
		id = pathAndQuery.substring(start, end);
		return id.length() > 5 ||  id.matches("\\d+") ? id : null;
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
