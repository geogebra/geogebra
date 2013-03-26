package geogebra.web;


import geogebra.common.GeoGebraConstants;
import geogebra.common.kernel.commands.AlgebraProcessor;
import geogebra.common.main.App;
import geogebra.web.asyncservices.HandleGoogleDriveService;
import geogebra.web.asyncservices.HandleGoogleDriveServiceAsync;
import geogebra.web.asyncservices.HandleOAuth2Service;
import geogebra.web.asyncservices.HandleOAuth2ServiceAsync;
import geogebra.web.css.GuiResources;
import geogebra.web.gui.app.GeoGebraAppFrame;
import geogebra.web.helper.JavaScriptInjector;
import geogebra.web.html5.ArticleElement;
import geogebra.web.html5.Dom;

import java.util.ArrayList;
import java.util.Date;

import com.google.api.gwt.oauth2.client.Auth;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.StyleInjector;
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
		//do we have an app?
		Web.currentGUI = checkIfNeedToLoadGUI();
		
		// always need English properties available, eg Function.sin
		JavaScriptInjector.inject(GuiResources.INSTANCE.propertiesKeysJS().getText());

		injectResources();
		
//		setLocaleToQueryParam();
				
		if (Web.currentGUI.equals(GuiToLoad.VIEWER)) {
			//we dont want to parse out of the box sometimes...
			if (!calledFromExtension()) {
				startGeoGebra(getGeoGebraMobileTags());
			} else {
				exportArticleTagRenderer();
				//export other methods if needed
				//call the registered methods if any
				GGW_ext_webReady();
			}
		} else if (Web.currentGUI.equals(GuiToLoad.APP)) {
			loadAppAsync();
		}
	}

	public static void injectResources() {
	    // insert mathquill css
		String mathquillcss = GuiResources.INSTANCE.mathquillCss().getText();
		// In GWT 2.4 GWT.getModuleBaseURL was used,
		// but GWT.getModuleBaseForStaticFiles is supported afterwards.
		// String.replace(CharSequence, CharSequence) should replace all occurences in theory
		// mathquillcss.replace("url(web/font/Symbola", "url("+GWT.getModuleBaseURL()+"font/Symbola");
		mathquillcss.replace("url(web/font/Symbola", "url("+GWT.getModuleBaseForStaticFiles()+"font/Symbola");
		// This code has been changed automatically by the build script, but we
		// use GWT 2.5 right now, so that part of the script is no longer needed.

		StyleInjector.inject(mathquillcss);

		//insert zip.js
		JavaScriptInjector.inject(GuiResources.INSTANCE.zipJs().getText());
		JavaScriptInjector.inject(GuiResources.INSTANCE.jQueryJs().getText());
		JavaScriptInjector.inject(GuiResources.INSTANCE.mathquillJs().getText());
		JavaScriptInjector.inject(GuiResources.INSTANCE.giacJs().getText());
		Web.webWorkerSupported = checkWorkerSupport(GWT.getModuleBaseURL());
		if (!webWorkerSupported) {
			JavaScriptInjector.inject(GuiResources.INSTANCE.deflateJs().getText());
			JavaScriptInjector.inject(GuiResources.INSTANCE.inflateJs().getText());
		}
		JavaScriptInjector.inject(GuiResources.INSTANCE.arrayBufferJs().getText());
		//strange, but iPad can blow it away again...
		if (checkIfFallbackSetExplicitlyInArrayBufferJs() && webWorkerSupported) {
			JavaScriptInjector.inject(GuiResources.INSTANCE.deflateJs().getText());
			JavaScriptInjector.inject(GuiResources.INSTANCE.inflateJs().getText());
		}
		JavaScriptInjector.inject(GuiResources.INSTANCE.dataViewJs().getText());
		JavaScriptInjector.inject(GuiResources.INSTANCE.base64Js().getText());
    }

	private void loadAppAsync() {
	    GWT.runAsync(new RunAsyncCallback() {
			
			public void onSuccess() {
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
	
	private native void exportArticleTagRenderer() /*-{
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
	
	public static boolean webWorkerSupported = false; 
	
	private void startGeoGebra(ArrayList<ArticleElement> geoGebraMobileTags) {
	 	
		geogebra.web.gui.applet.GeoGebraFrame.main(geoGebraMobileTags);
	    
    }
	
	
	private static native boolean checkIfFallbackSetExplicitlyInArrayBufferJs() /*-{
		if ($wnd.zip.useWebWorkers === false) {
			//we set this explicitly in arraybuffer.js
			@geogebra.common.main.App::debug(Ljava/lang/String;)("INIT: workers maybe supported, but fallback set explicitly in arraybuffer.js");
			return true;;
		}
		return false;
	}-*/;
	
	
	private static native boolean checkWorkerSupport(String workerpath) /*-{
		// Worker support in Firefox is incompatible at the moment for zip.js,
		// see http://gildas-lormeau.github.com/zip.js/ for details:
		if (navigator.userAgent.toLowerCase().indexOf("firefox") != -1) {
			@geogebra.common.main.App::debug(Ljava/lang/String;)("INIT: worker not supported in Firefox, fallback for simple js");
			return false;
		}
		if (navigator.userAgent.toLowerCase().indexOf("safari") != -1
			&& navigator.userAgent.toLowerCase().indexOf("chrome") == -1) {
			@geogebra.common.main.App::debug(Ljava/lang/String;)("INIT: worker not supported in Safari, fallback for simple js");
			return false;
		}
		
	    try {
	    	var worker = new $wnd.Worker(workerpath+"js/workercheck.js");
	    } catch (e) {
	    	@geogebra.common.main.App::debug(Ljava/lang/String;)("INIT: worker not supported (no worker at " + workerpath + "), fallback for simple js");
	    	
	    	return false;
	    }
	    @geogebra.common.main.App::debug(Ljava/lang/String;)("INIT: workers are supported");
	    	
	    worker.terminate();
	    return true;
    }-*/;
	
}
