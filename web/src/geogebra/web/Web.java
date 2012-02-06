package geogebra.web;


import java.util.ArrayList;
import java.util.HashMap;

import geogebra.common.GeoGebraConstants;
import geogebra.common.kernel.commands.AlgebraProcessor;
import geogebra.common.main.AbstractApplication;
import geogebra.web.gui.SplashDialog;
import geogebra.web.helper.ImageLoadCallback;
import geogebra.web.helper.ImageWrapper;
import geogebra.web.html5.ArticleElement;
import geogebra.web.html5.Dom;
import geogebra.web.main.Application;
import geogebra.web.util.DebugPrinterWeb;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;



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
			nodes.getItem(i).setId(GeoGebraConstants.GGM_CLASS_NAME+i);
			articleNodes.add(ArticleElement.as(nodes.getItem(i)));
		}
		return articleNodes;
	}
	
	public static SplashDialog splash = new SplashDialog();

	public void onModuleLoad() {
		//for debug
		//DebugPrinterWeb.DEBUG_IN_PRODUCTION = true;
		//show splash
		splash.center();
		splash.show();
		startGeoGebra(getGeoGebraMobileTags());
	}
	
	private void startGeoGebra(ArrayList<ArticleElement> geoGebraMobileTags) {
	 	
		geogebra.web.gui.app.GeoGebraFrame.main(geoGebraMobileTags);
	    
    }
}
