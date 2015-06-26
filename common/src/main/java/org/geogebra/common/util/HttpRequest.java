package org.geogebra.common.util;

import org.geogebra.common.move.ggtapi.models.AjaxCallback;

/**
 * @author Zoltan Kovacs <zoltan@geogebra.org>
 * 
 *         Common abstract class for HttpRequest, implemented by different ways
 *         in desktop and web
 */
public abstract class HttpRequest {
	/**
	 * the default HTTP request timeout in seconds
	 */
	protected int DEFAULT_TIMEOUT = 10;
	/**
	 * current timeout for HTTP requests
	 */
	protected int timeout = DEFAULT_TIMEOUT;

	/**
	 * stores if the HTTP request is already processed
	 */
	public boolean processed = false;
	/**
	 * stores if the HTTP request has been successful
	 */
	protected Boolean success;
	/**
	 * the textual content of the result (or the error message)
	 */
	protected String responseText;

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
	 * Opens an URL
	 * 
	 * @param url
	 *            full URL to be opened
	 */
	public abstract void sendRequest(String url);

	/**
	 * Opens and URL and sends some POST parameters
	 * 
	 * @param url
	 *            full URL to be opened
	 * @param post
	 *            POST parameters (already encoded)
	 */
	public abstract void sendRequestPost(String url, String post,
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
	 * @return if the HTTP request was successful
	 */
	public Boolean isSuccessful() {
		return success;
	}


}
