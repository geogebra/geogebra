package geogebra.web.gui.applet;

import geogebra.common.GeoGebraConstants;
import geogebra.common.main.App;
import geogebra.html5.js.ResourcesInjector;
import geogebra.html5.main.HasAppletProperties;
import geogebra.html5.util.ArticleElement;
import geogebra.html5.util.View;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.gui.SplashDialog;
import geogebra.web.main.AppW;
import geogebra.web.presenter.LoadFilePresenter;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * The main frame containing every view / menu bar / ....
 * This Panel (Frame is resize able)
 */
public abstract class GeoGebraFrame extends FlowPanel implements HasAppletProperties {

	private static final int BORDER_WIDTH = 2;
	private static final int BORDER_HEIGHT = 2;

	private static final int WINDOW_WIDTH_PADDING = 16;
	private static final int WINDOW_HEIGHT_PADDING = 16;

	private static ArrayList<GeoGebraFrame> instances = new ArrayList<GeoGebraFrame>();
	private static GeoGebraFrame activeInstance;

	/** Loads file into active GeoGebraFrame */
	public static LoadFilePresenter fileLoader = new LoadFilePresenter();

	/** The application */
	protected AppW app;

	private boolean resize = false;
	private boolean move = false;

	/**
	 * Splash Dialog to get it work quickly
	 */
	public SplashDialog splash;

	/** Creates new GeoGebraFrame */
	public GeoGebraFrame() {
		super();
		instances.add(this);
		activeInstance = this;
	    addStyleName("GeoGebraFrame");
		DOM.sinkEvents(this.getElement(), Event.ONMOUSEDOWN | Event.ONMOUSEMOVE
				| Event.ONMOUSEUP | Event.ONMOUSEOVER);
	}

	
	
	
	/**
	 * @param ae ArticleElement
	 *
	 * In the splashDialog onLoad handler will the application loading continue
	 */	
	protected void createSplash(ArticleElement ae) {
		
		int splashWidth = 427;
		int splashHeight = 120;
		
		//to not touch the DOM twice when computing widht and height
		computeMinDim();
		computeMaxDim();
			
		int width = computeWidth();
		int height = computeHeight();
				
		/*
		if (ae.getDataParamShowMenuBar()) {
			// The menubar has extra height:
			height += 31;
		}
		if (ae.getDataParamShowToolBar()) {
			// The toolbar has extra height:
			height += 57;
		}
		*/
		
		boolean showLogo = ((width >= splashWidth) && (height >= splashHeight));
		splash = new SplashDialog(showLogo, ae.getId());
		splash.setGeoGebraFrame(this);

		if (splash.isPreviewExists()) {
			splashWidth = width;
			splashHeight = height;
		}

		if (width > 0 && height > 0) {
			setWidth((width - BORDER_WIDTH) + "px"); // 2: border
			setComputedWidth(width);
			setComputedHeight(height);
			setHeight((height - BORDER_HEIGHT) + "px"); // 2: border
			splash.addStyleName("splash");
			splash.getElement().getStyle()
	        	.setTop((height / 2) - (splashHeight / 2), Unit.PX);
			if (!ae.isRTL()) {
				splash.getElement().getStyle()
				        .setLeft((width / 2) - (splashWidth / 2), Unit.PX);
			} else {
				splash.getElement().getStyle()
		        		.setRight((width / 2) - (splashWidth / 2), Unit.PX);
			}
		}
		addStyleName("jsloaded");
		add(splash);
	}
	
	private int [] minDim;
	private int [] maxDim;
	
	private void computeMinDim() {
		minDim = ae.getDataParamMinDimensions();
	}
	
	private void computeMaxDim() {
		maxDim = ae.getDataParamMaxDimensions();
	}
	
