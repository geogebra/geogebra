package org.geogebra.common.kernel.interval;

import java.util.List;

import org.geogebra.common.kernel.geos.GeoFunction;

public interface IntervalFunctionSampler extends IntervalEvaluatable {

	/**
	 * Gets the samples with the predefined range and sample rate
	 *
	 * @return the sample list
	 */
	IntervalTupleList result();

	List<IntervalTupleList> results();

	void update(IntervalTuple range);

	IntervalTupleList extendDomain(double min, double max);

	void setInterval(double low, double high);

	GeoFunction getGeoFunction();
}
