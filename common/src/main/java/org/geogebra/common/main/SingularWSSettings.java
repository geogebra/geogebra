package org.geogebra.common.main;

import org.geogebra.common.GeoGebraConstants;

/**
 * SingularWS settings (see AppD's handleHelpVersionArgs for details)
 */
public class SingularWSSettings {

	/**
	 * Do we want to use SingularWS for specific computations?
	 */
	public static boolean useSingularWebService = GeoGebraConstants.SINGULARWS_ENABLED_BY_DEFAULT;
	/**
	 * The remote machine to be used for outsourced computations.
	 */
	public static String singularWebServiceRemoteURL = "http://singularws.idm.jku.at/";
	/**
	 * Timeout for a SingularWS session to run in seconds.
	 */
	public static int singularWebServiceTimeout = 5;
	/**
	 * Above this value there is no detailed logging, only the size of the
	 * program code will be printed as a debug message. This can help avoiding
	 * too noisy debug.
	 */
	public static int debugMaxProgramSize = 2000;
	/**
	 * Use caching on server side? It's possible to use server side default by
	 * setting this to null, otherwise we'll override the server setting.
	 */
	public static Boolean useCaching = true;

	/**
	 * Reports current caching setting in human readable form.
	 * 
	 * @return "false", "true" or "auto"
	 */
	public static String getCachingText() {
		if (useCaching == null) {
			return "auto";
		}
		return useCaching.toString();
	}

	/**
	 * Sets the useCaching value to the requested setting.
	 * 
	 * @param s
	 *            the requested value
	 */
	public static void setCachingFromText(String s) {
		if ("auto".equals(s.toLowerCase())) {
			useCaching = null;
		}
		useCaching = Boolean.valueOf(s).booleanValue();
	}

}