	private int computeHeight() {
	    //do we have data-param-height?
		int height = ae.getDataParamHeight();
		int minHeight = ae.getDataParamMinHeight();
		int maxHeight = ae.getDataParamMaxHeight();
		if (height > 0) {
			return height;
		}
		
		//do we have fit to screen?
		
		if (ae.getDataParamFitToScreen()) {
			//we must say the bodyelement to resize itself
			height = Window.getClientHeight() - WINDOW_HEIGHT_PADDING;
		}
		if (minHeight > 0 && height < minHeight ) {
			height = minHeight;
		}
		if (maxHeight > 0 && height > maxHeight) {
			height = maxHeight;
		}
		if (minDim != null && height < minDim[1]) {
			height = minDim[1];
		}
		
		if (maxDim != null && height > maxDim[1]) {
			height = maxDim[1];
		}
		return height;
    }




	private int computeWidth() {
	    //do we have data-param-width?
		int width = ae.getDataParamWidth();
		int minWidth = ae.getDataParamMinWidth();
		int maxWidth = ae.getDataParamMaxWidth();
		if (width > 0) {
			return width;
		}
		
		//do we have fit to screen?
		if (ae.getDataParamFitToScreen()) {
			width = Window.getClientWidth() - WINDOW_WIDTH_PADDING;
			//kill the scrollbars. We don't need them anymore
			RootPanel.get().getElement().getStyle().setOverflow(Overflow.HIDDEN);
		}
		if (minWidth > 0 && width < minWidth ) {
			width = minWidth;
		}
		if (maxWidth > 0 && width > maxWidth) {
			width = maxWidth;
		}
		
		if (minDim != null && width < minDim[0]) {
			width = minDim[0];
		}
		
		if (maxDim != null && width > maxDim[0]) {
			width = maxDim[0];
		}
	    return width;
    }

	protected ArticleElement ae;
	
	protected int computedWidth = 0;
	protected int computedHeight = 0;
	
	public void setComputedWidth(int width) {
		this.computedWidth = width;
	}

	public void setComputedHeight(int height) {
		this.computedHeight = height;
	}

	public int getComputedWidth() {
		return computedWidth;
	}

	public int getComputedHeight() {
		return computedHeight;
	}

	private static void setBorder(ArticleElement ae, GeoGebraFrame gf, String dpBorder, int px) {
		ae.getStyle().setBorderWidth(0, Style.Unit.PX);
		ae.getStyle().setBorderStyle(Style.BorderStyle.SOLID);
		ae.getStyle().setBorderColor(dpBorder);
		gf.getStyleElement().getStyle().setBorderWidth(px, Style.Unit.PX);
		gf.getStyleElement().getStyle().setBorderStyle(Style.BorderStyle.SOLID);
		gf.getStyleElement().getStyle().setBorderColor(dpBorder);
	}
	
	/**
	 * Sets the border around the canvas to the data-param-bordercolor property
	 * or leaves it invisible if "none" was set.
	 * @param ae article element
	 * @param gf frame
	 */
	public static void useDataParamBorder(ArticleElement ae, GeoGebraFrame gf) {
		String dpBorder = ae.getDataParamBorder();
		if (dpBorder != null && dpBorder.equals("none")) {
			setBorder(ae, gf, "transparent", 1);
			return;
		}
		
		if (dpBorder == null || dpBorder.length() != 7 ||
			(dpBorder.length() > 0 && dpBorder.charAt(0) != '#')) {
			// FIXME: This check is incomplete, do a complete check.
			dpBorder = GeoGebraConstants.APPLET_UNFOCUSED_BORDER_COLOR;
		}
		setBorder(ae, gf, dpBorder, 1);
	}

	/**
	 * Sets the border around the canvas to be highlighted. At the moment we use
	 * "#9999ff" for this purpose.
	 * @param ae article element
	 * @param gf frame
	 */
	public static void useFocusedBorder(ArticleElement ae, GeoGebraFrame gf) {
		String dpBorder = ae.getDataParamBorder();
		if (dpBorder != null && dpBorder.equals("none")) {
			setBorder(ae, gf, "transparent", 1);
			return;
		}
		setBorder(ae, gf, GeoGebraConstants.APPLET_FOCUSED_BORDER_COLOR, 1);
	}
	
