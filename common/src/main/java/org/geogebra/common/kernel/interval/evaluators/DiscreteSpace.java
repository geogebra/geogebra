package org.geogebra.common.kernel.interval.evaluators;

import java.util.stream.Stream;

import org.geogebra.common.kernel.interval.Interval;

public interface DiscreteSpace {
	Stream<Interval> values();

	Stream<Interval> values(double low, double high);

	void update(Interval interval, int count);

	void setInterval(double min, double max);
}
