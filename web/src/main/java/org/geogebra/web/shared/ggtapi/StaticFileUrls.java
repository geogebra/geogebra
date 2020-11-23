package org.geogebra.web.shared.ggtapi;

import com.google.gwt.core.client.GWT;

/**
 * Provides absolute URLs of static files
 */
public final class StaticFileUrls {

	private static final String STATIC_DIR = GWT.getModuleBaseForStaticFiles();
	private static final String OPENER = "html/opener.html";
	private static final String CALLBACK_HTML = "html/ggtcallback.html";

	/**
	 * Callback page converts URL parameters to postMessage to make cross-origin work
	 * @return url of the callback page
	 */
	public static String getCallbackUrl() {
		return STATIC_DIR + CALLBACK_HTML;
	}

	/**
	 * Opener page redirects to a page on another domain, used for MAT login
	 * @return the url of the opener page
	 */
	public static String getOpenerUrl() {
		return STATIC_DIR + OPENER;
	}
}