package org.geogebra.common.kernel.interval.node;

import org.geogebra.common.kernel.interval.Interval;

public class IntervalFunctionVariable implements IntervalExpressionValue {
	private final Interval interval = new Interval();

	@Override
	public boolean isLeaf() {
		return true;
	}

	@Override
	public IntervalExpressionNode asExpressionNode() {
		return null;
	}

	@Override
	public void set(Interval other) {
		interval.set(other);
	}

	@Override
	public void set(double value) {
		interval.set(value);
	}

	@Override
	public Interval value() {
		return new Interval(interval);
	}

	@Override
	public boolean hasFunctionVariable() {
		return true;
	}

	@Override
	public IntervalNode simplify() {
		return this;
	}

	@Override
	public String toString() {
		return "[x]";
	}
}
