package org.geogebra.common.euclidian.plot.interval;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalConstants;

/**
 *  Optional settings for debug purposes
 *
 */

public final class IntervalPlotSettings {

	/**
	 *
	 * @return if model should check its x range and re-evaluate them.
	 * Set it false to debug line joins.
	 */
	static boolean isUpdateOnMoveEnabled() {
		return true;
	}

	/**
	 *
	 * @return if model should check its x range and re-evaluate them.
	 * Set it false to debug line joins.
	 */
	static boolean isUpdateOnMoveStopEnabled() {
		return true;
	}

	/**
	 *
	 * @return if model should check its x range and re-evaluate them.
	 * Set it false to debug line joins.
	 */
	static boolean isUpdateOnZoomStopEnabled() {
		return true;
	}

	/**
	 * Limit the data to the given x range to plot.
	 * Useful to debug glitches.
	 */
	static Interval visibleXRange() {
		return IntervalConstants.undefined();
	}

	private IntervalPlotSettings() {
		throw new IllegalArgumentException("Should be not initialized");
	}

	/**
	 *
	 * @return if data should resampled on view settings (practically: width) change
	 */
	public static boolean isUpdateOnSettingsChangeEnabled() {
		return true;
	}
}
