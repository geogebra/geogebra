package geogebra.web.helper;

import geogebra.web.jso.JsUint8Array;

import com.google.gwt.core.client.JavaScriptException;

public class RequestTemplateXhr implements RequestTemplate {

	
	public void fetchBinary(String url, FileLoadCallback callback) {
		try {
			nativeFetchBinary(url, callback);
		} catch (JavaScriptException ex) {
			callback.onError(ex.getMessage());
		}
	}
	
	private static native JsUint8Array toByteArray(String binStr) /*-{
		var bytes = new Uint8Array(binStr.length);
		for (var i = 0; i < binStr.length; i++) {
			bytes[i] = binStr.charCodeAt(i) & 0xff;
		}
		return bytes;
	}-*/;
	
	private native void nativeFetchBinary(String url, FileLoadCallback callback) /*-{
		var req = new XMLHttpRequest();
		req.open('GET', url, true);
		req.overrideMimeType('text/plain; charset=x-user-defined'); // fetch binary data; hackery XHR 1, see http://www.html5rocks.com/en/tutorials/file/xhr2/
		
		req.onerror = function(evt) {
			var errorMessage = req.status + ' - ' + req.statusText;
			callback.@geogebra.web.helper.FileLoadCallback::onError(Ljava/lang/String;)(errorMessage);
		};
		req.onload = function(evt) {
			if (req.status == 200) {	// SC_OK ?
				var bytes = @geogebra.web.helper.RequestTemplateXhr::toByteArray(Ljava/lang/String;)(req.responseText);
				callback.@geogebra.web.helper.FileLoadCallback::onSuccess(Lgeogebra/web/jso/JsUint8Array;)(bytes);
			} else {
				req.onerror(evt);
			}
		};
    	req.send();
	}-*/;
	
}
