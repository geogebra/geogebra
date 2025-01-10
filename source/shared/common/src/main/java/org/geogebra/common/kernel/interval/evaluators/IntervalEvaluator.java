package org.geogebra.common.kernel.interval.evaluators;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.interval.Interval;

public interface IntervalEvaluator {
	void setNode(ExpressionNode node);

	boolean isAccepted();

	Interval evaluate(Interval x);
}
