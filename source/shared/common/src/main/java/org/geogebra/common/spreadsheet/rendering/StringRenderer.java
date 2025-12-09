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

package org.geogebra.common.spreadsheet.rendering;

import org.geogebra.common.awt.AwtFactory;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.spreadsheet.core.CellRenderer;
import org.geogebra.common.util.shape.Rectangle;

/**
 * Renderer for plain text cells
 */
public class StringRenderer implements CellRenderer {
	public static final int FONT_SIZE = 14;
	private static final GFont baseFont = AwtFactory.getPrototype()
			.newFont("mathsans", GFont.PLAIN, FONT_SIZE);
	private static final GGraphics2D measuringGraphics = AwtFactory.getPrototype()
			.createBufferedImage(100, 100, true).createGraphics();

	// design suggests 6px from text box in 36px cell,
	// but canvas drawing does not consider text height
	private static final int LINE_HEIGHT = 16;

	@Override
	public void draw(Object data, int fontStyle, double offsetX, GGraphics2D graphics,
			Rectangle cellBorder) {
		GFont font = baseFont.deriveFont(fontStyle);
		graphics.setFont(font);
		graphics.drawString(data.toString(), cellBorder.getMinX() + offsetX,
				cellBorder.getMaxY() - (cellBorder.getHeight() - LINE_HEIGHT) / 2
						- font.getSize() / 4.0);
	}

	@Override
	public boolean match(Object renderable) {
		return renderable instanceof String;
	}

	@Override
	public double measure(Object data, int fontStyle) {
		GFont font = baseFont.deriveFont(fontStyle);
		return (int) AwtFactory.getPrototype().newTextLayout(data.toString(),
				font, measuringGraphics.getFontRenderContext()).getAdvance();
	}
}
