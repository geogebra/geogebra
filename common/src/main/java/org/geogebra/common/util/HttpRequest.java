package org.geogebra.common.util;

import org.geogebra.common.move.ggtapi.models.AjaxCallback;

/**
 * Common abstract class for HttpRequest, implemented by different ways in
 * desktop and web
 * 
 * @author Zoltan Kovacs
 */
public abstract class HttpRequest {

	private String type = "text/plain";
	private String auth;

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
