package org.geogebra.web.html5.util;

import org.geogebra.common.move.ggtapi.models.AjaxCallback;

import com.google.gwt.xhr.client.XMLHttpRequest;

/**
 * @author gabor extendef from native XHR to accept FormData
 */
public class XHR2 extends XMLHttpRequest {

	/**
	 * Use XHR2.create instead
	 */
	protected XHR2() {
		// needed for GWT
	}

	/** @return new XHR */
	public static native XHR2 create() /*-{
		return new $wnd.XMLHttpRequest();
	}-*/;

	/**
	 * @param httpMethod
	 *            GET/POST/PUT/ETC
	 * @param url
	 *            url to go
	 * 
	 *            Opens a sync AJAX call to the server
	 */
	public final native void openSync(String httpMethod, String url) /*-{
		this.open(httpMethod, url, false);
	}-*/;

	/**
	 * @param timeout
	 *            the timeout to made
	 */
	public final native void setTimeOut(int timeout) /*-{
		this.timeout = timeout;
	}-*/;

	/**
	 * @param callback
	 *            Contains the he success handler and the error handler code
	 */
	public final native void onLoad(AjaxCallback callback) /*-{
		var xhr = this;
		xhr.onload = function(e) {
			if (xhr.readyState === 4) {
				if (xhr.status === 200) {
					callback.@org.geogebra.common.move.ggtapi.models.AjaxCallback::onSuccess(Ljava/lang/String;)(xhr.responseText);
				} else {
					callback.@org.geogebra.common.move.ggtapi.models.AjaxCallback::onError(Ljava/lang/String;)(xhr.status + ":" + xhr.statusText);
				}
			}
		};
		xhr.onerror = function(e) {
			callback.@org.geogebra.common.move.ggtapi.models.AjaxCallback::onError(Ljava/lang/String;)(xhr.status + ":" + xhr.statusText);
		}
	}-*/;

}
