package org.geogebra.common.kernel.interval;

public interface IntervalEvaluation {
	IntervalTupleList evaluateBetween(double low, double high);

	IntervalTupleList evaluate(Interval x);

	IntervalTupleList evaluateOnSpace(DiscreteSpace space);
}
