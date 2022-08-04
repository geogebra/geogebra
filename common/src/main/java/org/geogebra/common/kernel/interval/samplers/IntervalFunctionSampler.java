package org.geogebra.common.kernel.interval.samplers;

import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.evaluators.IntervalEvaluatable;
import org.geogebra.common.kernel.interval.function.IntervalTuple;
import org.geogebra.common.kernel.interval.function.IntervalTupleList;

public interface IntervalFunctionSampler extends IntervalEvaluatable {

	IntervalTupleList result();

	void update(IntervalTuple range);

	IntervalTupleList extendDomain(double min, double max);

	GeoFunction getGeoFunction();

	Interval getDomain();
}
