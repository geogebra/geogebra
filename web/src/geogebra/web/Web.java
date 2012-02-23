package geogebra.web;


import java.util.ArrayList;

import geogebra.common.GeoGebraConstants;
import geogebra.common.kernel.commands.AlgebraProcessor;
import geogebra.web.html5.ArticleElement;
import geogebra.web.html5.Dom;

import com.google.gwt.core.client.EntryPoint;
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
			nodes.getItem(i).setId(GeoGebraConstants.GGM_CLASS_NAME+i);
			articleNodes.add(ArticleElement.as(nodes.getItem(i)));
		}
		return articleNodes;
	}

	public void onModuleLoad() {
		//for debug
		// DebugPrinterWeb.DEBUG_IN_PRODUCTION = true;
		//show splash
		startGeoGebra(getGeoGebraMobileTags());
	}
	
	private void startGeoGebra(ArrayList<ArticleElement> geoGebraMobileTags) {
	 	
		geogebra.web.gui.app.GeoGebraFrame.main(geoGebraMobileTags);
	    
    }
}
