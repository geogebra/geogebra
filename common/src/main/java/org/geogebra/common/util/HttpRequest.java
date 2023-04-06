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
