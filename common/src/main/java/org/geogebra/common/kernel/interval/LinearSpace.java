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

	public LinearSpace() {
		values = new ArrayList<>();
	}

	/**
	 * Updates the space.
	 * @param interval the base interval.
	 * @param count of the interval to divide.
	 */
	public void update(Interval interval, int count) {
		values.clear();
		fill(interval.getLow(), interval.getHigh(), interval.getWidth() / count);
		scale = values.size() > 2 ? values.get(1) - values.get(0) : 0;
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
}