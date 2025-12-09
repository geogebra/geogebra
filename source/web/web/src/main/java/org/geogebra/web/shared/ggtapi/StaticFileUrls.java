/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.shared.ggtapi;

import com.google.gwt.core.client.GWT;

/**
 * Provides absolute URLs of static files
 */
public final class StaticFileUrls {

	private static final String STATIC_DIR = GWT.getModuleBaseForStaticFiles();
	private static final String CALLBACK_HTML = "html/ggtcallback.html";

	/**
	 * Callback page converts URL parameters to postMessage to make cross-origin work
	 * @return url of the callback page
	 */
	public static String getCallbackUrl() {
		return STATIC_DIR + CALLBACK_HTML;
	}

}