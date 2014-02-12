/**
 * 
 */
package geogebra.web.gui.app;

import geogebra.common.GeoGebraConstants;
import geogebra.common.main.App;
import geogebra.html5.gui.browser.BrowseGUI;
import geogebra.html5.util.ArticleElement;
import geogebra.html5.util.JSON;
import geogebra.html5.util.View;
import geogebra.html5.util.debug.GeoGebraLogger;
import geogebra.web.gui.layout.DockGlassPaneW;
import geogebra.web.gui.layout.panels.EuclidianDockPanelW;
import geogebra.web.html5.Dom;
import geogebra.web.main.AppW;
import geogebra.web.main.AppWapplication;
import geogebra.web.presenter.LoadFilePresenter;

import java.util.Date;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * @author gabor
 * 
 * Creates the App base structure.
 *
 */
public class GeoGebraAppFrame extends ResizeComposite {

	//interface Binder extends UiBinder<DockLayoutPanel, GeoGebraAppFrame> { }
	//private static final Binder binder = GWT.create(Binder.class);
	
	/** Loads file into active GeoGebraFrame */
	public static LoadFilePresenter fileLoader = new LoadFilePresenter();
	
	//declared in uibinder xml!
	public static int GGWVIewWrapper_WIDTH = 300;
	public static int GGWSpreadsheetView_WIDTH = 400;
	public static int GGWToolBar_HEIGHT = 61;
	private static final int GGWStyleBar_HEIGHT = 65;
	public static int GGWCommandLine_HEIGHT = 50;
	
	@UiField GGWToolBar ggwToolBar;
	@UiField GGWCommandLine ggwCommandLine;
	@UiField GGWMenuBar ggwMenuBar;

	DockLayoutPanel outer = null;
	GGWFrameLayoutPanel frameLayout;
	public AppW app;

	public GeoGebraAppFrame() {
		frameLayout = new GGWFrameLayoutPanel();		
		initWidget(frameLayout);
		
		//ggwSplitLayoutPanel = frameLayout.getSPLayout();
		ggwCommandLine = frameLayout.getCommandLine();
		ggwMenuBar = frameLayout.getMenuBar();
		ggwToolBar = frameLayout.getToolBar();
		
		
		//initWidget(outer = binder.createAndBindUi(this));
		//boolean showCAS = "true".equals(RootPanel.getBodyElement().getAttribute("data-param-showCAS"));
		//outer.add(ggwSplitLayoutPanel = new MySplitLayoutPanel(true, true, false, !showCAS, showCAS));
		
	    // Get rid of scrollbars, and clear out the window's built-in margin,
	    // because we want to take advantage of the entire client area.
	    Window.enableScrolling(false);
	    Window.setMargin("0px");
	    addStyleName("GeoGebraAppFrame");

	    // Add the outer panel to the RootLayoutPanel, so that it will be
	    // displayed.
	    RootLayoutPanel rootLayoutPanel = RootLayoutPanel.get();
	    rootLayoutPanel.add(this);
	    rootLayoutPanel.forceLayout();
	    
	}
	
	@Override
	public void onResize() {
		super.onResize();

		/**
		 * Keep RootPanel and RootLayoutPanel dimensions the same so that
		 * tooltips will work. Tooltip positions are based on RootPanel.
		 */

		RootLayoutPanel rootLayoutPanel = RootLayoutPanel.get();
		RootPanel rootPanel = RootPanel.get();
		rootPanel.setPixelSize(rootLayoutPanel.getOffsetWidth(),
		        rootLayoutPanel.getOffsetHeight());
	}
	
	public static void setCloseMessage(final App appl) {
		// popup when the user wants to exit accidentally
        Window.addWindowClosingHandler(new Window.ClosingHandler() {
            public void onWindowClosing(ClosingEvent event) {
            	event.setMessage(appl.getPlain("CloseApplicationLoseUnsavedData"));
            }
        });
	}
	
	public static void removeCloseMessage(){
		Window.addWindowClosingHandler(new Window.ClosingHandler() {
			public void onWindowClosing(ClosingEvent event) {
				event.setMessage(null);
			}
		});
	}

	
	@Override
    protected void onLoad() {
//		init();
		setVisible(false);
		geoIPCall();				
	}
	
	
	private void geoIPCall() {
		
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(GeoGebraConstants.GEOIP_URL));
		
		try {
			Request request = builder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					AppW.geoIPCountryName = "";
					AppW.geoIPLanguage = "";
					init();
				}
				
