package org.geogebra.common.kernel.interval.evaluators;

import org.geogebra.common.kernel.interval.Interval;

/**
 * Can be extended by intervals.
 */
public interface ExtendSpace {
	/**
	 * @param x interval
	 */
	void extend(Interval x);
}
