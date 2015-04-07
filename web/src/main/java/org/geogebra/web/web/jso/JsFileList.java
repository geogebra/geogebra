package org.geogebra.web.web.jso;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.DataTransfer;
import com.google.gwt.dom.client.InputElement;

public class JsFileList extends JavaScriptObject {

	public static JsFileList from(DataTransfer dataTransfer) {
		return nativeFrom(dataTransfer);
	}

	public static JsFileList from(InputElement inputElement) {
		return nativeFrom(inputElement);
	}

	private static native JsFileList nativeFrom(JavaScriptObject jso) /*-{
		return jso.files;
	}-*/;

	protected JsFileList() {
	}

	public final native int getLength() /*-{
		return this.length;
	}-*/;

	public final native JsFile get(int index) /*-{
		return this[index];
	}-*/;

}
