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
	private static final int RULER_DISTANCE = 50;

	/**
	 * 
	 * @param euclidianView
	 *            view
	 */
	public DrawBackground(EuclidianView euclidianView) {
		view = euclidianView;
	}

	/**
	 * Draws the background for MOW.
	 * 
	 * @param g2
	 */
	public void draw(GGraphics2D g2) {
		drawHorizontalLines(g2, 0, 0);
	}
	private void drawHorizontalLines(GGraphics2D g2, double xCrossPix1, double yCrossPix1) {
		EuclidianSettings settings = view.getSettings();
		double xCrossPix = xCrossPix1;

		double gapY = settings.getBackgroundRulerGap();
		double start = view.getYZero() % gapY;

		// draw main grid
		g2.setColor(settings.getBgRulerColor());
		g2.startGeneralPath();
		final double x = xCrossPix + view.getXZero();
		double yEnd = view.getHeight();
		double y = start + gapY;
		double width = view.getWidth();
		while (y <= yEnd) {
			addStraightLineToGeneralPath(g2, x, y, x + width, y);
			y += gapY;
		}
		g2.endAndDrawGeneralPath();
	}

	private static void addStraightLineToGeneralPath(GGraphics2D g2, double x1,
			double y1, double x2, double y2) {
		g2.addStraightLineToGeneralPath(x1, y1, x2, y2);
	}

}
