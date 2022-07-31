package org.geogebra.common.kernel.interval.evaluators;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.geogebra.common.kernel.interval.Interval;

public class DiscreteSpaceCentered implements DiscreteSpace {
	private double start;
	private double countLeft;
	private double countRight;
	private double step;

	public DiscreteSpaceCentered(double start, double countLeft, double countRight, double step) {
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
		countLeft = Math.ceil((start - interval.getLow()) / step);
		countRight = Math.ceil((interval.getHigh() - start) / step);
	}

	@Override
	public Stream<Interval> values() {
		List<Interval> list = new ArrayList<>();
		for (double i = -countLeft; i < countRight; i++) {
			double low = start + i * step;
			list.add(new Interval(low, low + step));
		}
		return list.stream();
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
