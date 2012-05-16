/**
 * 
 */
package geogebra.web.gui.app;

import java.util.Date;

import geogebra.common.GeoGebraConstants;
import geogebra.common.main.AbstractApplication;
import geogebra.web.html5.ArticleElement;
import geogebra.web.html5.Dom;
import geogebra.web.html5.View;
import geogebra.web.main.Application;
import geogebra.web.presenter.LoadFilePresenter;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;

/**
 * @author gabor
 * 
 * Creates the App base structure.
 *
 */
public class GeoGebraAppFrame extends Composite {

	interface Binder extends UiBinder<DockLayoutPanel, GeoGebraAppFrame> { }
	private static final Binder binder = GWT.create(Binder.class);
	
	/** Loads file into active GeoGebraFrame */
	public static LoadFilePresenter fileLoader = new LoadFilePresenter();
	
	//declared in uibinder xml!
	public static int GGWVIewWrapper_WIDTH = 300;
	public static int GGWToolBar_HEIGHT = 50;
	public static int GGWCommandLine_HEIGHT = 50;
	
	@UiField GGWToolBar ggwToolBar;
	@UiField GGWCommandLine ggwCommandLine;
	MySplitLayoutPanel ggwSplitLayoutPanel;
	
	DockLayoutPanel outer = null;

	private AbstractApplication app;
	
	public GeoGebraAppFrame() {
		initWidget(outer = binder.createAndBindUi(this));
		outer.add(ggwSplitLayoutPanel = new MySplitLayoutPanel());
		
	    // Get rid of scrollbars, and clear out the window's built-in margin,
	    // because we want to take advantage of the entire client area.
	    Window.enableScrolling(false);
	    Window.setMargin("0px");

	    // Add the outer panel to the RootLayoutPanel, so that it will be
	    // displayed.
	    RootLayoutPanel root = RootLayoutPanel.get();
	    root.add(this);
	    root.forceLayout();
	}
	
	@Override
    protected void onLoad() {
		init();
	}
	
	private int cw;
	private int ch;


	private void init() {
		ArticleElement article = ArticleElement.as(Dom.querySelector(GeoGebraConstants.GGM_CLASS_NAME));
		Date creationDate = new Date();
		article.setId(GeoGebraConstants.GGM_CLASS_NAME+creationDate.getTime());
		cw = (Window.getClientWidth() - (GGWVIewWrapper_WIDTH + ggwSplitLayoutPanel.getSplitLayoutPanel().getSplitterSize())); 
		ch = (Window.getClientHeight() - (GGWToolBar_HEIGHT + GGWCommandLine_HEIGHT));
		app = createApplication(article,this);
		ggwSplitLayoutPanel.attachApp(app);
		ggwCommandLine.attachApp(app);
		
		//take in GeoGebraTube link
		LinkToGGT lggt = new LinkToGGT();
		lggt.setStyleName("linktoggt");
		RootPanel.get().add(lggt);
    }
	
	/**
	 * @return int computed width of the canvas
	 * 
	 * (Window.clientWidth - GGWViewWrapper (left - side) - splitter size)
	 */
	public int getCanvasCountedWidth() {
		return cw;
	}
	
	/**
	 * @return int computed height of the canvas
	 * 
	 * (Window.clientHeight - GGWToolbar - GGWCommandLine)
	 */
	public int getCanvasCountedHeight() {
		return ch;
	}


	private AbstractApplication createApplication(ArticleElement article,
            GeoGebraAppFrame geoGebraAppFrame) {
		return new Application(article, geoGebraAppFrame);
    }


	public void finishAsyncLoading(ArticleElement articleElement,
            GeoGebraAppFrame ins, Application app) {
	    handleLoadFile(articleElement,app);
	    
    }
	
	private static void handleLoadFile(ArticleElement articleElement,
			Application app) {
		View view = new View(articleElement, app);
		fileLoader.setView(view);
		fileLoader.onPageLoad();
	}
	
	/**
	 * @return Canvas
	 * 
	 * Return the canvas in UiBinder of EuclidianView1.ui.xml
	 */
	public Canvas getEuclidianView1Canvas() {
		return ggwSplitLayoutPanel.getGGWGraphicsView().getEuclidianView1Wrapper().getCanvas();
	}
	
	/**
	 * @return AbsolutePanel
	 * 
	 * EuclidianViewPanel for wrapping textfields
	 */
	public AbsolutePanel getEuclidianView1Panel() {
		return ggwSplitLayoutPanel.getGGWGraphicsView()	.getEuclidianView1Wrapper().getEuclidianPanel();
	}
	
	public SplitLayoutPanel getGGWSplitLayoutPanel() {
		return ggwSplitLayoutPanel.getSplitLayoutPanel();
	}

	
	/**
	 * @return GGWToolbar the Toolbar container
	 */
	public GGWToolBar getGGWToolbar() {
	    return ggwToolBar;
    }

}
