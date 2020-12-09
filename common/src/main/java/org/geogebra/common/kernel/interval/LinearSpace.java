package org.geogebra.common.kernel.interval;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for linear space.
 *
 * @author laszlo
 */
public class LinearSpace {
	public List<Double> values;
	private double scale;
	private double step;

	public LinearSpace() {
		values = new ArrayList<>();
	}

	/**
	 * Constructor
	 *
	 * @param start value
	 * @param end value
	 * @param count the refinement of the linear space.
	 */
	public LinearSpace(int start, int end, int count) {
		this();
		update(new Interval(start, end), count);
	}

	/**
	 * Updates the space.
	 * @param interval the base interval.
	 * @param count of the interval to divide.
	 */
	public void update(Interval interval, int count) {
		values.clear();
		step = interval.getLength() / count;
		fill(interval.getLow(), interval.getHigh(), step);
		scale = size() > 2 ? values.get(1) - values.get(0) : 0;
	}

	private void fill(double start, double end, double step) {
		double current = start;
		while (current < end + step) {
			values.add(current);
			current += step;
		}
	}

	/**
	 *
	 * @return the value list of the space.
	 */
	public List<Double> values() {
		return values;
	}

	/**
	 *
	 * @return the difference between two neighbour values.
	 */
	public double getScale() {
		return scale;
	}

	/**
	 * Extend space to the given maximum.
	 *
	 * @param max to adjust to.
	 * @return the new {@link LinearSpace} that contains the new values only.
	 */
	public LinearSpace extendMax(double max) {
		LinearSpace result = new LinearSpace();
		double t = getLastValue();
		result.values.add(getLastValue());
		while (t < max) {
			t += step;
			values.add(t);
			result.values.add(t);
		}
		return result;
	}

	private double getLastValue() {
		return values.get(size() - 1);
	}

	private int size() {
		return values.size();
	}

	/**
	 * Adjust space to the given minimum.
	 *
	 * @param min to adjust to.
	 * @return the new {@link LinearSpace} that contains the new values only.
	 */
	public LinearSpace extendMin(double min) {
		LinearSpace result = new LinearSpace();
		result.values.add(getFirstValue());
		double t = getFirstValue();
		while (min < t) {
			t -= step;
			result.values.add(0, t);
			values.add(0, t);
		}
		return result;
	}

	private double getFirstValue() {
		return values.isEmpty() ? 0 : values.get(0);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof LinearSpace) {
			LinearSpace other = ((LinearSpace) obj);
			return values.equals(other.values);
		}
		return false;
	}

	@Override
	public String toString() {
		return values.toString();
	}

	@Override
	public int hashCode() {
		return values.hashCode();
	}

	/**
	 * Reduces linear space to a new maximum.
	 *
	 * @param max to reduce.
	 *
	 * @return number of values deleted.
	 */
	public int shrinkMax(double max) {
		double t = getLastValue();
		int count = 0;
		while (t > max) {
			values.remove(size() - 1);
			t = getLastValue();
			count++;
		}
		return count;
	}

	/**
	 * Reduces linear space to a new minimum.
	 *
	 * @param min to reduce.
	 *
	 * @return number of values deleted.
	 */
	public int shrinkMin(double min) {
		int count = 0;
		double t = getFirstValue();
		while (t < min) {
			values.remove(0);
			t = getFirstValue();
			count++;
		}
		return count;
	}
}