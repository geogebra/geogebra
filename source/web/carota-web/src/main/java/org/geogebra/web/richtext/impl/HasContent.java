/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
