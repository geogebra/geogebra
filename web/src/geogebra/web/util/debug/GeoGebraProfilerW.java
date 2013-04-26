package geogebra.web.util.debug;

import geogebra.common.main.App;
import geogebra.common.util.debug.GeoGebraProfiler;

/**
 * @author gabor
 * 
 * profiler using the Chrome Console API
 *
 */
public class GeoGebraProfilerW extends GeoGebraProfiler {
	
	
	/**
	 * Inits Web's GeoGebraProfiler, and extends the functionality to use them in native js code.
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
			    		t.@geogebra.web.util.debug.GeoGebraProfilerW::profile()();
			    	},
			    	profileEnd : function() {
			    		t.@geogebra.web.util.debug.GeoGebraProfilerW::profileEnd()();
			    	},
			    	time : function(label) {
			    		t.@geogebra.web.util.debug.GeoGebraProfilerW::time(Ljava/lang/String;)(label);
			    	},
			    	timeEnd : function(label) {
			    		t.@geogebra.web.util.debug.GeoGebraProfilerW::timeEnd(Ljava/lang/String;)(label);
			    	}
	    		};
	    	}
	    }
    }-*/;

	@Override
    public native void profile() /*-{
    	if (@geogebra.web.util.debug.GeoGebraProfilerW::isConsoleSupported()() && $wnd.console.profile) {
    		$wnd.console.profile();
    	} else {
    		@geogebra.web.util.debug.GeoGebraProfilerW::showError()();
    	}
    }-*/;

	@Override
    public native void profileEnd() /*-{
    	if (@geogebra.web.util.debug.GeoGebraProfilerW::isConsoleSupported()() && $wnd.console.profileEnd) {
    		$wnd.console.profileEnd();
    	} else {
    		@geogebra.web.util.debug.GeoGebraProfilerW::showError()();
    	}  		
    }-*/;

	@Override
    public native void time(String label) /*-{
    	if (@geogebra.web.util.debug.GeoGebraProfilerW::isConsoleSupported()() && $wnd.console.time) {
    		$wnd.console.time(label);
    	} else {
    		@geogebra.web.util.debug.GeoGebraProfilerW::showError()();
    	}	    
    }-*/;

	@Override
    public native void timeEnd(String label) /*-{
	    if (@geogebra.web.util.debug.GeoGebraProfilerW::isConsoleSupported()() && $wnd.console.timeEnd) {
    		$wnd.console.timeEnd(label);
    	} else {
    		@geogebra.web.util.debug.GeoGebraProfilerW::showError()();
    	}	 
    }-*/;
	
	@Override
    public native void trace() /*-{
		if (@geogebra.web.util.debug.GeoGebraProfilerW::isConsoleSupported()() && $wnd.console.trace) {
			$wnd.console.trace();
		} else {
			@geogebra.web.util.debug.GeoGebraProfilerW::showError()();
		}
	}-*/;
			
		
    
    private static void showError() {
    	App.debug("console methods for profiling not supported");
    }
    
    private static native boolean isConsoleSupported() /*-{
    	return (typeof $wnd.console === 'object'); 
    }-*/;

}
