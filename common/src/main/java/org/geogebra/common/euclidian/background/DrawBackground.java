package org.geogebra.common.euclidian.background;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.settings.EuclidianSettings;

/**
 * Helper class for drawing the grid
 * 
 * @author zbynek
 *
 */
public class DrawBackground {
	private EuclidianView view;
	private EuclidianSettings settings;
	private static final int RULER_DISTANCE = 50;

	/**
	 * 
	 * @param euclidianView
	 *            view
	 */
	public DrawBackground(EuclidianView euclidianView) {
		view = euclidianView;
		settings = view.getSettings();
	}

	/**
	 * Draws the background for MOW.
	 * 
	 * @param g2
	 */
	public void draw(GGraphics2D g2) {
		switch (settings.getBackgroundType()) {
		case RULER:
			drawHorizontalLines(g2, 0, 0);
			break;
		case SQUARE_BIG:
			drawSquaredBackground(g2, 0, 0);
			break;
		case SQUARE_SMALL:
			break;
		case SVG:
			break;
		default:
			break;
		}

	}
	private void drawHorizontalLines(GGraphics2D g2, double xCrossPix1, double yCrossPix1) {
		double xCrossPix = xCrossPix1;

		double gapY = settings.getBackgroundRulerGap();
		double start = view.getYZero() % gapY;

		// draw main grid
		g2.setColor(settings.getBgRulerColor());
		g2.startGeneralPath();
		final double x = xCrossPix + view.getXZero();
		double yEnd = view.getHeight();
		double y = start + gapY;
		double width = view.getWidth() - (view.getWidth() % gapY);
		while (y <= yEnd) {
			addStraightLineToGeneralPath(g2, x, y, x + width, y);
			y += gapY;
		}
		g2.endAndDrawGeneralPath();
	}

	private void drawVerticalLines(GGraphics2D g2, double xCrossPix1,
			double yCrossPix1) {
		double xCrossPix = xCrossPix1;

		double gapY = settings.getBackgroundRulerGap();
		double start = view.getYZero() % gapY;

		// draw main grid
		g2.setColor(settings.getBgRulerColor());
		g2.startGeneralPath();
		double x = xCrossPix + view.getXZero();
		double xEnd = x + view.getWidth();
		double y = start + gapY;
		double height = view.getHeight();

		while (x <= xEnd) {
			addStraightLineToGeneralPath(g2, x, y, x, y + height);
			x += gapY;
		}
		g2.endAndDrawGeneralPath();
	}

	private void drawSquaredBackground(GGraphics2D g2, double xCrossPix1,
			double yCrossPix1) {
		drawHorizontalLines(g2, xCrossPix1, yCrossPix1);
		drawVerticalLines(g2, xCrossPix1, yCrossPix1);
	}

	private static void addStraightLineToGeneralPath(GGraphics2D g2, double x1,
			double y1, double x2, double y2) {
		g2.addStraightLineToGeneralPath(x1, y1, x2, y2);
	}

}
