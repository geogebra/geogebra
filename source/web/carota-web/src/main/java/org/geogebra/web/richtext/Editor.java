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

package org.geogebra.web.richtext;

import org.geogebra.web.richtext.impl.Carota;
import org.gwtproject.user.client.ui.Widget;

import elemental2.dom.CanvasRenderingContext2D;

/** The interface to the Carota editor */
public interface Editor {

	/**
	 * Serialize and reload content (to refresh fonts).
	 */
	void reload();

	/**
	 * Return the GWT widget that represents the editor.
	 *
	 * @return a GWT widget
	 */
	Widget getWidget();

	/**
	 * Focuses the editor.
	 */
	void focus(int x, int y);

	/**
	 * Sets the editor change listener
	 */
	void setListener(EditorChangeListener listener);

	/**
	 * Set the content of the editor
	 * @param content JSON encoded string in Carota format
	 */
	void setContent(String content);

	/**
	 * Deselect all text
	 */
	void deselect();

	/**
	 * Format selection or (if nothing selected) whole document.
	 *
	 * @param key
	 *            property name
	 * @param val
	 *            property value (double, bool or color string)
	 */
	void format(String key, Object val);

	/**
	 * @param <T>
	 *            parameter type (bool, string or double)
	 * @param key
	 *            property name
	 * @param fallback
	 *            fallback to use when format not found
	 * @return format property value
	 */
	<T> T getFormat(String key, T fallback);

	/**
	 * @return if part is selected, then hyperlink of selection,
	 *         otherwise hyperlink at the end of text
	 */
	String getHyperLinkURL();

	/**
	 * Insert a hyperlink.
	 * @param url link URL
	 * @param text link text
	 */
	void insertHyperlink(String url, String text);

	/**
	 * @return JSON encoded editor content
	 */
	String getContent();

	/**
	 * Draw editor on canvas.
	 * @param context2D canvas context
	 */
	void draw(CanvasRenderingContext2D context2D);

	/**
	 * @param width width in pixels
	 */
	void setWidth(int width);

	/**
	 * @return the plaintext representation of the hyperlink range
	 */
	String getHyperlinkRangeText();

	/**
	 * @return plain text representation of the selection
	 */
	String getSelectionRangeText();

	/**
	 * Replace selection with a text.
	 * @param text new text content
	 */
	void setSelection(String text);

	/**
	 * Update text of selected hyperlink.
	 * @param input text
	 */
	void setHyperlinkUrl(String input);

	/**
	 * Returns link URL at given cursor position.
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @return hyperlink URL at given coordinates.
	 */
	String urlByCoordinate(int x, int y);

	/**
	 * Switch selection to list.
	 * @param listType "number" for numbered, "bullet" for a bullet list
	 */
	void switchListTo(String listType);

	/**
	 * @return "number" for numbered, "bullet" for a bullet list
	 */
	String getListStyle();

	/**
	 * @return minimal height in pixels
	 */
	int getMinHeight();

	/**
	 * @param sx external scale factor
	 */
	void setExternalScale(double sx);

	/**
	 * Add text insertion filter (for pasted text).
	 * @param handleInsert text insertion filter
	 */
	void addInsertFilter(Carota.InsertFilter handleInsert);
}
