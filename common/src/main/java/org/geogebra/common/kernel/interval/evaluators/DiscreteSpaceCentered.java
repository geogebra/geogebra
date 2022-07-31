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
		int allCount = countLeft + countRight;
		return DoubleStream.iterate(-countLeft, index -> index + 1)
				.limit(allCount)
				.mapToObj(index -> new Interval(index * step, (index + 1) * step));
	}

	@Override
	public DiscreteSpace difference(double low, double high) {
		double oldLow = start - countLeft * step;
		int count = 0;
		if (low < oldLow) {
			double l = low;
			while (l < oldLow) {
				l += step;
				count++;
			}
		}
		return new DiscreteSpaceCentered(oldLow, count, 0, step);
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
