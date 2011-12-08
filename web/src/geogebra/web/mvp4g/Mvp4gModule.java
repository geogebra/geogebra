package geogebra.web.mvp4g;

import java.util.ArrayList;

import geogebra.common.GeoGebraConstants;
import geogebra.web.eventbus.MyEventBus;
import geogebra.web.html5.ArticleElement;
import geogebra.web.html5.Dom;
import geogebra.web.jso.JsUint8Array;

import com.gargoylesoftware.htmlunit.javascript.host.Node;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author ancsing
 * Just mimic class for Mvp4g
 *
 */
public class Mvp4gModule {
	
	MyEventBus eventBus;
	private HasWidgets container;
	private JsUint8Array zippedContent;
	
	

	public void createAndStartModule() {
		
		ArrayList<ArticleElement> articles = getGeoGebraMobileTags();
		for (ArticleElement article : articles) {
			GWT.log(article.getClassName());
		}
		
		
		eventBus = new MyEventBus(container, zippedContent);
	    
    }
	
	private ArrayList<ArticleElement> getGeoGebraMobileTags() {
		NodeList<Element> nodes = Dom.getElementsByClassName(GeoGebraConstants.GGM_CLASS_NAME);
		ArrayList<ArticleElement> articleNodes = new ArrayList<ArticleElement>();
		for (int i = 0; i < nodes.getLength(); i++) {
			articleNodes.add(ArticleElement.as(nodes.getItem(i)));
		}
		return articleNodes;
	}
	
	
	

	public Widget getStartView() {
	    // TODO Auto-generated method stub
	    return null;
    }

}
