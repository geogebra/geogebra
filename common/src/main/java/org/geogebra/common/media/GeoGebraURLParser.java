package org.geogebra.common.media;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.util.debug.Log;

/**
 * Helper methods for getting material ID from URLs
 */
public final class GeoGebraURLParser {

	/**
	 * @param processedUrlString
	 *            URL
	 * @return whether URL belongs to GeoGebra
	 */
	public static boolean isGeoGebraURL(String processedUrlString) {
		final String ggbTubeOld = "geogebratube.org/";
		final String ggbTube = "tube.geogebra.org/";
		final String ggbTubeBeta = "beta.geogebra.org/";
		final String ggbTubeShort = "ggbtu.be/";
		final String ggbMatShort = "ggbm.at/";
		return processedUrlString.contains(GeoGebraConstants.GEOGEBRA_WEBSITE)
				|| processedUrlString.contains(GeoGebraConstants.GEOGEBRA_WEBSITE_BETA)
				|| processedUrlString.contains(ggbTube) || processedUrlString.contains(ggbTubeShort)
				|| processedUrlString.contains(ggbMatShort)
				|| processedUrlString.contains(ggbTubeBeta)
				|| processedUrlString.contains(ggbTubeOld);
	}

	/**
	 * @param processedUrlString0
	 *            GeoGebra URL
	 * @return material sharing key (or numeric ID)
	 */
	public static String getIDfromURL(String processedUrlString0) {
		String processedUrlString = processedUrlString0;
		final String material = "/material/show/id/";
		// remove eg http:// if it's there
		if (processedUrlString.contains("://")) {
			processedUrlString = processedUrlString.substring(processedUrlString.indexOf("://") + 3,
					processedUrlString.length());
		}
		// remove hostname
		processedUrlString = processedUrlString.substring(processedUrlString.indexOf('/'),
				processedUrlString.length());

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
}
