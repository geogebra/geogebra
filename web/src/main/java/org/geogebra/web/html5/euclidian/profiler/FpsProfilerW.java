package org.geogebra.web.html5.euclidian.profiler;

import org.geogebra.common.util.profiler.FpsProfiler;

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
	public static native double getMillisecondTimeNative() /*-{
    	return $wnd.performance.now();
	}-*/;
}
