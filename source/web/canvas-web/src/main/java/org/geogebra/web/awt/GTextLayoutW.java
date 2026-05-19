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

package org.geogebra.web.awt;

import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.awt.font.GTextLayout;
import org.geogebra.common.awt.font.TextSizeUtil;
import org.geogebra.ggbjdk.java.awt.geom.Rectangle;

public class GTextLayoutW implements GTextLayout {

	private final GFont font;
	private final String str;
	private final GFontRenderContextW frc;
	private final double aboveBaselineRatio;
	private int advance = -1;

	/**
	 * Creates a layout for given text.
	 *
	 * @param str
	 *            string
	 * @param font
	 *            font
	 * @param frc
	 *            font context
	 */
	public GTextLayoutW(String str, GFont font, GFontRenderContextW frc) {
		this.font = font;
		this.str = str;
		this.frc = frc;
		this.aboveBaselineRatio = TextSizeUtil.getAboveBaselineRatio(str);
	}

	@Override
	public double getAdvance() {
		if (advance < 0 && frc != null) {
			advance = frc.measureText(str, ((GFontW) font).getFullFontString());
		}
		return advance;
	}

	@Override
	public GRectangle2D getBounds() {
		return new Rectangle((int) getAdvance(), (int) getAscent());
	}

	/**
	 * Estimates ascent of the text based on 2 (wrong) assumptions
	 * - ascent + descent = font size
	 * - ascent / descent only depends on letters with descenders
	 * @return estimated ascent
	 */
	@Override
	public double getAscent() {
		return font.getSize() * aboveBaselineRatio;
	}

	/**
	 * @see #getAscent
	 * @return estimated descent
	 */
	@Override
	public double getDescent() {
		return font.getSize() * (1 - aboveBaselineRatio);
	}

	@Override
	public void draw(GGraphics2D g2, int x, int y) {
		GFont tempFont = g2.getFont();
		g2.setFont(font);
		g2.drawString(str, x, y);
		g2.setFont(tempFont);
	}

}
