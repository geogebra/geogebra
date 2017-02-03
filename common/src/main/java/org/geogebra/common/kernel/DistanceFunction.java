package org.geogebra.common.kernel;

import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.roots.RealRootFunction;

/**
 * Evaluates distance between f(T) and distant point for parametric curve f
 *
 */
public interface DistanceFunction extends RealRootFunction {

	@Override
	double evaluate(double pathParam);

	/**
	 * @param p
	 *            distant point
	 */
	void setDistantPoint(GeoPointND p);

}
