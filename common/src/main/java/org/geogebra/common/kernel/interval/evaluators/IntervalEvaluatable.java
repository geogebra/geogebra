package org.geogebra.common.kernel.interval.evaluators;

import org.geogebra.common.kernel.interval.function.IntervalTupleList;

public interface IntervalEvaluatable {

	/**
	 * Evaluate on {@link DiscreteSpace}
	 *
	 * @param space to evaluate on.
	 * @return tuples evaluated on space.
	 */
	IntervalTupleList evaluate(DiscreteSpace space);
}
