package geogebra.web;


import geogebra.common.GeoGebraConstants;
import geogebra.common.util.debug.GeoGebraProfiler;
import geogebra.common.util.debug.SilentProfiler;
import geogebra.html5.Browser;
import geogebra.html5.util.ArticleElement;
import geogebra.web.WebStatic.GuiToLoad;
import geogebra.web.html5.Dom;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class WebSimple implements EntryPoint {

	private static ArrayList<ArticleElement> getGeoGebraMobileTags() {
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
		exportGGBElementRenderer();

		
		//WebStatic.currentGUI = checkIfNeedToLoadGUI();
//		setLocaleToQueryParam();
				
		//if (WebStatic.currentGUI.equals(GuiToLoad.VIEWER)) {
			//we dont want to parse out of the box sometimes...
		//	loadAppletAsync();
		//}

		// instead, load it always as simple here
		WebStatic.currentGUI = GuiToLoad.VIEWER;

		//loadAppletAsync();
		// instead, load it immediately
		startGeoGebra(getGeoGebraMobileTags());
	}

	public static void loadAppletAsync() {
	    GWT.runAsync(new RunAsyncCallback() {
			
			public void onSuccess() {
				startGeoGebra(getGeoGebraMobileTags());
			}
			
			public void onFailure(Throwable reason) {
				// TODO Auto-generated method stub
				
			}
		});
    }

	static void startGeoGebra(ArrayList<ArticleElement> geoGebraMobileTags) {
	 	
		geogebra.web.gui.applet.GeoGebraFrameSimple.main(geoGebraMobileTags);
	    
    }
	
	private native void exportGGBElementRenderer() /*-{
 		$wnd.renderGGBElement = $entry(@geogebra.web.gui.applet.GeoGebraFrameSimple::renderArticleElement(Lcom/google/gwt/dom/client/Element;));
	}-*/;

}
