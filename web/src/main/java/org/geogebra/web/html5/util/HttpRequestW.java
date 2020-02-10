package org.geogebra.web.html5.util;

import org.geogebra.common.move.ggtapi.models.AjaxCallback;
import org.geogebra.common.util.HttpRequest;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;

/**
 * Implements HTTP requests and responses for web.
 * 
 * @author Zoltan Kovacs
 */
public class HttpRequestW extends HttpRequest {

	@Override
	public void sendRequestPost(String method, String url, String post,
			AjaxCallback callback) {
		XHR2 request = XHR2.create();
		if (callback == null) {
			request.openSync(method, url);
		} else {
			request.open(method, url);
		}
		// text/plain needed for SMART, hopefully no problem for others
		request.setRequestHeader("Content-type", getType());
		if (!StringUtil.emptyOrZero(getAuth())) {
			Log.debug("setting authorization header");
			request.setRequestHeader("Authorization", "Bearer " + getAuth());
		}

		request.onLoad(callback);
		request.send(post);
	}
}
