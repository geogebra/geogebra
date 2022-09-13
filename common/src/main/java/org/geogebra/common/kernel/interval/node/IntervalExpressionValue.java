package org.geogebra.common.kernel.interval.node;

import org.geogebra.common.kernel.interval.Interval;

public interface IntervalExpressionValue {

	boolean isVariable();
	boolean isConstant();
	boolean isLeaf();

	void set(Interval interval);

	void set(double value);
	Interval evaluate();

}
