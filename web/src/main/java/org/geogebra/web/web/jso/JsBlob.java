package org.geogebra.web.web.jso;

import com.google.gwt.core.client.JavaScriptObject;

public class JsBlob extends JavaScriptObject {

	protected JsBlob() {
	}

	/**
	 * Represents the size of the Blob object in bytes.
	 * 
	 * @return size in bytes
	 */
	public final native int getSize() /*-{
		return this.size;
	}-*/;

	/**
	 * Gets the MIME type of the Blob or the empty string if it is not known.
	 * 
	 * @return MIME type of the Blob or the empty string
	 */
	public final native String getType() /*-{
		return this.type;
	}-*/;

	public final native String slice(int start, int length) /*-{
		return this.slice(start, length);
	}-*/;

	public final native String slice(int start, int length, String contentType) /*-{
		return this.slice(start, length, contentType);
	}-*/;

}
