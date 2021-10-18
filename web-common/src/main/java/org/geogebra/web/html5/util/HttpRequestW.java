package org.geogebra.web.html5.util;

import org.geogebra.common.move.ggtapi.models.AjaxCallback;
import org.geogebra.common.util.HttpRequest;
import org.geogebra.common.util.StringUtil;

import elemental2.dom.XMLHttpRequest;

/**
 * Implements HTTP requests and responses for web.
 */
public class HttpRequestW extends HttpRequest {

	@Override
	public void sendRequestPost(String method, String url, String post,
			AjaxCallback callback) {
		XMLHttpRequest request = new XMLHttpRequest();
		request.open(method, url);

		// text/plain needed for SMART, hopefully no problem for others
		request.setRequestHeader("Content-type", getType());
		if (!StringUtil.emptyOrZero(getAuth())) {
			request.setRequestHeader("Authorization", "Bearer " + getAuth());
		}

		request.onload = (e) -> {
			if (request.readyState == 4) {
				if (request.status == 200) {
					callback.onSuccess(request.responseText);
				} else {
					callback.onError(request.status + ":" + request.statusText);
				}
			}
		};

		request.onerror = (e) -> {
			callback.onError(request.status + ":" + request.statusText);
			return null;
		};

		request.send(post);
	}
}
