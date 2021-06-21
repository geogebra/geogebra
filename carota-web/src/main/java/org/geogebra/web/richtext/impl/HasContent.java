package org.geogebra.web.richtext.impl;

import com.google.gwt.core.client.JavaScriptObject;

import elemental2.dom.CanvasRenderingContext2D;
import jsinterop.annotations.JsType;

@JsType(isNative = true)
public interface HasContent {

	void selectionChanged(EditorCallback editorCallback);

	void contentChanged(EditorCallback editorCallback);

	JavaScriptObject save();

	void load(Object content);

	void draw(CanvasRenderingContext2D context);

}
