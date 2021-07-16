package org.geogebra.common.euclidian;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.factories.AwtFactory;

/**
 * Paints highligted area around a button
 */
public class ButtonHighlightArea {
	private static final int OUTLINE_WIDTH = 4;
	/** distance between button border and middle of the outline stroke */
	private static final int HALO_WIDTH = 4 + OUTLINE_WIDTH / 2;

	private MyButton button;

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
		g.setColor(GColor.newColor(0, 0, 0, 50));
		g.setStroke(AwtFactory.getPrototype().newBasicStroke(OUTLINE_WIDTH));
		int xStart = button.getX() - HALO_WIDTH;
		int yStart = button.getY() - HALO_WIDTH;
		int totalWidth = button.getWidth() + (int) widthCorrection + 2 * HALO_WIDTH;
		int totalHeight = button.getHeight() + 2 * HALO_WIDTH;
		g.fillRoundRect(xStart, yStart, totalWidth, totalHeight, arcSize,
				arcSize);
		g.setColor(GColor.newColor(255, 255, 255, 128));

		g.drawRoundRect(xStart, yStart, totalWidth, totalHeight, arcSize,
				arcSize);
	}
}
