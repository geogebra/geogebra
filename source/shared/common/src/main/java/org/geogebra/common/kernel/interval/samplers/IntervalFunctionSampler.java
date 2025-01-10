package org.geogebra.common.kernel.interval.samplers;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.function.IntervalTupleList;

public interface IntervalFunctionSampler {

	IntervalTupleList tuples();

	void extend(Interval domain);

	void resample(Interval domain);
}
