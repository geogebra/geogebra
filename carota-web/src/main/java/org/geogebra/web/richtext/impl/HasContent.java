package org.geogebra.web.richtext.impl;

import elemental2.dom.CanvasRenderingContext2D;
import jsinterop.annotations.JsType;

@JsType(isNative = true)
public interface HasContent {

	void selectionChanged(EditorCallback editorCallback);

	void contentChanged(EditorCallback editorCallback);

	void onEscape(EditorCallback editorCallback);

	Object save();

	void load(Object content);

	void draw(CanvasRenderingContext2D context);

}
