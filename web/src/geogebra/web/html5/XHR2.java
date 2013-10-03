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
	 * @param htpmethod GET/POST/PUT/ETC
	 * @param url url to go
	 * 
	 * Opens a sync AJAX call to the server
	 */
	public final native void openSync(String htpmethod, String url) /*-{
		this.open(httpmethod, url, false);		
	}-*/;
	
	/**
	 * @param timeout the timeout to made
	 */
	public final native void setTimeOut(int timeout) /*-{
		this.timeout = timeout;
	}-*/;	
	
	
	
}
