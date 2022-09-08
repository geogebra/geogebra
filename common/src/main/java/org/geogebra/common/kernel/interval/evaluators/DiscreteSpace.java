package org.geogebra.common.kernel.interval.evaluators;

import java.util.function.Consumer;

import org.geogebra.common.kernel.interval.Interval;

public interface DiscreteSpace {

	void update(Interval interval, int count);

	void extendLeft(Interval domain, ExtendSpace cb);

	void extendRight(Interval domain, ExtendSpace cb);

	void forEach(Consumer<Interval> action);

}
