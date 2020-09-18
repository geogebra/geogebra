package org.geogebra.web.richtext.impl;

import com.google.gwt.core.client.JavaScriptObject;

import jsinterop.annotations.JsType;

@JsType(isNative = true)
public interface HasContent {

	void selectionChanged(EditorCallback editorCallback);

	void contentChanged(EditorCallback editorCallback);

	JavaScriptObject save();

}
