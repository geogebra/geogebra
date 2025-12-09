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

package org.geogebra.common.util;

import org.geogebra.common.move.ggtapi.models.AjaxCallback;

/**
 * Common abstract class for HttpRequest, implemented by different ways in
 * desktop and web
 *
 * @author Zoltan Kovacs
 */
public abstract class HttpRequest implements Cancelable {
	/**
	 * the default HTTP request timeout in seconds
	 */
	protected static final int DEFAULT_TIMEOUT = 10;
	/**
	 * current timeout for HTTP requests
	 */
	private int timeout = DEFAULT_TIMEOUT;

	/**
	 * the textual content of the result (or the error message)
	 */
	protected String responseText;
	private String type = "text/plain";
	private String auth;

	private String csrfToken;

	/**
	 * Gets a response from a remote HTTP server
	 *
	 * @return the full textual content of the result after the request
	 *         processed (the output page itself)
	 */
	public String getResponse() {
		return responseText;
	}

	/**
	 * Sends a `method` type HTTP request to the `url` address with `content`
	 * and calls `callback`
	 *
	 * @param url
	 *            full URL to be opened
	 * @param content
	 *            already encoded HTTP request content
	 */
	public abstract void sendRequestPost(String method, String url, String content,
			AjaxCallback callback);

	/**
	 * @param timeout_secs
	 *            HTTP request timeout in seconds Modify the default timeout for
	 *            HTTP requests Warning: the desktop version currently ignores
	 *            this setting
	 */
	public void setTimeout(Integer timeout_secs) {
		timeout = timeout_secs;
	}

	/**
	 * @return current timeout for HTTP requests
	 */
	protected int getTimeout() {
		return timeout;
	}

	/**
	 * @param responseText
	 *            response text
	 */
	protected void setResponseText(String responseText) {
		this.responseText = responseText;
	}

	/**
	 * Set mimetype to JSON
	 */
	public void setContentTypeJson() {
		this.type = "application/json";
	}

	protected String getType() {
		return type;
	}

	public void setAuth(String auth) {
		this.auth = auth;
	}

	protected String getAuth() {
		return auth;
	}

	@Override
	public void cancel() {
		// for now Android only
	}

	/**
	 * @param name header name
	 * @return value of response header with given name, null if not set
	 */
	public String getResponseHeader(String name) {
		return null;
	}

	public void setRequestCSRFHeader(String csrfToken) {
		this.csrfToken = csrfToken;
	}

	public String getRequestCSRFHeader() {
		return csrfToken;
	}
}
