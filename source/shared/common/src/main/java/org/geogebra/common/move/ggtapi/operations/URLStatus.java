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

package org.geogebra.common.move.ggtapi.operations;

/**
 * Result of status check.
 */
public class URLStatus {
	private String errorKey;
	private String url;

	/**
	 * Constructor
	 */
	public URLStatus() {
		this.errorKey = null;
	}

	/**
	 * @param errorKey key for Localization.getError or null if there is no error
	 */
	public URLStatus(String errorKey) {
		this.errorKey = errorKey;
	}

	/**
	 * @param errorKey key for Localization.getError or null if there is no error
	 */
	public void setErrorKey(String errorKey) {
		this.errorKey = errorKey;
	}

	/**
	 * @return key for Localization.getError
	 */
	public String getErrorKey() {
		return errorKey;
	}

	/**
	 * @return page URL
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param embedUrl URL for embedding
	 * @return this
	 */
	public URLStatus withUrl(String embedUrl) {
		this.url = embedUrl;
		return this;
	}
}
