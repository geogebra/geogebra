package org.geogebra.web.html5.util;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author ancsingabor wrapper for javascript objects to get out their calls
 *         easily
 *
 */
public class JavaScriptObjectWrapper extends JavaScriptObject {

	/**
	 * protected javascriptobjectwrapper
	 */
	protected JavaScriptObjectWrapper() {

	}

	/**
	 * @param key
	 *            the index to get
	 * @return The javascriptobject stored on that key
	 */
	public final native JavaScriptObjectWrapper getKeyAsObject(String key) /*-{
		return this[key];
	}-*/;

	/**
	 * @param key
	 *            the index to get
	 * @return the value stored there.
	 */
	public final native String getKeyAsString(String key) /*-{
		return this[key];
	}-*/;

}
