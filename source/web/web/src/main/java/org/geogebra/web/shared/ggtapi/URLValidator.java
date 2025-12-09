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

import org.geogebra.regexp.shared.RegExp;

/**
 * Class to validate urls
 *
 * @author laszlo
 */
public class URLValidator {
	private static final String AZLATIN2 = "a-z\u00E1\u00E9\u00ED\u00F3\u00F6"
			+ "\u0151\u00FA\u00FC\u0171";
	private static final String AZLATIN2D = AZLATIN2 + "\\d";
	private static final String PROTOCOL = "^(https?:\\/\\/)?";
	private static final String DOMAIN = "((([" + AZLATIN2D + "]([" + AZLATIN2D + "-]*["
			+ AZLATIN2 + "])*)\\.?)+[ " + AZLATIN2 + "]{2,}|";
	private static final String IP4_ADDRESS = "((\\d{1,3}\\.){3}\\d{1,3}))";
	private static final String PORT_WITH_PATH = "(\\:\\d+)?(\\/.*)*?$";
	private static final String DOMAIN_OR_IP = DOMAIN + IP4_ADDRESS;

	private static final String URL_PATTERN = PROTOCOL
				+ DOMAIN_OR_IP
				+ PORT_WITH_PATH;

	private final RegExp regexp;

	/**
	 * Constructor
	 */
	public URLValidator() {
		regexp = RegExp.compile(URL_PATTERN, "i");
	}

	/**
	 *
	 * @param url to test
	 * @return if url is syntactically correct or not.
	 */
	public boolean isValid(String url) {
		return regexp.test(url);
	}
}
