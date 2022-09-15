package org.geogebra.common.kernel.interval.node;

import org.geogebra.common.kernel.interval.Interval;

public interface IntervalExpressionValue extends IntervalExpression {

	void set(Interval interval);

	void set(double value);
}
