package geogebra.web.helper;

import com.google.gwt.core.client.JavaScriptException;

public class RequestTemplateXhr2 implements RequestTemplate {

	
	public void fetchBinary(String url, FileLoadCallback callback) {
		try {
			nativeFetchBinary(url, callback);
		} catch (JavaScriptException ex) {
			callback.onError(ex.getMessage());
		}
	}
	
	private native void nativeFetchBinary(String url, FileLoadCallback callback) /*-{
		var req = new XMLHttpRequest();
		req.open('GET', url, true);
		req.responseType = 'arraybuffer';	// fetch binary data; requires XHR level 2, see http://www.html5rocks.com/en/tutorials/file/xhr2/
		
		req.onerror = function(evt) {
			var errorMessage = req.status + ' - ' + req.statusText;
			callback.@geogebra.web.helper.FileLoadCallback::onError(Ljava/lang/String;)(errorMessage);
		};
		req.onload = function(evt) {
			if (req.status == 200) {	// SC_OK ?
				var bytes = new Uint8Array(req.response);		// wrap ArrayBuffer
				callback.@geogebra.web.helper.FileLoadCallback::onSuccess(Lgeogebra/web/jso/JsUint8Array;)(bytes);
			} else {
				req.onerror(evt);
			}
		};
    	req.send();
	}-*/;
	
}
