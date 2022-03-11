package org.geogebra.common.euclidian.plot;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;

/**
 * Class to determine the position of the label belongs to the function curve
 */
public class LabelPositionCalculator {

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
		double xLabel = bounds.toScreenCoordXd(x);
		double yLabel = bounds.toScreenCoordYd(y);
		return new GPoint((int) xLabel, (int) yLabel);
	}
}