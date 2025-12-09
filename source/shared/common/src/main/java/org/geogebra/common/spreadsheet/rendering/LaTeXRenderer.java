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

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.spreadsheet.core.CellRenderer;
import org.geogebra.common.util.shape.Rectangle;

import com.himamis.retex.renderer.share.TeXIcon;

/**
 * Renderer for LaTeX cells
 */
public final class LaTeXRenderer implements CellRenderer {

	private AwtReTeXGraphicsBridge bridge;

	public LaTeXRenderer(AwtReTeXGraphicsBridge bridge) {
		this.bridge = bridge;
	}

	@Override
	public void draw(Object data, int fontStyle, double offsetX,
			GGraphics2D graphics, Rectangle cellBorder) {
		graphics.setColor(GColor.BLACK);
		TeXIcon teXIcon = (TeXIcon) data;
		teXIcon.paintIcon(null, bridge.convert(graphics),
				cellBorder.getMinX() + offsetX, cellBorder.getMinY()
						+ (cellBorder.getHeight() - teXIcon.getIconHeight()) / 2);
	}

	@Override
	public boolean match(Object renderable) {
		return renderable instanceof TeXIcon;
	}

	@Override
	public double measure(Object renderable, int fontStyle) {
		return ((TeXIcon) renderable).getIconWidth();
	}
}
