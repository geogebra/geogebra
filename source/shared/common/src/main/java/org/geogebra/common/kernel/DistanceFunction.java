package org.geogebra.common.kernel;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Evaluates distance between f(T) and distant point for parametric curve f
 *
 */
public interface DistanceFunction extends UnivariateFunction {

	@Override
	double value(double pathParam);

	/**
	 * @param p
	 *            distant point
	 */
	void setDistantPoint(GeoPointND p);

}
