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