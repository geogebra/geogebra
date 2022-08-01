package org.geogebra.common.kernel.interval.samplers;

import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.interval.function.IntervalTuple;

public class ConditionalSamplerFactory implements SamplerFactory {

	@Override
	public IntervalFunctionSampler create(GeoFunction function,	EuclidianViewBounds bounds) {
		IntervalTuple range = new IntervalTuple(bounds.domain(), bounds.range());
		return new ConditionalFunctionSampler(function, range, bounds);
	}
}
