package org.geogebra.common.kernel.interval.samplers;

import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.function.IntervalTuple;
import org.geogebra.common.kernel.interval.function.IntervalTupleList;

public interface IntervalFunctionSampler {

	IntervalTupleList tuples();

	void pan(Interval domain);

	GeoFunction getGeoFunction();

	IntervalTuple at(int index);

	boolean isEmpty();

	void resample(Interval domain);
}
