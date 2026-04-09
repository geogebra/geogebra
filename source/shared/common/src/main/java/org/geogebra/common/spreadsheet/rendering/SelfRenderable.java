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

import static org.geogebra.common.spreadsheet.style.SpreadsheetStyling.DEFAULT_CELL_ALIGNMENT;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.spreadsheet.core.CellRenderer;
import org.geogebra.common.spreadsheet.style.CellFormat;
import org.geogebra.common.spreadsheet.style.SpreadsheetStyling;
import org.geogebra.common.util.shape.Rectangle;

public final class SelfRenderable {

	public static final int HORIZONTAL_PADDING = 6;
	private final CellRenderer renderer;
	private final Object renderable;
	private final GColor background;
	private final GColor textColor;
	private final int fontStyle;
	private final int alignment;
	private final double width;
	private final double fontSize;

	/**
	 * @param renderer renderer
	 * @param renderable cached renderable value
	 * @param fontSize font size in points
	 * @param fontStyle optional font style ({@link GFont#getStyle})
	 * @param alignment text alignment
	 */
	public SelfRenderable(CellRenderer renderer, double fontSize, Integer fontStyle,
			Integer alignment, Object renderable) {
		this(renderer, fontSize, fontStyle, alignment, renderable, null,
				SpreadsheetStyling.getDefaultTextColor());
	}

	/**
	 * @param renderer renderer
	 * @param fontSize fontSize in points
	 * @param renderable cached renderable value
	 * @param fontStyle optional font style ({@link GFont#getStyle})
	 * @param alignment text alignment
	 * @param background background color
	 * @param textColor text color
	 */
	public SelfRenderable(CellRenderer renderer, double fontSize, Integer fontStyle,
			Integer alignment, Object renderable, GColor background, GColor textColor) {
		this.renderer = renderer;
		this.renderable = renderable;
		this.background = background;
		this.textColor = textColor;
		this.alignment = alignment == null ? DEFAULT_CELL_ALIGNMENT : alignment;
		this.fontStyle = fontStyle == null ? GFont.PLAIN : fontStyle;
		this.fontSize = fontSize;
		if (this.alignment != CellFormat.ALIGN_LEFT) {
			width = renderer.measureWidth(renderable, this.fontStyle, fontSize);
		} else {
			width = 0;
		}
	}

	/**
	 * Align and render the content
	 * @param graphics target graphics
	 * @param cellBorder cell dimensions
	 */
	public void draw(GGraphics2D graphics, Rectangle cellBorder) {
		double offset = HORIZONTAL_PADDING;
		if (alignment == CellFormat.ALIGN_CENTER) {
			offset = (cellBorder.getWidth() - width) / 2;
		} else if (alignment == CellFormat.ALIGN_RIGHT) {
			offset = cellBorder.getWidth() - width - HORIZONTAL_PADDING;
		}
		graphics.setClip(cellBorder.getMinX(), cellBorder.getMinY(),
				cellBorder.getWidth(), cellBorder.getHeight());
		renderer.draw(renderable, fontSize, fontStyle, offset, graphics, cellBorder);
		graphics.resetClip();
	}

	public GColor getBackground() {
		return background;
	}

	public GColor getTextColor() {
		return textColor;
	}
}
