package org.geogebra.common.euclidian.plot;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;

/**
 * Class to determine the position of the label belongs to the function curve
 */
public class LabelPositionCalculator {

	private static final int X_GAP = 10;
	private static final int X_THRESHOLD_LEFT = 20;
	private static final int X_MARGIN_LEFT = 5;
	private static final int X_THRESHOLD_RIGHT = 30;
	private static final int X_MARGIN_RIGHT = 15;
	private static final int Y_GAP = 15;
	private static final int Y_THRESHOLD_TOP = 40;
	private static final int Y_TOP_MARGIN = 15;
	private static final int Y_THRESHOLD_BOTTOM = 30;
	private static final int Y_MARGIN_BOTTOM = 5;

	private final EuclidianViewBounds bounds;

	public LabelPositionCalculator(EuclidianViewBounds bounds) {
		this.bounds = bounds;
	}

	/**
	 * Gives the position of label based on the first point of the function.
	 * @param x coordinate of function point
	 * @param y coordinate of function point
	 * @return the position of the label
	 */
	public GPoint calculate(double x, double y) {
		double xLabel = bounds.toScreenCoordXd(x) + X_GAP;
		if (xLabel < X_THRESHOLD_LEFT) {
			xLabel = X_MARGIN_LEFT;
		}

		if (xLabel > bounds.getWidth() - X_THRESHOLD_RIGHT) {
			xLabel = bounds.getWidth() - X_MARGIN_RIGHT;
		}

		double yLabel = bounds.toScreenCoordYd(y) + Y_GAP;
		if (yLabel < Y_THRESHOLD_TOP) {
			yLabel = Y_TOP_MARGIN;
		} else if (yLabel > bounds.getHeight() - Y_THRESHOLD_BOTTOM) {
			yLabel = bounds.getHeight() - Y_MARGIN_BOTTOM;
		}

		return new GPoint((int) xLabel, (int) yLabel);
	}
}