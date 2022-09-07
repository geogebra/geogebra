package org.geogebra.common.kernel.interval.evaluators;

import java.util.function.Consumer;

import org.geogebra.common.kernel.interval.Interval;

public interface DiscreteSpace {

	Interval head();

	Interval tail();

	void update(Interval interval, int count);

	void moveLeft();

	void moveRight();

	void forEach(Consumer<Interval> action);
}
