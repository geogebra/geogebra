package geogebra.web;

import geogebra.common.util.debug.GeoGebraProfiler;
import geogebra.common.util.debug.Log;
import geogebra.common.util.debug.SilentProfiler;
import geogebra.html5.Browser;
import geogebra.html5.js.ResourcesInjector;
import geogebra.html5.util.MyRunAsyncCallback;
import geogebra.html5.util.RunAsync;
import geogebra.web.WebStatic.GuiToLoad;
import geogebra.web.gui.app.GeoGebraAppFrame;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class WebApplication implements EntryPoint {

	/**
	 * set true if Google Api Js loaded
	 */

	public void onModuleLoad() {
		if(RootPanel.getBodyElement().getAttribute("data-param-laf")!=null
				&& !"".equals(RootPanel.getBodyElement().getAttribute("data-param-laf"))){
			//loading touch, ignore.
			return;
		}
		Browser.checkFloat64();
		//use GeoGebraProfilerW if you want to profile, SilentProfiler  for production
		//GeoGebraProfiler.init(new GeoGebraProfilerW());
		GeoGebraProfiler.init(new SilentProfiler());
		
		GeoGebraProfiler.getInstance().profile();

		//WebStatic.currentGUI = checkIfNeedToLoadGUI();
		//if (WebStatic.currentGUI.equals(GuiToLoad.APP)) {
		//	loadAppAsync();
		//}

		// instead of that, load the application version always
		WebStatic.currentGUI = GuiToLoad.APP;
		loadAppAsync();
	}

	private void loadAppAsync() {
	    RunAsync.INSTANCE.runAsyncCallback(new MyRunAsyncCallback() {
			
			public void onSuccess() {
				ResourcesInjector.injectResources();
				GeoGebraAppFrame app = new GeoGebraAppFrame();
			}

			public void onFailure(Throwable reason) {
				Log.debug(reason);
			}
		});
	    
    }
	
	
	/*
	 * Checks, if the <body data-param-app="true" exists in html document
	 * if yes, GeoGebraWeb will be loaded as a full app.
	 * 
	 * @return true if bodyelement has data-param-app=true
	 */
	private static GuiToLoad checkIfNeedToLoadGUI() {
	    if ("true".equals(RootPanel.getBodyElement().getAttribute("data-param-app"))) {
	    	return GuiToLoad.APP;
	    } else if ("true".equals(RootPanel.getBodyElement().getAttribute("data-param-mobile"))) {
	    	return GuiToLoad.MOBILE;
	    }
	    return GuiToLoad.VIEWER;
    }
}
