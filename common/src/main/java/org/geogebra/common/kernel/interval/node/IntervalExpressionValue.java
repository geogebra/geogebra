package org.geogebra.common.kernel.interval.node;

import org.geogebra.common.kernel.interval.Interval;

public interface IntervalExpressionValue {

	boolean isVariable();
	boolean isConstant();
	boolean isLeaf();

	boolean isNode();

	IntervalExpressionNode wrap();
	IntervalExpressionValue unwrap();

	void set(Interval interval);

	void set(double value);
	Interval value();

}
