package geogebra.web;


import geogebra.common.GeoGebraConstants;
import geogebra.common.kernel.commands.AlgebraProcessor;
import geogebra.common.main.App;
import geogebra.common.util.debug.GeoGebraProfiler;
import geogebra.common.util.debug.SilentProfiler;
import geogebra.html5.js.ResourcesInjector;
import geogebra.html5.util.ArticleElement;
import geogebra.web.asyncservices.HandleGoogleDriveService;
import geogebra.web.asyncservices.HandleGoogleDriveServiceAsync;
import geogebra.web.asyncservices.HandleOAuth2Service;
import geogebra.web.asyncservices.HandleOAuth2ServiceAsync;
import geogebra.web.gui.app.GeoGebraAppFrame;
import geogebra.web.html5.Dom;

import java.util.ArrayList;
import java.util.Date;

import com.google.api.gwt.oauth2.client.Auth;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.ui.RootPanel;



/**
 * @author apa
 *
 */
/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Web implements EntryPoint {
	
	
	

	/**
	 * Google Authorization class entry point.
	 */
	public static final Auth AUTH = Auth.get();
	/**
	 * Global Async for communication with the server
	 */
	public final static HandleGoogleDriveServiceAsync gdAsync = GWT.create(HandleGoogleDriveService.class);
	public final static HandleOAuth2ServiceAsync oaAsync = GWT.create(HandleOAuth2Service.class);
	
	public void t(String s,AlgebraProcessor ap) throws Exception{
		ap.processAlgebraCommandNoExceptionHandling(s, false,false, true);
	}
	
	private ArrayList<ArticleElement> getGeoGebraMobileTags() {
		NodeList<Element> nodes = Dom.getElementsByClassName(GeoGebraConstants.GGM_CLASS_NAME);
		ArrayList<ArticleElement> articleNodes = new ArrayList<ArticleElement>();
		for (int i = 0; i < nodes.getLength(); i++) {
			Date creationDate = new Date();
			nodes.getItem(i).setId(GeoGebraConstants.GGM_CLASS_NAME+i+creationDate.getTime());
			articleNodes.add(ArticleElement.as(nodes.getItem(i)));
		}
		return articleNodes;
	}
	
	/**
	 * @author gabor
	 * Describes the Gui type that needed to load
	 *
	 */
	
	public enum GuiToLoad {
		/**
		 * Gui For an App.
		 */
		APP, 
		/**
		 * Gui for a mobile
		 */
		MOBILE,
		/**
		 * No Gui, only euclidianView
		 */
		VIEWER
	}
	
	/**
	 * GUI currently Loaded
	 */
	public static GuiToLoad currentGUI = null;
	/**
	 * set true if Google Api Js loaded
	 */

	public void onModuleLoad() {
		//use GeoGebraProfilerW if you want to profile, SilentProfiler  for production
		//GeoGebraProfiler.init(new GeoGebraProfilerW());
		GeoGebraProfiler.init(new SilentProfiler());
		
		GeoGebraProfiler.getInstance().profile();

		
		Web.currentGUI = checkIfNeedToLoadGUI();
		
		
		
//		setLocaleToQueryParam();
				
		if (Web.currentGUI.equals(GuiToLoad.VIEWER)) {
			//we dont want to parse out of the box sometimes...
			if (!calledFromExtension()) {
				loadAppletAsync();
			} else {
				loadExtensionAsync();
			}
		} else if (Web.currentGUI.equals(GuiToLoad.APP)) {
			loadAppAsync();
		}
	}

	private void loadExtensionAsync() {
		GWT.runAsync(new RunAsyncCallback() {
			
			public void onSuccess() {
				ResourcesInjector.injectResources();
				 exportArticleTagRenderer();
				    //export other methods if needed
				    //call the registered methods if any
				    GGW_ext_webReady();
			}
			
			public void onFailure(Throwable reason) {
				// TODO Auto-generated method stub
				
			}
		});
	   
    }

	private void loadAppletAsync() {
	    GWT.runAsync(new RunAsyncCallback() {
			
			public void onSuccess() {
				startGeoGebra(getGeoGebraMobileTags());
			}
			
			public void onFailure(Throwable reason) {
				// TODO Auto-generated method stub
				
			}
		});
    }

	private void loadAppAsync() {
	    GWT.runAsync(new RunAsyncCallback() {
			
			public void onSuccess() {
				ResourcesInjector.injectResources();
				GeoGebraAppFrame app = new GeoGebraAppFrame();
			}

			public void onFailure(Throwable reason) {
				App.debug(reason);
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
	
	native void exportArticleTagRenderer() /*-{
	    $wnd.GGW_ext.render = $entry(@geogebra.web.gui.applet.GeoGebraFrame::renderArticleElemnt(Lcom/google/gwt/dom/client/Element;));
    }-*/;
    
	private native boolean calledFromExtension() /*-{
	    return (typeof $wnd.GGW_ext !== "undefined");
    }-*/;
	
	
	/*
	 * This method should never be called. Only copyed to external javascript files,
	 * if we like to use GeoGebraWeb as an library, and call its methods depending on
	 * it is loaded or not.
	 */
	private native void copyThisJsIfYouLikeToUseGeoGebraWebAsExtension() /*-{
		//GGW_ext namespace must be a property of the global scope
		window.GGW_ext = {
			startupFunctions : []
		};
		
		//register methods that will be called if web is loaded,
		//or if it is loaded, will be called immediately
		//GGW_ext.webReady("render",articleelement);
		GGW_ext.webReady = function(functionName, args) {
			if (typeof GGW_ext[functionName] === "function") {
				//web loaded
				this[functionName].apply(args);
			} else {
				this.startupFunctions.push([functionName,args]);
			}	
		}
	}-*/;
	
	private native void GGW_ext_webReady() /*-{
		var functions = null,
			i,l;
		if (typeof $wnd.GGW_ext === "object") {
			if ($wnd.GGW_ext.startupFunctions && $wnd.GGW_ext.startupFunctions.length) {
				functions = $wnd.GGW_ext.startupFunctions;
				for (i = 0, l = functions.length; i < l; i++) {
					if (typeof $wnd.GGW_ext[functions[i][0]] === "function") {
						$wnd.GGW_ext[functions[i][0]](functions[i][1]);
					}
				}
			} 
		}
	}-*/;
	
	
	void startGeoGebra(ArrayList<ArticleElement> geoGebraMobileTags) {
	 	
		geogebra.web.gui.applet.GeoGebraFrame.main(geoGebraMobileTags);
	    
    }

}
