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

package org.geogebra.common.gui.dialog.handler;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.kernel.geos.properties.TextFontSize;

public class TextStyle {
	private boolean isBold;
	private boolean isItalic;
	private boolean isSerif;
	private boolean isLatex;
	private GColor bgColor;
	private GColor fontColor;
	private TextFontSize textSize;

	public boolean isItalic() {
		return isItalic;
	}

	public void setItalic(boolean italic) {
		isItalic = italic;
	}

	public boolean isBold() {
		return isBold;
	}

	public void setBold(boolean bold) {
		isBold = bold;
	}

	public boolean isSerif() {
		return isSerif;
	}

	public void setSerif(boolean serif) {
		isSerif = serif;
	}

	public boolean isLatex() {
		return isLatex;
	}

	public void setLatex(boolean latex) {
		isLatex = latex;
	}

	public GColor getBgColor() {
		return bgColor;
	}

	public void setBgColor(GColor bgColor) {
		this.bgColor = bgColor;
	}

	public GColor getFontColor() {
		return fontColor;
	}

	public void setFontColor(GColor fontColor) {
		this.fontColor = fontColor;
	}

	public TextFontSize getTextSize() {
		return textSize;
	}

	public void setTextSize(TextFontSize textSize) {
		this.textSize = textSize;
	}
}
