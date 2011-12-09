package geogebra.web.mvp4g;

import java.util.ArrayList;

import geogebra.common.GeoGebraConstants;
import geogebra.web.eventbus.MyEventBus;
import geogebra.web.helper.FileLoadCallback;
import geogebra.web.helper.RequestTemplateXhr2;
import geogebra.web.helper.UrlFetcher;
import geogebra.web.helper.UrlFetcherImpl;
import geogebra.web.html5.ArticleElement;
import geogebra.web.html5.Dom;
import geogebra.web.html5.View;
import geogebra.web.jso.JsUint8Array;
import geogebra.web.presenter.LoadFilePresenter;

import com.gargoylesoftware.htmlunit.javascript.host.Node;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.google.web.bindery.event.shared.Event.Type;

/**
 * @author ancsing
 * Just mimic class for Mvp4g
 *
 */
public class Mvp4gModule {
	
	ArrayList<MyEventBus> eventbuses = new ArrayList<MyEventBus>();
	private HasWidgets container;
	private JsUint8Array zippedContent;
	
	

	public void createAndStartModule() {
		
		ArrayList<ArticleElement> articles = getGeoGebraMobileTags();
		for (ArticleElement article : articles) {
			MyEventBus _eventFlow = new MyEventBus(article);
			_eventFlow.addLoadHandler(
					new LoadFilePresenter(
							new UrlFetcherImpl(
									new RequestTemplateXhr2(),
									GeoGebraConstants.URL_PARAM_GGB_FILE,
									GeoGebraConstants.URL_PARAM_PROXY,
									GeoGebraConstants.PROXY_SERVING_LOCATION
									), new View(article)
							)
					);
			_eventFlow.pageLoad();
			eventbuses.add(_eventFlow);
			};
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
