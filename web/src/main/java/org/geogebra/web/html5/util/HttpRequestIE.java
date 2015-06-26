package org.geogebra.web.html5.util;

import org.geogebra.common.move.ggtapi.models.AjaxCallback;
import org.geogebra.common.util.HttpRequest;

public class HttpRequestIE extends HttpRequest {

	@Override
	public void sendRequest(String url) {
		// only used by Singular, not needed ATM

	}

	@Override
	public native void sendRequestPost(String url, String data,
			AjaxCallback callback) /*-{
		var xdr = new XDomainRequest();
		if (xdr) {
			xdr.open("POST", url);
			xdr.oneror = function() {
				callback.@org.geogebra.common.move.ggtapi.models.AjaxCallback::onError(Ljava/lang/String;)("XDR error");
			}
			xdr.onload = function() {
				callback.@org.geogebra.common.move.ggtapi.models.AjaxCallback::onSuccess(Ljava/lang/String;)(xdr.responseText+"");
			}
			xdr.send(data);
		}

	}-*/;

}
