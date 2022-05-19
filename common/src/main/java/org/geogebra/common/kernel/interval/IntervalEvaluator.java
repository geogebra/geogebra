package org.geogebra.common.kernel.interval;

public interface IntervalEvaluator {
	boolean isAccepted();

	Interval evaluate(Interval x);
}
