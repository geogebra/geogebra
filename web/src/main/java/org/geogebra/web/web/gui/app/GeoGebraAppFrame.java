/**
 * 
 */
package org.geogebra.web.web.gui.app;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.gui.view.algebra.MathKeyboardListener;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.HasAppletProperties;
import org.geogebra.web.html5.util.ArticleElement;
import org.geogebra.web.html5.util.Dom;
import org.geogebra.web.html5.util.LoadFilePresenter;
import org.geogebra.web.html5.util.View;
import org.geogebra.web.html5.util.debug.GeoGebraLogger;
import org.geogebra.web.web.gui.GuiManagerW;
import org.geogebra.web.web.gui.HeaderPanelDeck;
import org.geogebra.web.web.gui.MyHeaderPanel;
import org.geogebra.web.web.gui.applet.AppletFactory;
import org.geogebra.web.web.gui.laf.GLookAndFeel;
import org.geogebra.web.web.gui.layout.DockGlassPaneW;
import org.geogebra.web.web.gui.layout.panels.EuclidianDockPanelW;
import org.geogebra.web.web.main.GDevice;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.ui.HeaderPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * @author gabor
 * 
 * Creates the App base structure.
 *
 */
public class GeoGebraAppFrame extends ResizeComposite implements
        HeaderPanelDeck, HasAppletProperties {
	
	/** Loads file into active GeoGebraFrame */
	public static LoadFilePresenter fileLoader = new LoadFilePresenter();
	
	public GGWToolBar ggwToolBar;
	private final GGWCommandLine ggwCommandLine;
	private final GGWMenuBar ggwMenuBar;
	GGWFrameLayoutPanel frameLayout;
	public AppW app;
	private int cw;
	private int ch;
	private final GLookAndFeel laf;

	private final GDevice device;

	private final AppletFactory factory;

	private int id;
	private static int counter = 0;
	public GeoGebraAppFrame(GLookAndFeel laf, GDevice device, AppletFactory factory) {
		this.laf = laf;
		this.device = device;
		this.factory = factory;
		this.id = counter++;
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
	    addStyleName("GeoGebraFrame");

	    // Add the outer panel to the RootLayoutPanel, so that it will be
	    // displayed.
	    final RootLayoutPanel rootLayoutPanel = RootLayoutPanel.get();
	    rootLayoutPanel.add(this);
	    rootLayoutPanel.forceLayout();
	    
	}
	
	public GGWCommandLine getAlgebraInput(){
		return ggwCommandLine;
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
		device.setMinWidth(this);
	}
	
	/**
	 * Synchronizes the size of the rootLayoutPanel and the rootPanel.
	 */
	public void syncPanelSizes() {
		/*
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
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			public void execute() {
				init();
			}
		});		
	}

	public void init() {
		setVisible(true);
		final ArticleElement article = ArticleElement.as(Dom.querySelector(GeoGebraConstants.GGM_CLASS_NAME));
		GeoGebraLogger.startLogger(article);
		article.initID(0);
		//cw = (Window.getClientWidth() - (GGWVIewWrapper_WIDTH + ggwSplitLayoutPanel.getSplitLayoutPanel().getSplitterSize())); 
		//ch = (Window.getClientHeight() - (GGWToolBar_HEIGHT + GGWCommandLine_HEIGHT + GGWStyleBar_HEIGHT));
		
		cw = Window.getClientWidth(); 
		ch = Window.getClientHeight() ;
		
		app = factory.getApplication(article, this, this.laf, this.device); 
		App.debug("Callbacks ...");
		
		this.addDomHandler(new MouseDownHandler() {
			public void onMouseDown(MouseDownEvent event) {
				if(!CancelEventTimer.cancelMouseEvent()){
					closePopupsAndMaybeMenu(event.getNativeEvent());
				}
			}
		}, MouseDownEvent.getType());
		
		this.addDomHandler(new TouchStartHandler() {
			public void onTouchStart(TouchStartEvent event) {
				event.stopPropagation();
				CancelEventTimer.touchEventOccured();
				closePopupsAndMaybeMenu(event.getNativeEvent());
			}
		}, TouchStartEvent.getType());
		
		//make sure SMART widget has border when in app mode
		if(laf.isSmart()){
			RootLayoutPanel.get().getElement().addClassName("AppFrameParent");
		}
		//Debugging purposes
		App.debug("Done");
    }
	
	/**
	 * This method will also close the menu if the event doesn't target the menu
	 * or the menu toggle button and there is no drag in progress.
	 * @see GeoGebraAppFrame#init()
	 * @param event native event
	 */
	public void closePopupsAndMaybeMenu(NativeEvent event) {
		app.closePopups();
		if (isMenuOpen() &&
				!Dom.eventTargetsElement(event, getGGWMenuBar().getElement()) &&
				!Dom.eventTargetsElement(event, getGGWToolbar().getOpenMenuButtonElement()) &&
				!frameLayout.getGlassPane().isDragInProgress()) {
			app.toggleMenu();
		}
	}

	/**
	 * This method should only run once, at the startup of the application
	 * In contrast, setFrameLayout runs every time a new ggb file loads
	 */
	public void onceAfterCoreObjectsInited() {

		// layout things - moved to AppWapplication, appropriate places
		// frameLayout.setLayout(app);
		//frameLayout.registerPreviewNativeEventHandler(app);

		// Graphics view
		frameLayout.getGGWGraphicsView().attachApp(app);

		// Algebra input
		if(app.showAlgebraInput()){
			getAlgebraInput().attachApp(app);
		}

		// Menu bar
		//Do not call init here, wait for toggle
		((GuiManagerW)app.getGuiManager()).getObjectPool().setGgwMenubar(ggwMenuBar);

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

	public void finishAsyncLoading(final ArticleElement articleElement,
	        final AppW appw) {
		handleLoadFile(articleElement, appw);
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
	
	/**
	 * @return GGWMenuBar the MenuBar container
	 */
	public GGWMenuBar getGGWMenuBar() {
		return ggwMenuBar;
	}

	public void setFrameLayout(){
		frameLayout.setLayout(app);
	}

	public DockGlassPaneW getGlassPane(){
		return frameLayout.getGlassPane();
	}

	private boolean[] childVisible = new boolean[0];
	private boolean isBrowserShowing = false;
	
	public void showBrowser(final HeaderPanel bg) {
		this.isBrowserShowing = true;
	    final int count = frameLayout.getWidgetCount();

	    for(int i = 0; i<count;i++){
	    	if(bg == frameLayout.getWidget(i)){
	    		return;
	    	}
	    }
		childVisible = new boolean[count];
	    for(int i = 0; i<count;i++){
			childVisible[i] = frameLayout.getWidget(i).isVisible();
	    	frameLayout.getWidget(i).setVisible(false);
	    }
	    frameLayout.add(bg);
	    bg.setVisible(true);

	    ((MyHeaderPanel)bg).setFrame(this);
	    frameLayout.forceLayout();
	    
    }

	public void hideBrowser(final MyHeaderPanel bg) {
		this.isBrowserShowing = false;
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
	
	public boolean isBrowserShowing() {
		return this.isBrowserShowing;
	}

	public boolean toggleMenu() {
	    return frameLayout.toggleMenu();
    }
	
	public void hideMenu() {
		frameLayout.hideMenu();
	}

	public boolean isMenuOpen() {
		return frameLayout.isMenuOpen();
	}

	public GGWMenuBar getMenuBar() {
	    return frameLayout.getMenuBar();
    }

	public void setMenuHeight(boolean showAlgebraInput) {
	   this.frameLayout.setMenuHeight(showAlgebraInput);
    }
	
	@Override
	public void showKeyBoard(boolean show, MathKeyboardListener textField,
	        boolean forceShow) {
		if (forceShow) {
			this.frameLayout.doShowKeyBoard(show, textField);
		} else {
			this.frameLayout.keyBoardNeeded(show, textField);
		}
	}

	@Override
	public void setWidth(int width) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setHeight(int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void resetAutoSize() {
		// TODO Auto-generated method stub

	}

	@Override
	public void enableRightClick(boolean enable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void enableLabelDrags(boolean enable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void enableShiftDragZoom(boolean enable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showToolBar(boolean show) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showMenuBar(boolean show) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showAlgebraInput(boolean show) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showResetIcon(boolean show) {
		// TODO Auto-generated method stub

	}

	@Override
	public JavaScriptObject getOnLoadCallback() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isKeyboardShowing() {
		return this.frameLayout.keyboardShowing;
	}

	public void showKeyboardOnFocus() {
		this.app.getGuiManager().getOnScreenKeyboard(null, frameLayout)
				.showOnFocus();

	}
}
