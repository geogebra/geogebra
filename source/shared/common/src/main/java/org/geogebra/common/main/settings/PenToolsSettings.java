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

package org.geogebra.common.main.settings;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.io.XMLStringBuilder;
import org.geogebra.common.kernel.geos.XMLBuilder;

public class PenToolsSettings extends AbstractSettings {

	private GColor lastSelectedPenColor = GColor.BLACK;
	private GColor lastSelectedHighlighterColor = GColor.newColorRGB(0x388C83);
	private int lastPenThickness = EuclidianConstants.DEFAULT_PEN_SIZE;
	private int lastHighlighterThickness = EuclidianConstants.DEFAULT_HIGHLIGHTER_SIZE;
	private int lastPenOpacity = 255;
	private int deleteToolSize = EuclidianConstants.DEFAULT_ERASER_SIZE;

	/**
	 * @return last selected pen color
	 */
	public GColor getLastSelectedPenColor() {
		return lastSelectedPenColor;
	}

	/**
	 * @param lastSelectedPenColor
	 *            update last selected pen color
	 */
	public void setLastSelectedPenColor(GColor lastSelectedPenColor) {
		this.lastSelectedPenColor = lastSelectedPenColor;
		notifyListeners();
	}

	/**
	 * @return last selected highlighter color
	 */
	public GColor getLastSelectedHighlighterColor() {
		return lastSelectedHighlighterColor;
	}

	/**
	 * @param lastSelectedHighlighterColor
	 *            update last selected highlighter color
	 */
	public void setLastSelectedHighlighterColor(GColor lastSelectedHighlighterColor) {
		this.lastSelectedHighlighterColor = lastSelectedHighlighterColor;
		notifyListeners();
	}

	/**
	 * @return last selected size for pen
	 */
	public int getLastPenThickness() {
		return lastPenThickness;
	}

	/**
	 * @param lastPenThickness size of pen
	 */
	public void setLastPenThickness(int lastPenThickness) {
		this.lastPenThickness = lastPenThickness;
		notifyListeners();
	}

	/**
	 * @return last selected size of highlighter
	 */
	public int getLastHighlighterThickness() {
		return lastHighlighterThickness;
	}

	/**
	 * @param lastHighlighterThickness size of highlighter
	 */
	public void setLastHighlighterThickness(int lastHighlighterThickness) {
		this.lastHighlighterThickness = lastHighlighterThickness;
		notifyListeners();
	}

	public void setLastPenOpacity(int lineOpacity) {
		lastPenOpacity = lineOpacity;
	}

	public int getLastPenOpacity() {
		return lastPenOpacity;
	}

	/**
	 * @return delete tool size
	 */
	public int getDeleteToolSize() {
		return this.deleteToolSize;
	}

	/**
	 * @param size
	 *            delete tool size
	 */
	public void setDeleteToolSize(int size) {
		this.deleteToolSize = size;
		notifyListeners();
	}

	/**
	 * Print pen tool settings to XML
	 * @param sbxml output string builder
	 */
	public void getXML(XMLStringBuilder sbxml) {
		// size of pen
		sbxml.startTag("penSize").attr("val", getLastPenThickness()).endTag();

		// color of pen
		sbxml.startTag("penColor");
		XMLBuilder.appendRGB(sbxml, getLastSelectedPenColor());
		sbxml.endTag();

		// size of highlighter
		sbxml.startTag("highlighterSize")
				.attr("val", getLastHighlighterThickness()).endTag();

		// highlighter of pen
		sbxml.startTag("highlighterColor");
		XMLBuilder.appendRGB(sbxml, getLastSelectedHighlighterColor());
		sbxml.endTag();

		// size of eraser
		sbxml.startTag("eraserSize").attr("val", getDeleteToolSize()).endTag();
	}
}
