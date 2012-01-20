package geogebra.web.gui.app;

import geogebra.common.GeoGebraConstants;
import geogebra.web.css.CssWeb;
import geogebra.web.css.GuiResources;
import geogebra.web.helper.RequestTemplateXhr2;
import geogebra.web.helper.UrlFetcherImpl;
import geogebra.web.html5.ArticleElement;
import geogebra.web.html5.View;
import geogebra.web.main.Application;
import geogebra.web.presenter.LoadFilePresenter;

import java.util.ArrayList;

import com.gargoylesoftware.htmlunit.html.HtmlAttributeChangeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class GeoGebraFrame extends VerticalPanel {
	
	private static ArrayList<GeoGebraFrame> instances = new ArrayList<GeoGebraFrame>();
	private static GeoGebraFrame activeInstance;

	private static LoadFilePresenter fileLoader = new LoadFilePresenter(
			new UrlFetcherImpl(
					new RequestTemplateXhr2(),
					GeoGebraConstants.URL_PARAM_GGB_FILE,
					GeoGebraConstants.URL_PARAM_PROXY,
					GeoGebraConstants.PROXY_SERVING_LOCATION
					));
	
	protected Application app;
	
	public GeoGebraFrame() {
		super();
		instances.add(this);
		activeInstance = this;
	}

	public static void main(ArrayList<ArticleElement> geoGebraMobileTags) {
		init(geoGebraMobileTags);
    }

	private static void init(ArrayList<ArticleElement> geoGebraMobileTags) {
		
		GuiResources.INSTANCE.getCssWeb().ensureInjected();
	   
		for (ArticleElement articleElement : geoGebraMobileTags) {
	        GeoGebraFrame inst = new GeoGebraFrame();
	        Application app = inst.createApplication();
	        inst.app = app;
	        inst.add(app.buildApplicationPanel());
	        RootPanel.get(articleElement.getId()).add(inst); 
	        handleLoadFile(articleElement,app);
        }
    }

	private static void handleLoadFile(ArticleElement articleElement, Application app) {
		View view = new View(articleElement,app);
	    fileLoader.setView(view);
	    fileLoader.onPageLoad();
	    
    }

	public Application getApplication() {
    	return app;
    }

	public void setApplication(Application app) {
    	this.app = app;
    }
	
	protected Application createApplication() {
		return new Application();
	}

}
