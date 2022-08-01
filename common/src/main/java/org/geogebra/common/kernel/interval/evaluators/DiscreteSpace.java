package org.geogebra.common.kernel.interval.evaluators;

import java.util.stream.Stream;

import org.geogebra.common.kernel.interval.Interval;

public interface DiscreteSpace {
	void update(Interval interval, int count);

	Stream<Interval> values();

	Stream<Interval> values(double low, double high);

	DiscreteSpace difference(double low, double high);

	void extend(DiscreteSpace subspace);

	void setInterval(double min, double max);

	double getStep();
}
