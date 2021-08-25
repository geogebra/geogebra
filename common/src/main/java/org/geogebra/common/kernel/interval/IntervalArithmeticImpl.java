package org.geogebra.common.kernel.interval;

import com.google.j2objc.annotations.Weak;

public class IntervalArithmeticImpl implements IntervalArithmetic {
	@Weak
	private final Interval interval;
	private final IntervalMultiply intervalMultiply;
	private final IntervalDivide intervalDivide;

	public IntervalArithmeticImpl(Interval interval) {
		this.interval = interval;
		intervalMultiply = new IntervalMultiply(interval);
		intervalDivide = new IntervalDivide(interval);
	}

	@Override
	public Interval divide(Interval other) {
		return intervalDivide.divide(other);
	}

	@Override
	public Interval multiply(Interval other) {
		return intervalMultiply.multiply(other);
	}
}
