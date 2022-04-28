package org.geogebra.common.util;

import org.geogebra.common.move.ggtapi.models.AjaxCallback;

/**
 * Common abstract class for HttpRequest, implemented by different ways in
 * desktop and web
 * 
 * @author Zoltan Kovacs
 */
public abstract class HttpRequest {
	/**
	 * the default HTTP request timeout in seconds
	 */
	protected static final int DEFAULT_TIMEOUT = 10;
	/**
	 * current timeout for HTTP requests
	 */
	private int timeout = DEFAULT_TIMEOUT;

	/**
	 * stores if the HTTP request is already processed
	 */
	public boolean processed = false;
	/**
	 * the textual content of the result (or the error message)
	 */
	protected String responseText;
	private String type = "text/plain";
	private String auth;

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
	 * @return if the HTTP request has been processed by the remote server
	 */
	public boolean isProcessed() {
		return processed;
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
	 * @param processed
	 *     set processed
	 */
	protected void setProcessed(boolean processed) {
		this.processed = processed;
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
}
