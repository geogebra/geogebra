package org.geogebra.common.spreadsheet.rendering;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.spreadsheet.core.CellRenderer;
import org.geogebra.common.spreadsheet.style.CellFormat;
import org.geogebra.common.util.shape.Rectangle;

public final class SelfRenderable {

	public static final int HORIZONTAL_PADDING = 6;
	private final CellRenderer renderer;
	private final Object renderable;
	private final GColor background;
	private final int fontStyle;
	private final int alignment;
	private final double width;

	/**
	 * @param renderer renderer
	 * @param renderable cached renderable value
	 * @param fontStyle optional font style ({@link GFont#getStyle})
	 * @param alignment text alignment
	 */
	public SelfRenderable(CellRenderer renderer, Integer fontStyle,
			Integer alignment, Object renderable) {
		this(renderer, fontStyle, alignment, renderable, null);
	}

	/**
	 * @param renderer renderer
	 * @param renderable cached renderable value
	 * @param fontStyle optional font style ({@link GFont#getStyle})
	 * @param alignment text alignment
	 * @param background background color
	 */
	public SelfRenderable(CellRenderer renderer, Integer fontStyle,
			Integer alignment, Object renderable, GColor background) {
		this.renderer = renderer;
		this.renderable = renderable;
		this.background = background;
		this.alignment = alignment == null ? CellFormat.ALIGN_RIGHT : alignment;
		this.fontStyle = fontStyle == null ? GFont.PLAIN : fontStyle;
		if (this.alignment != CellFormat.ALIGN_LEFT) {
			width = renderer.measure(renderable, this.fontStyle);
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
			offset = (cellBorder.getWidth() - width) - HORIZONTAL_PADDING;
		}
		renderer.draw(renderable, fontStyle, offset, graphics, cellBorder);
	}

	public GColor getBackground() {
		return background;
	}
}
