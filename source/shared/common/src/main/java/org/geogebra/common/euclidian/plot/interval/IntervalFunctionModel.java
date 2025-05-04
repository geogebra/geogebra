package org.geogebra.common.euclidian.plot.interval;

/**
 * Interval function model.
 */
public interface IntervalFunctionModel {

	/**
	 * Updates what's necessary.
	 */
	void update();

	/**
	 * Updates the entire model.
	 */
	void resample();

	/**
	 *
	 * update function domain to plot due to the visible x range.
	 */
	void updateDomain();

	/**
	 * Clears the entire model.
	 */
	void clear();

	/**
	 * Mark this to be resampled on next update.
	 */
	void needsResampling();
}
