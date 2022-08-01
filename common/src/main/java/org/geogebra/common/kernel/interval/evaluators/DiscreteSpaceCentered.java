package org.geogebra.common.kernel.interval.evaluators;

import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import org.geogebra.common.kernel.interval.Interval;

public class DiscreteSpaceCentered implements DiscreteSpace {
	private double start;
	private int countLeft;
	private int countRight;
	private final double step;

	public DiscreteSpaceCentered(double start, int countLeft, int countRight, double step) {
		this.start = start;
		this.countLeft = countLeft;
		this.countRight = countRight;
		this.step = step;
	}

	public DiscreteSpaceCentered(double step) {
		this.step = step;
	}

	@Override
	public void update(Interval interval, int count) {
		start = interval.middle();
		countLeft = computeCountTo(interval.getLow());
		countRight = computeCountTo(interval.getHigh());
	}

	private int computeCountTo(double limit) {
		return (int) Math.ceil(Math.abs(start - limit) / step);
	}

	@Override
	public Stream<Interval> values() {
		return createStream(start, -countLeft, countLeft + countRight);
	}

	@Override
	public Stream<Interval> values(double low, double high) {
		double length = high - low;
		int diffCount = (int) Math.floor(length / step);
		return createStream(low, 0, diffCount);
	}

	private Stream<Interval> createStream(double start, int fromIndex, int toIndex) {
		return DoubleStream.iterate(fromIndex, index -> index + 1)
				.limit(toIndex)
				.mapToObj(index -> new Interval(start + index * step,
						start + (index + 1) * step));
	}


	@Override
	public void update(double min, double max) {
		start = (max - min) / 2;
		countLeft = computeCountTo(min);
		countLeft = computeCountTo(max);
	}
}
