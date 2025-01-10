package org.geogebra.common.euclidian.plot.interval;

public interface IntervalFunctionModel {
	void update();

	void resample();

	void updateDomain();

	void clear();

	void needsResampling();
}
