package org.geogebra.common.kernel.interval.evaluators;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.function.IntervalTupleList;

public interface IntervalEvaluatable {

	/**
	 * Evaluate on interval x with the same step that used before
	 * @param x the interval to be evaluated on.
	 * @return tuples evaluated on x.
	 */
	IntervalTupleList evaluate(Interval x);

	/**
	 * Evaluate on interval [high, low] with the same step that used before
	 * @param low  lower bound
	 * @param high higher bound
	 * @return tuples evaluated on [low, high].
	 */
	IntervalTupleList evaluate(double low, double high);

	/**
	 * Evaluate on {@link DiscreteSpace}
	 *
	 * @param space to evaluate on.
	 * @return tuples evaluated on space.
	 */
	IntervalTupleList evaluate(DiscreteSpace space);
}
