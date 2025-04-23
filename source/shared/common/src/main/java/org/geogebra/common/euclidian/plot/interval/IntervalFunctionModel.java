package org.geogebra.common.euclidian.plot.interval;

/**
 * Interval function model.
 */
public interface IntervalFunctionModel {
	void update();

	void resample();

	void updateDomain();

	void clear();

	void needsResampling();
}
