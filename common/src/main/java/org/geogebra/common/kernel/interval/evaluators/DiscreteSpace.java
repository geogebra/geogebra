package org.geogebra.common.kernel.interval.evaluators;

import java.util.stream.Stream;

import org.geogebra.common.kernel.interval.Interval;

public interface DiscreteSpace {
	void update(Interval interval, int count);

	DiscreteSpace diffMax(double max);

	DiscreteSpace diffMin(double min);

	Stream<Interval> values();

	void setInterval(double min, double max);

	double getStep();
}
