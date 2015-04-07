package org.geogebra.web.web.jso;

import com.google.gwt.core.client.JavaScriptObject;

public class JsUint8Array extends JavaScriptObject {

	protected JsUint8Array() {
	}

	public final native int getLength() /*-{
		return this.length;
	}-*/;

	public final native short get(int index) /*-{
		return this[index];
	}-*/;

	public final native void set(int index, short byteValue) /*-{
		this[index] = byteValue;
	}-*/;

	public final native String getString() /*-{
		var ret = "";
		for (i = 0; i < this.length; i++)
			ret += String.fromCharCode(this[i]);
		return ret;
	}-*/;
}
