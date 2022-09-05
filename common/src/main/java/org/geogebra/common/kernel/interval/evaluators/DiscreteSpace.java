package org.geogebra.common.kernel.interval.evaluators;

import java.util.function.Consumer;

import org.geogebra.common.kernel.interval.Interval;

public interface DiscreteSpace {

	void update(Interval interval, int count);

	Interval getDomain();

	void forEach(Consumer<Interval> action);

	void expandLeft(int count);

	void expandRight(int count);
}
