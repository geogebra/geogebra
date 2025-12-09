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

package org.geogebra.web.html5.util;

import static org.geogebra.common.move.ggtapi.models.AuthenticationModel.CSRF_TOKEN_KEY_NAME;

import java.util.function.Consumer;

import org.geogebra.common.move.ggtapi.models.AjaxCallback;
import org.geogebra.common.util.HttpRequest;
import org.geogebra.common.util.StringUtil;

import elemental2.dom.XMLHttpRequest;

/**
 * Implements HTTP requests and responses for web.
 */
public class HttpRequestW extends HttpRequest {
	private final XMLHttpRequest request = new XMLHttpRequest();

	@Override
	public void sendRequestPost(String method, String url, String post,
			AjaxCallback callback) {
		sendRequest(method, url, post,
				xhr -> callback.onSuccess(xhr.responseText), callback::onError);
	}

	/**
	 * Sends a `method` type HTTP request to the `url` address with `content`
	 * and calls `onSuccess`.
	 *
	 * @param url
	 *            full URL to be opened
	 * @param content
	 *            already encoded HTTP request content
	 */
	public void sendRequest(String method, String url, String content,
			Consumer<XMLHttpRequest> onSuccess, Consumer<String> onError) {
		request.open(method, url);

		// text/plain needed for SMART, hopefully no problem for others
		request.setRequestHeader("Content-type", getType());
		if (!StringUtil.emptyOrZero(getAuth())) {
			request.setRequestHeader("Authorization", "Bearer " + getAuth());
		}

		if (!StringUtil.empty(getRequestCSRFHeader())) {
			request.setRequestHeader(CSRF_TOKEN_KEY_NAME, getRequestCSRFHeader());
		}

		request.onload = (e) -> {
			if (request.readyState == 4) {
				if (request.status == 200) {
					onSuccess.accept(request);
				} else {
					onError.accept(request.status + ":" + request.statusText);
				}
			}
		};

		request.onerror = (e) -> {
			onError.accept(request.status + ":" + request.statusText);
			return null;
		};

		request.send(content);
	}

	@Override
	public String getResponseHeader(String name) {
		return request.getResponseHeader(name);
	}

	/**
	 * @param responseType one of "blob", "arraybuffer", "document", "json", "text"
	 */
	public void setResponseType(String responseType) {
		request.responseType = responseType;
	}
}