	public void runAsyncAfterSplash() {
		final GeoGebraFrame inst = this;
		final ArticleElement articleElement = ae;

		GWT.runAsync(new RunAsyncCallback() {

			public void onSuccess() {
				ResourcesInjector.injectResources();

				// More testing is needed how can we use
				// createApplicationSimple effectively
				//if (ae.getDataParamGuiOff())
				//	inst.app = inst.createApplicationSimple(articleElement, inst);
				//else
				inst.app = inst.createApplication(articleElement, inst);

				inst.app.setCustomToolBar();
				//useDataParamBorder(articleElement, inst);
			    //inst.add(inst.app.buildApplicationPanel());
				inst.app.buildApplicationPanel();
				    // need to call setLabels here
				// to print DockPanels' titles
				inst.app.setLabels();
			}
			
			public void onFailure(Throwable reason) {
				App.debug("Async load failed");
			}
		});
	}

	public static void finishAsyncLoading(ArticleElement articleElement,
            GeoGebraFrame inst, AppW app) {
	    handleLoadFile(articleElement, app);
    }

	private static void handleLoadFile(ArticleElement articleElement,
			AppW app) {
		View view = new View(articleElement, app);
		fileLoader.setView(view);
		fileLoader.onPageLoad();
	}

	/**
	 * @return the application
	 */
	public App getApplication() {
		return app;
	}

	/**
	 * Sets the Application of the GeoGebraFrame
	 * @param app
	 *          the application
	 */
	public void setApplication(AppW app) {
		this.app = app;
	}

	/**
	 * @param useFullGui
	 *          if false only one euclidianView will be available (without
	 *          menus / ...)
	 * @return the newly created instance of Application
	 */
	protected abstract AppW createApplication(ArticleElement ae, GeoGebraFrame gf);

	/**
	 * @return list of instances of GeogebraFrame
	 */
	public static ArrayList<GeoGebraFrame> getInstances() {
		return instances;
	}

	@Override
	public void onBrowserEvent(Event event) {
		if (app == null || !app.getUseFullGui()){
			return;
		}
		final int eventType = DOM.eventGetType(event);

		switch (eventType) {
		case Event.ONMOUSEOVER:
			if (isCursorAtResizePosition(event)) {
				DOM.setStyleAttribute(getElement(), "cursor", "se-resize");
			} else {
				DOM.setStyleAttribute(getElement(), "cursor", "default");
			}
			break;
		case Event.ONMOUSEDOWN:
			if (isCursorAtResizePosition(event)) {
				if (resize == false) {
					resize = true;
					DOM.setCapture(getElement());
				}
			}
			break;
		case Event.ONMOUSEMOVE:
			if (resize == true) {
				int width = DOM.eventGetClientX(event);
				int height = DOM.eventGetClientY(event);

				setPixelSize(width, height);
				if (app.getGuiManager() != null) {
					app.getGuiManager().resize(width, height);
				}
			} else if (move == true) {
				RootPanel.get().setWidgetPosition(this, DOM.eventGetClientX(event),
						DOM.eventGetClientY(event));
			}
			break;
		case Event.ONMOUSEUP:
			if (move == true) {
				move = false;
				DOM.releaseCapture(getElement());
			}
			if (resize == true) {
				resize = false;
				DOM.releaseCapture(getElement());
			}
			break;
		}
	}

	/**
	 * Determines if the mouse cursor is at the bottom right position of the Frame
	 * @param event
	 *          the cursor event
	 * @return true if cursor is within a 10x10 frame on the bottom right position
	 */
	protected boolean isCursorAtResizePosition(Event event) {
		int cursorY = DOM.eventGetClientY(event);
		int initialY = getAbsoluteTop();
		int height = getOffsetHeight();

		int cursorX = DOM.eventGetClientX(event);
		int initialX = getAbsoluteLeft();
		int width = getOffsetWidth();

		if (((initialX + width - 10) < cursorX && cursorX <= (initialX + width))
				&& ((initialY + height - 10) < cursorY && cursorY <= (initialY + height))) {
			return true;
		}
		return false;
	}

