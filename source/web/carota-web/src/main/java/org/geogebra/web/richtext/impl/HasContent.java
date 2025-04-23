package org.geogebra.web.richtext.impl;

import org.geogebra.gwtutil.JsRunnable;

import elemental2.dom.CanvasRenderingContext2D;
import jsinterop.annotations.JsType;

/**
 * Represents editable rich text content. Common interface for tables and documents.
 */
@JsType(isNative = true)
public interface HasContent {

	void selectionChanged(JsRunnable editorCallback);

	void contentChanged(JsRunnable editorCallback);

	void onEscape(JsRunnable editorCallback);

	Object save();

	void load(Object content);

	void draw(CanvasRenderingContext2D context);

}
