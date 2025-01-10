package org.geogebra.common.kernel.interval.evaluators;

import java.util.function.Consumer;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import org.geogebra.common.kernel.interval.Interval;

public class DiscreteSpaceCentered implements DiscreteSpace {
	private double center;
	private int countLeft;
	private int countRight;
	private double step;

	/**
	 *
	 * @param center of the space.
	 * @param countLeft number of steps from center to the most left value.
	 * @param countRight number of steps from center to the most right value.
	 * @param step of the values in space.
	 */
	public DiscreteSpaceCentered(double center, int countLeft, int countRight, double step) {
		this.center = center;
		this.countLeft = countLeft;
		this.countRight = countRight;
		this.step = step;
	}

	/**
	 *
	 * @param step of the values in space.
	 */
	public DiscreteSpaceCentered(double step) {
		this.step = step;
	}

	@Override
	public void rescale(Interval interval, int numberOfSteps) {
		center = interval.middle();
		this.step = interval.getLength() / numberOfSteps;
		countLeft = computeCountTo(interval.getLow());
		countRight = computeCountTo(interval.getHigh());
	}

	private int computeCountTo(double limit) {
		return (int) Math.ceil(Math.abs(center - limit) / step);
	}

	private double getMostRightValue() {
		return center + countRight * step;
	}

	private double getMostLeftValue() {
		return center - countLeft * step;
	}

	@Override
	public void forEach(Consumer<Interval> action) {
		values().forEach(action);
	}

	@Override
	public void extendLeft(Interval domain, ExtendSpace cb) {
		double evaluateTo = domain.getLow();
		if (evaluateTo > getMostLeftValue() - step) {
			return;
		}
		Interval x = head();
		while (x.getLow() > evaluateTo) {
			moveLeft();
			x = head();
			cb.extend(x);
		}
	}

	private void moveLeft() {
		countLeft++;
		countRight--;
	}

	private Interval head() {
		double value = getMostLeftValue();
		return new Interval(value, value + step);
	}

	@Override
	public void extend(Interval domain, ExtendSpace cbLeft, ExtendSpace cbRight) {
		double evaluateTo = domain.getLow();
		Interval x = head();
		while (x.getLow() > evaluateTo) {
			countLeft++;
			x = head();
			cbLeft.extend(x);
		}

		evaluateTo = domain.getHigh();
		x = tail();
		while (x.getHigh() < evaluateTo) {
			countRight++;
			x = tail();
			cbRight.extend(x);
		}

	}

	@Override
	public void extendRight(Interval domain, ExtendSpace extendSpace) {
		double evaluateTo = domain.getHigh();
		if (evaluateTo < getMostRightValue() + step) {
			return;
		}

		Interval x = tail();
		while (x.getHigh() < evaluateTo) {
			moveRight();
			x = tail();
			extendSpace.extend(x);
		}
	}

	private void moveRight() {
		countLeft--;
		countRight++;
	}

	private Interval tail() {
		double value = getMostRightValue();
		return new Interval(value - step, value);
	}

	private Stream<Interval> values() {
		return createStream(center, -countLeft, countLeft + countRight);
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
		return "DiscreteSpaceCentered{"
				+ "start=" + center
				+ ", countLeft=" + countLeft
				+ ", countRight=" + countRight
				+ ", step=" + step
				+ '}';
	}
}
