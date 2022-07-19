package org.geogebra.common.kernel.interval.evaluators;

import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import org.geogebra.common.kernel.interval.Interval;

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
		count = step != 0 ? (int) Math.max(1, Math.ceil(interval.getLength() / step)) : 0;
	}

	public DiscreteSpaceImp() {
		// nothing to do
	}

	public DiscreteSpaceImp(Interval interval, int count) {
		update(interval, count);
	}

	@Override
	public void update(Interval interval, int count) {
		if (count == 0) {
			return;
		}

		this.interval = interval;
		this.count = count;
		step = interval.getLength() / count;
	}

	@Override
	public DiscreteSpace difference(double low, double high) {
		if (low < interval.getLow()) {
			return diffMin(low);
		}
		return diffMax(high);
	}

	private DiscreteSpace diffMin(double min) {
		double d = Math.ceil(Math.abs(interval.getLow() - min) / step);
		double start = interval.getLow() - d * step;
		return new DiscreteSpaceImp(start, interval.getLow(), step);
	}

	private DiscreteSpace diffMax(double max) {
		return new DiscreteSpaceImp(interval.getHigh(), max, step);
	}

	@Override
	public void setInterval(double min, double max) {
		if (count == 0) {
			return;
		}
		interval.set(min, max);
		step = interval.getLength() / count;
	}

	@Override
	public Stream<Interval> values() {
		if (interval == null) {
			return Stream.empty();
		}

		return DoubleStream.iterate(interval.getLow(), d -> d + step)
				.limit(count)
				.mapToObj(value -> new Interval(value, value + step));
	}

	@Override
	public double getStep() {
		return step;
	}
}
