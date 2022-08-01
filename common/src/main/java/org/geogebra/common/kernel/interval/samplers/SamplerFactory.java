package org.geogebra.common.kernel.interval.samplers;

import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;
import org.geogebra.common.kernel.geos.GeoFunction;

public interface SamplerFactory {
	/**
	 *
	 * @param function to sample
	 * @param bounds of the view
	 * @return the new sampler created.
	 */
	IntervalFunctionSampler create(GeoFunction function, EuclidianViewBounds bounds);
}
