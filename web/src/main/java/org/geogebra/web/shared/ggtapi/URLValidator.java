package org.geogebra.web.shared.ggtapi;

import org.gwtproject.regexp.shared.RegExp;

/**
 * Class to validate urls
 *
 * @author laszlo
 */
public class URLValidator {
	private static final String AZLATIN2 = "a-záéíóöőúüű";
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
