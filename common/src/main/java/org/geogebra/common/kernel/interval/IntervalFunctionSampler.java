package org.geogebra.common.kernel.interval;

import org.geogebra.common.kernel.geos.GeoFunction;

public interface IntervalFunctionSampler extends IntervalEvaluatable {

	IntervalTupleList result();
	void update(IntervalTuple range);
	IntervalTupleList extendDomain(double min, double max);

	void setInterval(double low, double high);

	GeoFunction getGeoFunction();
}
