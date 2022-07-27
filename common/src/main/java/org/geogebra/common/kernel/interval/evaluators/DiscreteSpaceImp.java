package org.geogebra.common.kernel.interval.evaluators;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.debug.Log;

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

	@Override
	public void extend(DiscreteSpace subspace) {
		List<Interval> list = subspace.values().collect(Collectors.toList());
		double valueToExtend = list.get(list.size() - 1 ).getHigh();

		if (DoubleUtil.isEqual(valueToExtend, interval.getLow())) {
			double low = list.get(0).getLow();
			interval.set(low, interval.getHigh() - list.size() * step);
		} else {
			double extendHigh = list.get(0).getLow();
			if (DoubleUtil.isEqual(extendHigh, interval.getHigh())) {
				interval.set(interval.getLow() + list.size() * step,
						valueToExtend);
			}
		}
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

		return DoubleStream.iterate(interval.getLow(), d -> myRound(d + step))
				.limit(count)
				.mapToObj(value -> new Interval(value, myRound(value + step)));
	}

	private double myRound(double value) {
		try {
			BigDecimal bigDecimal = BigDecimal.valueOf(value);
			return bigDecimal.setScale(12, BigDecimal.ROUND_CEILING).doubleValue();
		} catch (Exception e) {
			Log.debug("Bad value: " + value);
		}
		return 0;
	}

	@Override
	public double getStep() {
		return step;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof DiscreteSpaceImp)) return false;
		DiscreteSpaceImp that = (DiscreteSpaceImp) o;
		return count == that.count && Double.compare(that.step, step) == 0
				&& Objects.equals(interval, that.interval);
	}

	@Override
	public int hashCode() {
		return Objects.hash(interval, count, step);
	}

	@Override
	public String toString() {
		return "DiscreteSpaceImp{" +
				"interval=" + interval +
				", count=" + count +
				", step=" + step +
				'}';
	}
}
