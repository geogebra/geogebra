package org.geogebra.web.html5.euclidian.profiler;

import org.geogebra.common.util.profiler.FpsProfiler;

import elemental2.dom.DomGlobal;

/**
 * Measures the frames painted per second (fps).
 */
public class FpsProfilerW extends FpsProfiler {

	@Override
	protected long now() {
		return (long) getMillisecondTimeNative();
	}

	/**
	 * @return current milliseconds
	 */
	public static double getMillisecondTimeNative() {
		return DomGlobal.performance.now();
	}
}
