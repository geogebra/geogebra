package org.geogebra.common.kernel.kernelND;

import org.geogebra.common.kernel.Path;

public interface Parametrizable extends Path {
	/**
	 * Sets curve to this conic
	 * 
	 * @param curve
	 *            curve for storing this conic
	 */
	void toGeoCurveCartesian(GeoCurveCartesianND curve);
}
