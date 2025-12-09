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

package org.geogebra.common.euclidian.draw;

import org.geogebra.common.annotation.MissingDoc;
import org.geogebra.common.kernel.geos.GeoInline;
import org.geogebra.common.kernel.geos.properties.HorizontalAlignment;
import org.geogebra.common.kernel.geos.properties.VerticalAlignment;

/**
 * Object with text formatting properties.
 */
public interface HasTextFormat {

	/**
	 * @param key
	 *            formatting option
	 * @param val
	 *            value (String, int or bool, depending on key)
	 */
	void format(String key, Object val);

	/**
	 * @param key formatting option name
	 * @param fallback fallback when not set / indeterminate
	 * @param <T> option type
	 * @return formatting option value or fallback
	 */
	<T> T getFormat(String key, T fallback);

	/**
	 * @return hyperlink of selected part, or at the end of text element if no selection
	 */
	String getHyperLinkURL();

	/**
	 * @param url
	 *         (absolute) link URL
	 */
	void setHyperlinkUrl(String url);

	/**
	 * @return the plaintext representation of the hyperlink range
	 */
	String getHyperlinkRangeText();

	/**
	 * Inserts formatted hyperlink at the current selection
	 */
	void insertHyperlink(String url, String text);

	/**
	 * Returns the style of selected text
	 * @return "number" or "bullet"
	 */
	String getListStyle();

	/**
	 * Switch the list type of selected text
	 * @param listType - "number" for numbered, "bullet" for a bullet list
	 */
	void switchListTo(String listType);

	@MissingDoc
	boolean copySelection();

	/**
	 * Replace selection with a text.
	 * @param text replacement text
	 */
	void setSelectionText(String text);

	/**
	 * @return vertical alignment
	 */
	VerticalAlignment getVerticalAlignment();

	/**
	 * @param alignment vertical alignment
	 */
	void setVerticalAlignment(VerticalAlignment alignment);

	/**
	 * @return horizontal alignment
	 */
	HorizontalAlignment getHorizontalAlignment();

	/**
	 * @param alignment horizontal alignment
	 */
	void setHorizontalAlignment(HorizontalAlignment alignment);

	/**
	 * @return editable element
	 */
	GeoInline getInline();
}
