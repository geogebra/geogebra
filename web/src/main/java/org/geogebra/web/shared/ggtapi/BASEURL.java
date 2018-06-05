package org.geogebra.web.shared.ggtapi;

import com.google.gwt.core.client.GWT;

/**
 * @author gabor
 *
 *         BaseURL for handling url based operations
 */
public final class BASEURL {

	/**
	 * the start of the url
	 */
	public static final String URL_START = buildBaseURL();
	/**
	 * the html that opens the window
	 */
	public static final String OPENER = "html/opener.html";
	/**
	 * the callback
	 */
	public static final String CALLBACK_HTML = "html/ggtcallback.html";

	private static String buildBaseURL() {
		return GWT.getModuleBaseForStaticFiles();
	}

	/**
	 * @return change this concerning what environment the project runs.
	 */
	public static String getCallbackUrl() {

		return BASEURL.URL_START + BASEURL.CALLBACK_HTML;
	}

	/**
	 * @return the url that will redirect the window to GGT login
	 */
	public static String getOpenerUrl() {
		return BASEURL.URL_START + BASEURL.OPENER;
	}
}