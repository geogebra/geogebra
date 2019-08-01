package org.geogebra.web.html5.euclidian.profiler;

import org.geogebra.common.util.profiler.FpsProfiler;
import org.geogebra.web.html5.util.debug.GeoGebraProfilerW;

/**
 * Measures the frames painted per second (fps).
 */
public class FpsProfilerW extends FpsProfiler {

    @Override
    protected long now() {
        return (long) GeoGebraProfilerW.getMillisecondTimeNative();
    }
}
