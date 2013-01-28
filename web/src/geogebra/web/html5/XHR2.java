package geogebra.web.html5;

import geogebra.web.helper.XHR2OnloadCallback;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author gabor
 * 
 *	XHR2 to get responseType like arrayBuffer working
 *
 */
public class XHR2 {
	
	private JavaScriptObject xhr2;
	/**
	 * Creates a new XmlHttp2 object
	 */
	public XHR2 () {
		this.xhr2 = createXHR2();
	}
	
	private native JavaScriptObject createXHR2 () /*-{
		return new $wnd.XMLHttpRequest();
	}-*/;
	
	/**
	 * @param type responseType
	 * sets the proper response type
	 */
	public native void setResponseType(String type) /*-{
		this.@geogebra.web.html5.XHR2::xhr2.responseType = type;
	}-*/;
	
	/**
	 * @param method GET/POST
	 * @param url	target url
	 * @param async not block or block browser
	 */
	public native void open(String method, String url, boolean async) /*-{
		this.@geogebra.web.html5.XHR2::xhr2.open(method, url, async);
	}-*/;
	
	/**
	 * Sends the request
	 */
	public native void send() /*-{
		this.@geogebra.web.html5.XHR2::xhr2.send();
	}-*/;
	
	/**
	 * @param callback the callback to call
	 * Adds the callback to the onload hanlder
	 */
	public native void addLoadHandler(XHR2OnloadCallback callback) /*-{
		this.@geogebra.web.html5.XHR2::xhr2.onload = function(e) {
			if (this.status === 200) {
				callback.@geogebra.web.helper.XHR2OnloadCallback::onLoad(Lcom/google/gwt/core/client/JavaScriptObject;)(this.response);
			}
		}
	}-*/;
}
