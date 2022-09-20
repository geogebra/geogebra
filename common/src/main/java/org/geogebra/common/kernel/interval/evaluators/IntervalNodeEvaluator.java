package org.geogebra.common.kernel.interval.evaluators;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.node.IntervalExpressionNode;

public interface IntervalNodeEvaluator {
	void setNode(IntervalExpressionNode node);
	Interval value(Interval x);
}
