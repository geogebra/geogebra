package org.geogebra.common.gui.util;

import static org.geogebra.common.gui.util.DropDownList.BOX_ROUND;
import static org.geogebra.common.gui.util.DropDownList.MAX_WIDTH;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.geos.GeoElement;

public class DrawSelectedItem {
	private final GRectangle ctrlRect;

	/**
	 * Constructor
	 */
	public DrawSelectedItem() {
		ctrlRect = AwtFactory.getPrototype().newRectangle();
	}

	/**
	 * Draws the triangle to open the dropdown
	 *
	 * @param g2 {@link GGraphics2D}
	 * @param boxLeft left of the visible box
	 * @param boxTop top of the visible box
	 * @param boxWidth width of the visible box
	 * @param boxHeight height of the visible box
	 */
	public void drawOpenControl(GGraphics2D g2, int boxLeft, int boxTop, int boxWidth,
			int boxHeight) {
		g2.setPaint(GColor.BLACK);
		int left = boxLeft + boxWidth - boxHeight;
		ctrlRect.setBounds(boxLeft, boxTop, boxWidth, boxHeight);
		drawTriangle(g2, left, boxTop, boxHeight);
	}

	/**
	 * Draw the bounds of the dropdown
	 *
	 * @param geo that represents the list
	 * @param g2 {@link GGraphics2D}
	 * @param bgColor the background color
	 * @param left of dropdown
	 * @param top of dropdown
	 * @param width of dropdown
	 * @param height of dropdown
	 */
	public void drawBounds(GeoElement geo, GGraphics2D g2, GColor bgColor,
			int left, int top, int width, int height) {
		g2.setPaint(bgColor);
		g2.fillRoundRect(left, top, width, height, BOX_ROUND, BOX_ROUND);

		// TF Rectangle
		g2.setPaint(geo.doHighlighting() ? GColor.BLUE : GColor.BLACK);
		g2.drawRoundRect(left, top, width, height, BOX_ROUND, BOX_ROUND);
	}

	/**
	 * @param g2
	 *            graphics
	 * @param left
	 *            left
	 * @param top
	 *            top
	 * @param size
	 *            size
	 */
	public void drawTriangle(GGraphics2D g2, int left, int top, int size) {
		g2.setColor(GColor.DARK_GRAY);

		int middleX = left + size / 2;

		int w = Math.min(size, MAX_WIDTH);
		int tW = w / 4;
		int tH = w / 6;

		int middleY = top + (size / 2 - (int) Math.round(tH * 1.5));

		int x1 = middleX - tW;
		int y1 = middleY + tH;
		int x2 = middleX + tW;
		int y3 = middleY + 2 * tW;
		AwtFactory.fillTriangle(g2, x1, y1, x2, y1, middleX, y3);
	}

	/**
	 * @param x
	 *            mouse x-coordinate
	 * @param y
	 *            mouse y-coordinate
	 * @return whether control rectangle was hit
	 */
	public boolean isOpenButtonHit(int x, int y) {
		return ctrlRect != null && ctrlRect.contains(x, y);
	}
}
