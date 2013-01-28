package geogebra.web.helper;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author gabor
 *
 *	Callback for the Ajax XHR2 object
 */
public interface XHR2OnloadCallback {

	public void onLoad(JavaScriptObject response);
}
