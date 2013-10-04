package geogebra.web.html5;

import com.google.gwt.xhr.client.XMLHttpRequest;

/**
 * @author gabor
 *	extendef from native XHR to accept FormData
 */
public class XHR2 extends XMLHttpRequest {
	
	protected XHR2() {
		
	}
	
	/**
	 * @param formData Formdata to send
	 * 
	 * Sends a formData object via AJAX
	 */
	public final native void send(FormData formData) /*-{
		this.send(formData);
	}-*/;
	
	/**
	 * @param httpMethod GET/POST/PUT/ETC
	 * @param url url to go
	 * 
	 * Opens a sync AJAX call to the server
	 */
	public final native void openSync(String httpMethod, String url) /*-{
		this.open(httpMethod, url, false);		
	}-*/;
	
	/**
	 * @param timeout the timeout to made
	 */
	public final native void setTimeOut(int timeout) /*-{
		this.timeout = timeout;
	}-*/;
	
	
	/**
	 * @param succesHandler The success handler code
	 * @param errorHandler The error handler code
	 */
	public final native void onLoad(AjaxSucces succesHandler, AjaxError errorHandler) /*-{
		var xhr = this;
		xhr.onload = function(e) {
			if (xhr.readyState === 4) {
				if (xhr.status === 200) { 
					succesHandler.@geogebra.web.html5.AjaxSucces::onSuccess(Ljava/lang/String;)(xhr.responseText);
				} else {
					if (errorHandler !== null) {
						errorHandler.@geogebra.web.html5.AjaxError::onError(Ljava/lang/String;)(xhr.statusText);
					} else {
						@geogebra.common.main.App::debug(Ljava/lang/String;)(xhr.statusText);
					}
				}
			}
		};
	}-*/;
	
	/**
	 * @param errorHandler for AJAX calls
	 */
	public final native void onError(AjaxError errorHandler) /*-{
		var xhr = this;
		xhr.onerror = function(e) {
			errorHandler.@geogebra.web.html5.AjaxError::onError(Ljava/lang/String;)(xhr.statusText);
		}
	}-*/;
	
}
