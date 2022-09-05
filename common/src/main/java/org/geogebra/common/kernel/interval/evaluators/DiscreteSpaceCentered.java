package org.geogebra.common.kernel.interval.evaluators;

import java.util.function.Consumer;
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
	public void update(Interval interval, int step) {
		start = interval.middle();
		this.step = interval.getLength() / step;
		countLeft = computeCountTo(interval.getLow());
		countRight = computeCountTo(interval.getHigh());
	}

	private int computeCountTo(double limit) {
		return (int) Math.ceil(Math.abs(start - limit) / step);
	}

	private double getMostRightValue() {
		return start + countRight * step;
	}

	private double getMostLeftValue() {
		return start - countLeft * step;
	}

	@Override
	public Interval getDomain() {
		return new Interval(getMostLeftValue(), getMostRightValue());
	}

	@Override
	public void forEach(Consumer<Interval> action) {
		values().forEach(action);
	}

	@Override
	public void expandLeft(int count) {
		countLeft += count;
	}

	@Override
	public void expandRight(int count) {
		countRight += count;
	}

	private Stream<Interval> values() {
		return createStream(start, -countLeft, countLeft + countRight);
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
	public String toString() {
		return "DiscreteSpaceCentered{" +
				"start=" + start +
				", countLeft=" + countLeft +
				", countRight=" + countRight +
				", step=" + step +
				'}';
	}
}
