/**
 * 
 */
package org.geogebra.web.web.gui.app;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.gui.view.algebra.MathKeyboardListener;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.ArticleElement;
import org.geogebra.web.html5.util.Dom;
import org.geogebra.web.html5.util.ViewW;
import org.geogebra.web.html5.util.debug.LoggerW;
import org.geogebra.web.keyboard.KeyBoardButtonBase;
import org.geogebra.web.web.gui.GuiManagerW;
import org.geogebra.web.web.gui.HeaderPanelDeck;
import org.geogebra.web.web.gui.MyHeaderPanel;
import org.geogebra.web.web.gui.applet.AppletFactory;
import org.geogebra.web.web.gui.laf.GLookAndFeel;
import org.geogebra.web.web.gui.layout.DockGlassPaneW;
import org.geogebra.web.web.gui.layout.DockManagerW;
import org.geogebra.web.web.gui.layout.panels.EuclidianDockPanelW;
import org.geogebra.web.web.main.GDevice;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.ui.HeaderPanel;
import com.google.gwt.user.client.ui.Panel;
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
		HeaderPanelDeck {
	
	
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
	private Object lastBG;

	public GeoGebraAppFrame(GLookAndFeel laf, GDevice device, AppletFactory factory) {
		this.laf = laf;
		this.device = device;
		this.factory = factory;
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
		if (!laf.isSmart()) {
			// RootPanel.getBodyElement().addClassName("application");
		}
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
		if (lastBG != null) {
			int width = (int) app.getWidth();
			int height = (int) app.getHeight();
			((MyHeaderPanel) lastBG).setPixelSize(width, height);
			((MyHeaderPanel) lastBG).resizeTo(width, height);
		}
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
			@Override
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
			@Override
			public void execute() {
				init();
				if (app.isExam()
						&& !app.getSettings().getEuclidian(1).isEnabled()) {
					((DockManagerW) app.getGuiManager().getLayout()
							.getDockManager()).hide(App.VIEW_EUCLIDIAN, true);
				}
			}
		});		
	}

	public void init() {
		setVisible(true);
		final ArticleElement article = ArticleElement.as(Dom.querySelector(GeoGebraConstants.GGM_CLASS_NAME));
		LoggerW.startLogger(article);
		article.initID(0);
		//cw = (Window.getClientWidth() - (GGWVIewWrapper_WIDTH + ggwSplitLayoutPanel.getSplitLayoutPanel().getSplitterSize())); 
		//ch = (Window.getClientHeight() - (GGWToolBar_HEIGHT + GGWCommandLine_HEIGHT + GGWStyleBar_HEIGHT));
		
		cw = Window.getClientWidth(); 
		ch = Window.getClientHeight() ;
		
		app = factory.getApplication(article, this, this.laf, this.device); 
		Log.debug("Callbacks ...");
		
		this.addDomHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				if(!CancelEventTimer.cancelMouseEvent()){
					closePopupsAndMaybeMenu(event.getNativeEvent());
				}
			}
		}, MouseDownEvent.getType());
		
		this.addDomHandler(new TouchStartHandler() {
			@Override
			public void onTouchStart(TouchStartEvent event) {
				event.stopPropagation();
				CancelEventTimer.touchEventOccured();
				closePopupsAndMaybeMenu(event.getNativeEvent());
			}
		}, TouchStartEvent.getType());
		
		addDomHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				event.stopPropagation();
				CancelEventTimer.cancelMouseEvent();
				int viewId = app.getGuiManager().getLayout().getDockManager()
						.getFocusedViewId();
				if (viewId != App.VIEW_ALGEBRA
						&& !(event.getSource() instanceof KeyBoardButtonBase)) {
					if (!keyboardHit(event)) {
						app.getAlgebraView().resetItems(true);
					}
				}

			}
		}, ClickEvent.getType());

		//make sure SMART widget has border when in app mode
		if(laf.isSmart()){
			RootLayoutPanel.get().getElement().addClassName("AppFrameParent");
		}
		//Debugging purposes
		Log.debug("Done");
    }
	
	protected static boolean keyboardHit(ClickEvent event) {
		try {
			String className = Element
					.as(event.getNativeEvent().getEventTarget())
					.getClassName();
			if (className.contains("KeyBoardButton")) {
				return true;
			}
			className = Element.as(event.getNativeEvent().getEventTarget())
					.getParentElement().getClassName();
			if (className.contains("KeyBoardButton")) {
				return true;
			}
		} catch (Exception e) {
			Log.warn(e + "");
		}
		return false;
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
 !Dom.eventTargetsElement(event, getToolbar()
						.getOpenMenuButtonElement())
				&&
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
		final ViewW view = new ViewW(articleElement, app);
		ViewW.fileLoader.setView(view);
		ViewW.fileLoader.onPageLoad();
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
	@Override
	public GGWToolBar getToolbar() {
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
	
	@Override
	public void showBrowser(final HeaderPanel bg) {
		this.lastBG = bg;
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

		if (bg instanceof MyHeaderPanel) {
			((MyHeaderPanel) bg).setFrame(this);
		}
	    frameLayout.forceLayout();
	    
    }

	@Override
	public void hideBrowser(final MyHeaderPanel bg) {
		this.lastBG = null;
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
	
	public void hideMenu() {
		frameLayout.hideMenu();
	}

	public boolean isMenuOpen() {
		return frameLayout.isMenuOpen();
	}

	public GGWMenuBar getMenuBar() {
	    return frameLayout.getMenuBar();
    }

	@Override
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
	public void updateKeyboardHeight() {
		this.frameLayout.updateKeyboardHeight();
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

	@Override
	public void showKeyboardOnFocus() {
		this.app.getGuiManager().getOnScreenKeyboard(null, frameLayout)
				.showOnFocus();

	}

	@Override
	public double getKeyboardHeight() {
		return this.frameLayout.getKeyboardHeight();
	}

	public Panel getPanel() {
		return this.frameLayout;
	}

	@Override
	public void remove() {
		this.removeFromParent();

	}

	public boolean isHeaderPanelOpen() {
		return this.lastBG != null;
	}
}
