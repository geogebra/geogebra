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

package org.geogebra.common.awt;

import org.geogebra.common.awt.font.GTextLayout;
import org.geogebra.common.util.StringUtil;
import org.geogebra.ggbjdk.java.awt.geom.Rectangle;

public class TextLayoutCommon implements GTextLayout {

	private final GFont font;
	private final String string;

	/**
	 * @param font font
	 * @param string string
	 */
	public TextLayoutCommon(GFont font, String string) {
		this.font = font;
		this.string = string;
	}

	@Override
	public double getAdvance() {
		return new StringUtil().estimateLength(string, font);
	}

	@Override
	public GRectangle2D getBounds() {
		return new Rectangle(0, 0, 1, 1);
	}

	@Override
	public double getAscent() {
		return 0.8 * font.getSize();
	}

	@Override
	public void draw(GGraphics2D g2, int x, int y) {
		// TODO Auto-generated method stub
	}

	@Override
	public double getDescent() {
		return 0.2 * font.getSize();
	}

}
