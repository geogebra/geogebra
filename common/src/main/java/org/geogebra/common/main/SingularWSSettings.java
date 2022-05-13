package org.geogebra.common.main;

/**
 * SingularWS settings (see AppD's handleHelpVersionArgs for details)
 */
public class SingularWSSettings {

	/**
	 * Do we want to use SingularWS for specific computations?
	 */
	private static volatile boolean useSingularWebService = false;
	/**
	 * The remote machine to be used for outsourced computations.
	 */
	private static volatile String singularWebServiceRemoteURL = "http://singularws.idm.jku.at/";
	private static Object lock = new Object();
	/**
	 * Timeout for a SingularWS session to run in seconds.
	 */
	private static volatile int singularWebServiceTimeout = 5;
	/**
	 * Above this value there is no detailed logging, only the size of the
	 * program code will be printed as a debug message. This can help avoiding
	 * too noisy debug.
	 */
	final public static int debugMaxProgramSize = 2000;
	/**
	 * Use caching on server side? It's possible to use server side default by
	 * setting this to null, otherwise we'll override the server setting.
	 */
	private static volatile Boolean useCaching = true;

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

	public static boolean getUseCaching() {
		return useCaching;
	}

	/**
	 * Sets the useCaching value to the requested setting.
	 * 
	 * @param s
	 *            the requested value
	 */
	public static void setCachingFromText(String s) {
		synchronized (lock) {
			if ("auto".equalsIgnoreCase(s)) {
				useCaching = null;
			}
			useCaching = Boolean.parseBoolean(s);
		}
	}

	/**
	 * @param url
	 *            service URL
	 */
	public static void setSingularWebServiceRemoteURL(String url) {
		synchronized (lock) {
			singularWebServiceRemoteURL = url;
		}
	}

	/**
	 * @param t
	 *            timeout
	 */
	public static void setTimeout(int t) {
		synchronized (lock) {
			singularWebServiceTimeout = t;
		}
	}

	public static String getSingularWebServiceRemoteURL() {
		return singularWebServiceRemoteURL;
	}

	public static int getTimeout() {
		return singularWebServiceTimeout;
	}

	public static boolean useSingularWebService() {
		return useSingularWebService;
	}

	/**
	 * @param b
	 *            flag for using remote Singular
	 */
	public static void setUseSingularWebService(boolean b) {
		synchronized (lock) {
			useSingularWebService = b;
		}
	}

}
