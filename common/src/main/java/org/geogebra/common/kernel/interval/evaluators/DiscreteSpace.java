package org.geogebra.common.kernel.interval.evaluators;

import java.util.stream.Stream;

import org.geogebra.common.kernel.interval.Interval;

public interface DiscreteSpace {
	void update(Interval interval, int count);

	Stream<Interval> values();

	DiscreteSpace difference(double low, double high);

	void setInterval(double min, double max);

	double getStep();
}
