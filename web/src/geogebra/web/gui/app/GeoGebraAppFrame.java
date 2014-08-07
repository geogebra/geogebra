/**
 * 
 */
package geogebra.web.gui.app;

import geogebra.common.GeoGebraConstants;
import geogebra.common.main.App;
import geogebra.html5.Browser;
import geogebra.html5.gui.MyHeaderPanel;
import geogebra.html5.util.ArticleElement;
import geogebra.html5.util.JSON;
import geogebra.html5.util.View;
import geogebra.html5.util.debug.GeoGebraLogger;
import geogebra.web.gui.app.docklayout.MyDockLayoutPanel;
import geogebra.web.gui.layout.DockGlassPaneW;
import geogebra.web.gui.layout.panels.EuclidianDockPanelW;
import geogebra.web.html5.Dom;
import geogebra.web.main.AppW;
import geogebra.web.main.AppWapplication;
import geogebra.web.presenter.LoadFilePresenter;

import java.util.Date;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
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
	
	/** Loads file into active GeoGebraFrame */
	public static LoadFilePresenter fileLoader = new LoadFilePresenter();
	
	public GGWToolBar ggwToolBar;
	private final GGWCommandLine ggwCommandLine;
	private final GGWMenuBar ggwMenuBar;

	MyDockLayoutPanel outer = null;
	GGWFrameLayoutPanel frameLayout;
	public AppW app;

	private Callback<String, String> callback;

	public GeoGebraAppFrame() {
		frameLayout = newGGWFrameLayoutPanel();		
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
	    final RootLayoutPanel rootLayoutPanel = RootLayoutPanel.get();
	    rootLayoutPanel.add(this);
	    rootLayoutPanel.forceLayout();
	    
	}
	
	/**
	 * For touch
	 * @param callback
	 */
	public GeoGebraAppFrame(final Callback<String, String> callback) {
		this();
	    this.callback = callback;
    }

	/**
	 * 
	 * @return new GGWFrameLayoutPanel
	 */
	protected GGWFrameLayoutPanel newGGWFrameLayoutPanel(){
		return new GGWFrameLayoutPanel();
	}
	
	@Override
	public void onResize() {
		super.onResize();

		/**
		 * Keep RootPanel and RootLayoutPanel dimensions the same so that
		 * tooltips will work. Tooltip positions are based on RootPanel.
		 */

		final RootLayoutPanel rootLayoutPanel = RootLayoutPanel.get();
		final RootPanel rootPanel = RootPanel.get();
		rootPanel.setPixelSize(rootLayoutPanel.getOffsetWidth(),
		        rootLayoutPanel.getOffsetHeight());
	}
	
	
	
	public static void removeCloseMessage(){
		Window.addWindowClosingHandler(new Window.ClosingHandler() {
			public void onWindowClosing(final ClosingEvent event) {
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
		
		final RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(Browser.normalizeURL(GeoGebraConstants.GEOIP_URL)));
		
		try {
			final Request request = builder.sendRequest(null, new RequestCallback() {
				public void onError(final Request request, final Throwable exception) {
					AppW.geoIPCountryName = "";
					AppW.geoIPLanguage = "";
					init();
				}
				
				public void onResponseReceived(final Request request, final Response response) {
					AppW.geoIPCountryName = "";
					AppW.geoIPLanguage = "";
					if (200 == response.getStatusCode()) {
						final JavaScriptObject geoIpInfos = JSON.parse(response.getText());
						AppW.geoIPCountryName = JSON.get(geoIpInfos, "geoIp");
						final String languages = JSON.get(geoIpInfos,"acceptLanguage");
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
		} catch (final Exception e) {
		       App.error(e.getLocalizedMessage());
		       AppW.geoIPCountryName = "";
		       AppW.geoIPLanguage = "";
			   init();
	    }
	}	
	
	private int cw;
	private int ch;


	public void init() {
		setVisible(true);
		final ArticleElement article = ArticleElement.as(Dom.querySelector(GeoGebraConstants.GGM_CLASS_NAME));
		GeoGebraLogger.startLogger(article);
		final Date creationDate = new Date();
		article.setId(GeoGebraConstants.GGM_CLASS_NAME+creationDate.getTime());
		//cw = (Window.getClientWidth() - (GGWVIewWrapper_WIDTH + ggwSplitLayoutPanel.getSplitLayoutPanel().getSplitterSize())); 
		//ch = (Window.getClientHeight() - (GGWToolBar_HEIGHT + GGWCommandLine_HEIGHT + GGWStyleBar_HEIGHT));
		
		cw = Window.getClientWidth(); 
		ch = Window.getClientHeight() ;
		
		app = createApplication(article,this); 
		if (this.callback != null) {
			this.callback.onSuccess("");
		}
		app.getLAF().setCloseMessage(app.getLocalization());
		this.addDomHandler(new ClickHandler(){

			@Override
            public void onClick(final ClickEvent event) {
	            app.closePopups();
	            
            }},ClickEvent.getType());
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
		//Do not call init here, wait for toggle
		app.getObjectPool().setGgwMenubar(ggwMenuBar);

		// Toolbar -- the tools are actually added in LoadFilePresenter
		if (!ggwToolBar.isInited()) {
			ggwToolBar.init(app);
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


	protected AppW createApplication(final ArticleElement article,
            final GeoGebraAppFrame geoGebraAppFrame) {
		return new AppWapplication(article, geoGebraAppFrame, 2);
    }


	public void finishAsyncLoading(final ArticleElement articleElement,
            final GeoGebraAppFrame ins, final AppW app) {
	    handleLoadFile(articleElement,app);	    
    }
	
	private static void handleLoadFile(final ArticleElement articleElement,
			final AppW app) {
		final View view = new View(articleElement, app);
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
	public void showBrowser(final MyHeaderPanel bg) {
	    final int count = frameLayout.getWidgetCount();
	    childVisible = new boolean[count];
	    for(int i = 0; i<count;i++){
	    	childVisible[i] = frameLayout.getWidget(i).isVisible(); 
	    	frameLayout.getWidget(i).setVisible(false);
	    }
	    frameLayout.add(bg);
	    bg.setVisible(true);
	    bg.setFrame(this);
	    frameLayout.forceLayout();
	    
    }

	public void hideBrowser(final MyHeaderPanel bg) {
		frameLayout.remove(bg);
		final int count = frameLayout.getWidgetCount();
		for(int i = 0; i<count;i++){
			if(childVisible.length > i){
				frameLayout.getWidget(i).setVisible(childVisible[i]);
			}
	    }
	    frameLayout.setLayout(app);
	    frameLayout.forceLayout();
	    app.updateViewSizes();
	    
    }

	public boolean toggleMenu() {
	    return frameLayout.toggleMenu();
    }

	public GGWMenuBar getMenuBar() {
	    return frameLayout.getMenuBar();
    }
	
}
