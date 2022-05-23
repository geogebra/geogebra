package org.geogebra.common.kernel.interval;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;

public interface IntervalEvaluator {
	void setNode(ExpressionNode node);

	boolean isAccepted();

	Interval evaluate(Interval x);
}
