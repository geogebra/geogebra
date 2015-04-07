package org.geogebra.web.web.move.ggtapi.operations;

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
	public static final String urlStart = buildBaseURL();
	/**
	 * the html that opens the window
	 */
	public static final String opener = "html/opener.html";
	/**
	 * the callback
	 */
	public static final String callbackHTML = "html/ggtcallback.html";

	private static String buildBaseURL() {
		return GWT.getModuleBaseForStaticFiles();
	}

	/**
	 * @return change this concerning what environment the project runs.
	 */
	public static String getCallbackUrl() {

		return BASEURL.urlStart + BASEURL.callbackHTML;
	}

	/**
	 * @return the url that will redirect the window to GGT login
	 */
	public static String getOpenerUrl() {
		return BASEURL.urlStart + BASEURL.opener;
	}
}