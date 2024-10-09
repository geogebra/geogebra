package org.geogebra.common.main.settings;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.geos.XMLBuilder;

public class PenToolsSettings extends AbstractSettings {

	private GColor lastSelectedPenColor = GColor.BLACK;
	private GColor lastSelectedHighlighterColor = GColor.newColorRGB(0x388C83);
	private int lastPenThickness = EuclidianConstants.DEFAULT_PEN_SIZE;
	private int lastHighlighterThinckness = EuclidianConstants.DEFAULT_HIGHLIGHTER_SIZE;
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
		return lastHighlighterThinckness;
	}

	/**
	 * @param lastHighlighterThickness size of highlighter
	 */
	public void setLastHighlighterThickness(int lastHighlighterThickness) {
		this.lastHighlighterThinckness = lastHighlighterThickness;
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
	public void getXML(StringBuilder sbxml) {
		// size of pen
		sbxml.append("\t<penSize val=\"");
		sbxml.append(getLastPenThickness());
		sbxml.append("\"/>\n");

		// color of pen
		sbxml.append("\t<penColor");
		XMLBuilder.appendRGB(sbxml, getLastSelectedPenColor());
		sbxml.append("/>\n");

		// size of highlighter
		sbxml.append("\t<highlighterSize val=\"");
		sbxml.append(getLastHighlighterThickness());
		sbxml.append("\"/>\n");

		// highlighter of pen
		sbxml.append("\t<highlighterColor");
		XMLBuilder.appendRGB(sbxml, getLastSelectedHighlighterColor());
		sbxml.append("/>\n");

		// size of eraser
		sbxml.append("\t<eraserSize val=\"");
		sbxml.append(getDeleteToolSize());
		sbxml.append("\"/>\n");
	}
}
