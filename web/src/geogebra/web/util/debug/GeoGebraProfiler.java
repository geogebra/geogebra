package geogebra.web.util.debug;

import geogebra.common.main.App;

public class GeoGebraProfiler extends geogebra.common.util.debug.GeoGebraProfiler {

	
	private static GeoGebraProfiler instance = null; 
	
	@Override
    public native void profile(String label) /*-{
    	if (@geogebra.web.util.debug.GeoGebraProfiler::isConsoleSupported()() && $wnd.console.profile) {
    		$wnd.console.profile(label);
    	} else {
    		@geogebra.web.util.debug.GeoGebraProfiler::showError()();
    	}
    }-*/;

	@Override
    public native void profileEnd() /*-{
    	if (@geogebra.web.util.debug.GeoGebraProfiler::isConsoleSupported()() && $wnd.console.profileEnd) {
    		$wnd.console.profileEnd();
    	} else {
    		@geogebra.web.util.debug.GeoGebraProfiler::showError()();
    	}  		
    }-*/;

	@Override
    public native void time(String label) /*-{
    	if (@geogebra.web.util.debug.GeoGebraProfiler::isConsoleSupported()() && $wnd.console.time) {
    		$wnd.console.time(label);
    	} else {
    		@geogebra.web.util.debug.GeoGebraProfiler::showError()();
    	}	    
    }-*/;

	@Override
    public native void timeEnd() /*-{
	    if (@geogebra.web.util.debug.GeoGebraProfiler::isConsoleSupported()() && $wnd.console.timeEnd) {
    		$wnd.console.timeEnd();
    	} else {
    		@geogebra.web.util.debug.GeoGebraProfiler::showError()();
    	}	 
    }-*/;
    
    private static void showError() {
    	App.debug("console methods for profiling not supported");
    }
    
    private static native boolean isConsoleSupported() /*-{
    	return (typeof $wnd.console === 'object'); 
    }-*/;
    
    public GeoGebraProfiler getInstance() {
    	if (instance == null) {
    		instance = new GeoGebraProfiler();
    	}
    	return instance;
    }

}
