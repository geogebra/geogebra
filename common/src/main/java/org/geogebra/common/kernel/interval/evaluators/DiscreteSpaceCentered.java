package org.geogebra.common.kernel.interval.evaluators;

import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import org.geogebra.common.kernel.interval.Interval;

public class DiscreteSpaceCentered implements DiscreteSpace {
	private double start;
	private int countLeft;
	private int countRight;
	private double step;

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
		if (low < start) {
			double mostLeftValue = start - countLeft * step;
			double length = mostLeftValue - low;
			int diffCount = (int) Math.floor(length / step);
			countLeft += diffCount;
			return createStream(mostLeftValue - diffCount * step, 0, diffCount);
		}
		double mostRightValue = start + countRight * step;
		double length = high - mostRightValue;
		int diffCount = (int) Math.floor(length / step);
		countRight += diffCount;
		return createStream(mostRightValue, 0, diffCount);

	}

	private Stream<Interval> createStream(double start, int fromIndex, int toIndex) {
		if (fromIndex >= toIndex) {
			return Stream.empty();
		}
		return DoubleStream.iterate(fromIndex, index -> index + 1)
				.limit(toIndex)
				.mapToObj(index -> new Interval(start + index * step,
						start + (index + 1) * step));
	}


	@Override
	public void update(double min, double max, double step) {
		start = min + (max - min) / 2;
		this.step = step;
		countLeft = computeCountTo(min);
		countRight = computeCountTo(max);
	}
}
