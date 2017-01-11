package org.geogebra.common.kernel;

import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.roots.RealRootFunction;

public interface DistanceFunction extends RealRootFunction {

	@Override
	double evaluate(double pathParam);

	/**
	 * @param p
	 *            distant point
	 */
	void setDistantPoint(GeoPointND p);

}
