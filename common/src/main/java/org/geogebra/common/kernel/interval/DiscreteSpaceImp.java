package org.geogebra.common.kernel.interval;

import java.util.stream.DoubleStream;
import java.util.stream.Stream;

public class DiscreteSpaceImp implements DiscreteSpace {
	private Interval interval;
	private int count;
	private double step;

	/**
	 * Constructor
	 *
	 * @param low bound
	 * @param high bound
	 * @param step to split the bounds.
	 */
	public DiscreteSpaceImp(double low, double high, double step) {
		interval = new Interval(low, high);
		this.step = step;
		count = (int) Math.floor(interval.getLength() / step);
	}

	public DiscreteSpaceImp() {
		// nothing to do
	}

	@Override
	public void update(Interval interval, int count) {
		this.interval = interval;
		this.count = count;
		step = interval.getLength() / count;
	}

	@Override
	public void setInterval(double min, double max) {
		interval.set(min, max);
		step = interval.getLength() / count;
	}

	@Override
	public DiscreteSpace diffMax(double max) {
		return new DiscreteSpaceImp(interval.getHigh() + step, max, step);
	}

	@Override
	public DiscreteSpace diffMin(double min) {
		return new DiscreteSpaceImp(min, interval.getLow() - step, step);
	}

	@Override
	public Stream<Interval> values() {
		return DoubleStream.iterate(interval.getLow(), d -> d + step)
				.limit(count)
				.mapToObj(value -> new Interval(value, value + step));
	}

	@Override
	public double getStep() {
		return step;
	}
}
