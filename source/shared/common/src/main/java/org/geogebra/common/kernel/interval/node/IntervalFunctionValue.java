package org.geogebra.common.kernel.interval.node;

import org.geogebra.common.kernel.interval.Interval;

/**
 * Function value (an interval) in the tree as leaf.
 */
public class IntervalFunctionValue implements IntervalExpressionValue {
	private final Interval interval;

	/**
	 *
	 * @param interval the value.
	 */
	public IntervalFunctionValue(Interval interval) {
		this.interval = interval;
		interval.setPrecision(0);
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
	public boolean isLeaf() {
		return true;
	}

	@Override
	public IntervalExpressionNode asExpressionNode() {
		return null;
	}

	@Override
	public Interval value() {
		return interval;
	}

	@Override
	public boolean hasFunctionVariable() {
		return false;
	}

	@Override
	public IntervalNode simplify() {
		return this;
	}

	@Override
	public String toString() {
		return interval.toShortString();
	}
}
