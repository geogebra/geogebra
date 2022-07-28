package org.geogebra.common.kernel.interval.evaluators;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.geogebra.common.kernel.interval.Interval;

public class DiscreteSpaceCentered implements DiscreteSpace {
	private final double start;
	private final double countLeft;
	private final double countRight;
	private final double step;

	public DiscreteSpaceCentered(double start, double countLeft, double countRight, double step) {
		this.start = start;
		this.countLeft = countLeft;
		this.countRight = countRight;
		this.step = step;
	}

	@Override
	public void update(Interval interval, int count) {

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
		return 0;
	}
}
