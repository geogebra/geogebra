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

	private double minValue() {
		return start - countLeft * step;
	}

	private Stream<Interval> createStream(double start, int fromIndex, int toIndex) {
		return DoubleStream.iterate(fromIndex, index -> index + 1)
				.limit(toIndex)
				.mapToObj(index -> new Interval(start + index * step,
						start + (index + 1) * step));
	}

	@Override
	public DiscreteSpace difference(double low, double high) {
		if (low < minValue()) {
			double length = Math.abs(minValue() - low);
			int diffCount = (int) Math.floor(length / step);
			DiscreteSpaceCentered diffSpace = new DiscreteSpaceCentered(low,
					diffCount, 0, step);
			countLeft += diffCount;
			countRight -= diffCount;
			return diffSpace;
		}
		return null;
	}

	@Override
	public void extend(DiscreteSpace subspace) {

	}

	@Override
	public void setInterval(double min, double max) {

	}

	@Override
	public double getStep() {
		return step;
	}
}
