package org.geogebra.common.kernel.interval.node;

import org.geogebra.common.kernel.interval.Interval;

public class IntervalFunctionValue implements IntervalExpressionValue {
	private final Interval interval;

	public IntervalFunctionValue(Interval interval) {
		this.interval = interval;
	}

	@Override
	public void set(Interval interval) {
		this.interval.set(interval);
	}

	@Override
	public void set(double value) {
		this.interval.set(value, value);
	}

	@Override
	public boolean isVariable() {
		return false;
	}

	@Override
	public boolean isConstant() {
		return true;
	}

	@Override
	public boolean isLeaf() {
		return true;
	}

	@Override
	public IntervalExpressionNode wrap() {
		return null;
	}

	@Override
	public boolean isNode() {
		return false;
	}

	@Override
	public IntervalExpressionValue unwrap() {
		return this;
	}

	@Override
	public Interval value() {
		return interval;
	}
}
