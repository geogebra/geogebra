package geogebra.web;


import java.util.ArrayList;
import java.util.Date;

import org.apache.tools.ant.taskdefs.Java;

import geogebra.common.GeoGebraConstants;
import geogebra.common.kernel.commands.AlgebraProcessor;
import geogebra.web.css.GuiResources;
import geogebra.web.helper.JavaScriptInjector;
import geogebra.web.html5.ArticleElement;
import geogebra.web.html5.Dom;
import geogebra.web.main.Application;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;



/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Web implements EntryPoint {
	
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

	public void onModuleLoad() {
		//for debug
		// DebugPrinterWeb.DEBUG_IN_PRODUCTION = true;
		
		//insert zip.js
		JavaScriptInjector.inject(GuiResources.INSTANCE.zipJs().getText());
		Web.webWorkerSupported = chekcWorkerSupport(GWT.getModuleBaseURL());
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
		
		
		//we dont want to parse out of the box sometimes...
		if (!calledFromExtension()) {
			startGeoGebra(getGeoGebraMobileTags());
		} else {
			exportArticleTagRenderer();
		}
	}
	
	private native void exportArticleTagRenderer() /*-{
	    $wnd.GGW_ext.render = $entry(@geogebra.web.gui.app.GeoGebraFrame::renderArticleElemnt(Lgeogebra/web/html5/ArticleElement;));
    }-*/;
    
	private native boolean calledFromExtension() /*-{
	    return (typeof $wnd.GGW_ext !== "undefined");
    }-*/;
	
	public static boolean webWorkerSupported = false; 
	
	private void startGeoGebra(ArrayList<ArticleElement> geoGebraMobileTags) {
	 	
		geogebra.web.gui.app.GeoGebraFrame.main(geoGebraMobileTags);
	    
    }
	
	private static native boolean checkIfFallbackSetExplicitlyInArrayBufferJs() /*-{
		if ($wnd.zip.useWebWorkers === false) {
			//we set this explicitly in arraybuffer.js
			$wnd.console.log("workers maybe supported, but fallback set explicitly in arraybuffer.js");
			return true;;
		}
		return false;
	}-*/;
	
	private static native boolean chekcWorkerSupport(String workerpath) /*-{
	    try {
	    	var worker = new $wnd.Worker(workerpath+"js/workercheck.js");
	    } catch (e) {
	    	$wnd.console.log("worker not supported, fallback for simple js");
	    	return false;
	    }
	    $wnd.console.log("workers are supported");
	    worker.terminate();
	    return true;
    }-*/;
}
