package org.geogebra.common.euclidian.plot.interval;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalConstants;

/**
 *  Optional settings for debug purposes
 *
 */

public class IntervalPlotSettings {

	/**
	 *
	 * @return if model shoud check its x range and re-evaluate them.
	 * Set it to false to debug line joins.
	 */
	static boolean isUpdateEnabled() {
		return true;
	}

	/**
	 * Limit the data to the given x range to plot.
	 * Useful to debug glitches.
	 */
	static Interval visisbleXRange() {
		return IntervalConstants.undefined();
	}
}
