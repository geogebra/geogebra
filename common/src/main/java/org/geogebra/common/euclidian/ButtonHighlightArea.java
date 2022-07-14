package org.geogebra.common.euclidian;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.factories.AwtFactory;

/**
 * Paints highlighted area around a button
 */
public class ButtonHighlightArea {

	private final MyButton button;

	/**
	 * @param button
	 *            parent button
	 */
	public ButtonHighlightArea(MyButton button) {
		this.button = button;
	}

	/**
	 * @param g
	 *            graphics
	 * @param widthCorrection
	 *            difference between actual width and user defined width
	 * @param arcSize
	 *            arc size
	 */
	public void draw(GGraphics2D g, double widthCorrection, int arcSize) {
		g.setColor(GColor.HIGHLIGHT_GRAY);
		g.setStroke(AwtFactory.getPrototype().newMyBasicStroke(
				Drawable.UI_ELEMENT_HIGHLIGHT_WIDTH));
		int xStart = button.getX();
		int yStart = button.getY();
		int totalWidth = button.getWidth() + (int) widthCorrection - 1;
		int totalHeight = button.getHeight() - 1;

		g.drawRoundRect(xStart, yStart, totalWidth, totalHeight, arcSize,
				arcSize);
	}
}