				public void onResponseReceived(Request request, Response response) {
					AppW.geoIPCountryName = "";
					AppW.geoIPLanguage = "";
					if (200 == response.getStatusCode()) {
						JavaScriptObject geoIpInfos = JSON.parse(response.getText());
						AppW.geoIPCountryName = JSON.get(geoIpInfos, "geoIp");
						String languages = JSON.get(geoIpInfos,"acceptLanguage");
						if(languages!=null){
							if(languages.contains(",")){
								AppW.geoIPLanguage = languages.substring(0, languages.indexOf(","));	
							}else{
								AppW.geoIPLanguage = languages;
							}
						}
					}						
					init();
					
				}
			});
		} catch (Exception e) {
		       App.error(e.getLocalizedMessage());
		       AppW.geoIPCountryName = "";
		       AppW.geoIPLanguage = "";
			   init();
	    }
	}	
	
	private int cw;
	private int ch;


	protected void init() {
		setVisible(true);
		ArticleElement article = ArticleElement.as(Dom.querySelector(GeoGebraConstants.GGM_CLASS_NAME));
		GeoGebraLogger.startLogger(article);
		Date creationDate = new Date();
		article.setId(GeoGebraConstants.GGM_CLASS_NAME+creationDate.getTime());
		//cw = (Window.getClientWidth() - (GGWVIewWrapper_WIDTH + ggwSplitLayoutPanel.getSplitLayoutPanel().getSplitterSize())); 
		//ch = (Window.getClientHeight() - (GGWToolBar_HEIGHT + GGWCommandLine_HEIGHT + GGWStyleBar_HEIGHT));
		
		cw = Window.getClientWidth(); 
		ch = Window.getClientHeight() ;
		
		app = createApplication(article,this);

		setCloseMessage(app);

//		((AppW)app).initializeLanguage();

		//Debugging purposes
		AppW.displayLocaleCookie();
    }

	/**
	 * This method should only run once, at the startup of the application
	 * In contrast, setFrameLayout runs every time a new ggb file loads
	 */
	public void onceAfterCoreObjectsInited() {

		// layout things - moved to AppWapplication, appropriate places
		// frameLayout.setLayout(app);

		// Graphics view
		frameLayout.getGGWGraphicsView().attachApp(app);

		// Algebra input
		ggwCommandLine.attachApp(app);

		// Menu bar
		ggwMenuBar.init(app);
		app.getObjectPool().setGgwMenubar(ggwMenuBar);

		// Toolbar
		if (!ggwToolBar.isInited()) {
			ggwToolBar.init(app);
			ggwToolBar.updateToolbarPanel();
		}
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


	protected AppW createApplication(ArticleElement article,
            GeoGebraAppFrame geoGebraAppFrame) {
		return new AppWapplication(article, geoGebraAppFrame);
    }


	public void finishAsyncLoading(ArticleElement articleElement,
            GeoGebraAppFrame ins, AppW app) {
	    handleLoadFile(articleElement,app);	    
    }
	
	private static void handleLoadFile(ArticleElement articleElement,
			AppW app) {
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
		
		return frameLayout.getGGWGraphicsView().getEuclidianView1Wrapper().getCanvas();
		//return ggwSplitLayoutPanel.getGGWGraphicsView().getEuclidianView1Wrapper().getCanvas();
	}
	
	/**
	 * @return AbsolutePanel
	 * 
	 * EuclidianViewPanel for wrapping textfields
	 */
	public EuclidianDockPanelW getEuclidianView1Panel() {
		
		//return ggwSplitLayoutPanel.getGGWGraphicsView().getEuclidianView1Wrapper();	
		return frameLayout.getGGWGraphicsView().getEuclidianView1Wrapper();
		
	}

	/**
	 * @return GGWToolbar the Toolbar container
	 */
	public GGWToolBar getGGWToolbar() {
	    return ggwToolBar;
    }

	public void setFrameLayout(){
		frameLayout.setLayout(app);
	}

	public DockGlassPaneW getGlassPane(){
		return frameLayout.getGlassPane();
	}

	private boolean[] childVisible = new boolean[0];
	public void showBrowser(BrowseGUI bg) {
	    int count = frameLayout.getWidgetCount();
	    for(int i = 0; i<count;i++){
	    	childVisible[i] = frameLayout.getWidget(i).isVisible(); 
	    	frameLayout.getWidget(i).setVisible(false);
	    }
	    frameLayout.add(bg);
	    bg.setVisible(true);
	    bg.loadFeatured();
	    bg.setFrame(this);
	    frameLayout.forceLayout();
	    
    }

	public void hideBrowser(BrowseGUI bg) {
		frameLayout.remove(bg);
		int count = frameLayout.getWidgetCount();
		for(int i = 0; i<count;i++){
			if(childVisible.length > i){
				frameLayout.getWidget(i).setVisible(childVisible[i]);
			}
	    }
	    frameLayout.setLayout(app);
	    frameLayout.forceLayout();
    }
}
