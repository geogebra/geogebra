package org.geogebra.common.kernel.interval.evaluators;

import java.util.function.Consumer;

import org.geogebra.common.kernel.interval.Interval;

/**
 * A collection of consecutive intervals.
 */
public interface DiscreteSpace {

	void rescale(Interval interval, int count);

	void extendLeft(Interval domain, ExtendSpace cb);

	void extendRight(Interval domain, ExtendSpace cb);

	void extend(Interval domain, ExtendSpace cbLeft, ExtendSpace cbRight);

	void forEach(Consumer<Interval> action);
}
