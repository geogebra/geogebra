package org.geogebra.web.richtext.impl;

import org.geogebra.gwtutil.JsRunnable;

import elemental2.dom.CanvasRenderingContext2D;
import jsinterop.annotations.JsType;

/**
 * Represents editable rich text content. Common interface for tables and documents.
 */
@JsType(isNative = true)
public interface HasContent {

	/**
	 * Add listener for selection changes.
	 * @param editorCallback listener
	 */
	void selectionChanged(JsRunnable editorCallback);

	/**
	 * Add listener for content changes.
	 * @param editorCallback listener
	 */
	void contentChanged(JsRunnable editorCallback);

	/**
	 * Add listener for Esc key.
	 * @param editorCallback listener
	 */
	void onEscape(JsRunnable editorCallback);

	/**
	 * Store current document in a plain JS object.
	 * @return document content
	 */
	Object save();

	/**
	 * Load document from a plain JS object.
	 * @param content document content
	 */
	void load(Object content);

	/**
	 * Draw editor on a canvas.
	 * @param context canvas context
	 */
	void draw(CanvasRenderingContext2D context);

}