	public static int getInstanceCount() {
		return instances.size();
	}
	
	/**
	 * @param width
	 * 
	 * sets the geogebra-web applet widht
	 */
	public void setWidth(int width) {
		setWidth(width + "px");
		if (app.getGuiManager() != null) {
			app.getGuiManager().resize(width, getOffsetHeight());
		} else {
			app.getEuclidianViewpanel().setPixelSize(width, getOffsetHeight());
			app.getEuclidianViewpanel().onResize();
		}
	}
	
	/**
	 * @param height
	 * 
	 * sets the geogebra-web applet height
	 */
	public void setHeight(int height) {
		setHeight(height + "px");
		if (app.getGuiManager() != null) {
			app.getGuiManager().resize(getOffsetWidth(), height);
		} else {
			app.getEuclidianViewpanel().setPixelSize(getOffsetWidth(), height );
			app.getEuclidianViewpanel().onResize();
		}
	}
	
	/**
	 * @param width
	 * @param height
	 * 
	 * sets the geogebra-web applet size (width, height)
	 */
	public void setSize(int width, int height) {
		setPixelSize(width, height);
		if (app.getGuiManager() != null) {
			app.getGuiManager().resize(width, height);
		}

	}
	
	/**
	 * @param enable
	 * wheter geogebra-web applet rightclick enabled or not
	 */
	public void enableRightClick(boolean enable) {
		app.setRightClickEnabled(enable);
	}
	
	/**
	 * @param enable
	 * 
	 * wheter labels draggable in geogebra-web applets or not
	 */
	public void enableLabelDrags(boolean enable) {
		app.setLabelDragsEnabled(enable);
	}
	
	/**
	 * @param enable
	 * 
	 * wheter shift - drag - zoom enabled in geogebra-web applets or not
	 */
	public void enableShiftDragZoom(boolean enable) {
		app.setShiftDragZoomEnabled(enable);
	}
	
	/**
	 * @param show
	 * 
	 * wheter show the toolbar in geogebra-web applets or not
	 */
	public void showToolBar(boolean show) {
		if (app.getGuiManager() != null) {
			((GuiManagerW) app.getGuiManager()).showToolBar(show);
		}
	}
	
	/**
	 * @param show
	 * 
	 * wheter show the menubar in geogebra-web applets or not
	 */
	public void showMenuBar(boolean show) {
		if (app.getGuiManager() != null) {
			((GuiManagerW) app.getGuiManager()).showMenuBar(show);
		}
	}
	
	/**
	 * @param show
	 * 
	 * wheter show the algebrainput in geogebra-web applets or not
	 */
	public void showAlgebraInput(boolean show) {
		if (app.getGuiManager() != null) {
			((GuiManagerW) app.getGuiManager()).showAlgebraInput(show);
		}
	}
	
	/**
	 * @param show
	 * 
	 * wheter show the reseticon in geogebra-web applets or not
	 */
	public void showResetIcon(boolean show) {
		app.setShowResetIcon(show);
		app.refreshViews();
	}
	
	/**
	 * @param element Html Element
	 * @param frame GeoGebraFrame subclasses
	 *
	 */
	public static void renderArticleElemntWithFrame(final Element element, GeoGebraFrame frame) {
		final ArticleElement article = ArticleElement.as(element);
		article.clear();
		Date creationDate = new Date();
		element.setId(GeoGebraConstants.GGM_CLASS_NAME+creationDate.getTime());
		final GeoGebraFrame inst = frame;
		inst.ae = article;
		inst.createSplash(article);
		RootPanel.get(article.getId()).add(inst);
	}
}

