package org.geogebra.common.kernel.interval.node;

import org.geogebra.common.kernel.interval.Interval;

public class IntervalFunctionVariable implements IntervalExpressionValue {
	private Interval interval = new Interval();
	@Override
	public boolean isVariable() {
		return true;
	}

	@Override
	public boolean isConstant() {
		return false;
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
	public void set(Interval other) {
		interval.set(other);
	}

	@Override
	public void set(double value) {
		interval.set(value);
	}

	@Override
	public Interval value() {
		return interval;
	}

	@Override
	public boolean isNode() {
		return false;
	}

	@Override
	public IntervalExpressionValue unwrap() {
		return this;
	}
}
