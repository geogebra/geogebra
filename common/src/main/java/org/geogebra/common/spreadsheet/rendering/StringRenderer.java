package org.geogebra.common.spreadsheet.rendering;

import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.factories.AwtFactory;
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
	public void draw(Object data, int fontStyle, int offset, GGraphics2D graphics,
			Rectangle cellBorder) {
		GFont font = baseFont.deriveFont(fontStyle);
		graphics.setFont(font);
		graphics.drawString(data.toString(), cellBorder.getMinX() + offset,
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
