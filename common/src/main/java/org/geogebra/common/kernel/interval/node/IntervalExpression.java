package org.geogebra.common.kernel.interval.node;

import org.geogebra.common.kernel.interval.Interval;

public interface IntervalExpression {

	boolean isLeaf();
	boolean isNode();
	IntervalExpressionNode wrap();
	IntervalExpression unwrap();
	Interval value();

}
