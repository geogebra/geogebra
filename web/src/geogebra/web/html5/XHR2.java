package geogebra.web.html5;

import com.google.gwt.xhr.client.XMLHttpRequest;

/**
 * @author gabor
 *	extendef from native XHR to accept FormData
 */
public class XHR2 extends XMLHttpRequest {
	
	public final native void send(FormData formData) /*-{
		this.send(formData);
	}-*/;
	
	protected XHR2() {
		
	}
}
