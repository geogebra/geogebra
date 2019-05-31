package org.geogebra.web.html5.util.debug;

import org.geogebra.common.util.ExternalAccess;
import org.geogebra.common.util.debug.GeoGebraProfiler;
import org.geogebra.common.util.debug.Log;

/**
 * @author gabor
 * 
 *         profiler using the Chrome Console API
 *
 */
public class GeoGebraProfilerW extends GeoGebraProfiler {

	/**
	 * Inits Web's GeoGebraProfiler, and extends the functionality to use them
	 * in native js code.
	 */
	public GeoGebraProfilerW() {
		initNative();
	}

	private native void initNative() /*-{
		var t = this;
		$wnd.GeoGebraProfiler = {
			getInstance : function() {
				return {
					profile : function(label) {
						t.@org.geogebra.web.html5.util.debug.GeoGebraProfilerW::profile()();
					},
					profileEnd : function() {
						t.@org.geogebra.web.html5.util.debug.GeoGebraProfilerW::profileEnd()();
					},
					time : function(label) {
						t.@org.geogebra.web.html5.util.debug.GeoGebraProfilerW::time(Ljava/lang/String;)(label);
					},
					timeEnd : function(label) {
						t.@org.geogebra.web.html5.util.debug.GeoGebraProfilerW::timeEnd(Ljava/lang/String;)(label);
					}
				};
			}
		}
	}-*/;

	@Override
	public native void profile() /*-{
		if ($wnd.console && $wnd.console.profile) {
			$wnd.console.profile();
		} else {
			@org.geogebra.web.html5.util.debug.GeoGebraProfilerW::showError()();
		}
	}-*/;

	@Override
	public native void profileEnd() /*-{
		if ($wnd.console && $wnd.console.profileEnd) {
			$wnd.console.profileEnd();
		} else {
			@org.geogebra.web.html5.util.debug.GeoGebraProfilerW::showError()();
		}
	}-*/;

	@Override
	public native void time(String label) /*-{
		if ($wnd.console && $wnd.console.time) {
			$wnd.console.time(label);
		} else {
			@org.geogebra.web.html5.util.debug.GeoGebraProfilerW::showError()();
		}
	}-*/;

	@Override
	public native void timeEnd(String label) /*-{
		if ($wnd.console && $wnd.console.timeEnd) {
			$wnd.console.timeEnd(label);
		} else {
			@org.geogebra.web.html5.util.debug.GeoGebraProfilerW::showError()();
		}
	}-*/;

	@Override
	public native void trace() /*-{
		if ($wnd.console && $wnd.console.trace) {
			$wnd.console.trace();
		} else {
			@org.geogebra.web.html5.util.debug.GeoGebraProfilerW::showError()();
		}
	}-*/;

	@ExternalAccess
	private static void showError() {
		Log.debug("console methods for profiling not supported");
	}

	/**
	 * @return current milliseconds
	 */
	public static native double getMillisecondTimeNative() /*-{
		if ($wnd.performance) {
			return $wnd.performance.now();
		}

		// for IE9
		return new Date().getTime();
	}-*/;

}
