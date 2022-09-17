package org.geogebra.common.kernel.interval.node;

import org.geogebra.common.kernel.interval.Interval;

public interface IntervalExpressionValue extends IntervalNode {

	/**
	 * Sets interval for the value node.
	 *
	 * @param interval to set.
	 */
	void set(Interval interval);

	/**
	 * Sets singleton interval for the value node.
	 *
	 * @param value to set as [value, value].
	 */
	void set(double value);
}