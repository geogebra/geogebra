package org.geogebra.web.html5.gui;

import java.util.ArrayList;
import java.util.Date;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.gui.laf.GLookAndFeelI;
import org.geogebra.web.html5.js.ResourcesInjector;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.HasAppletProperties;
import org.geogebra.web.html5.util.ArticleElement;
import org.geogebra.web.html5.util.LoadFilePresenter;
import org.geogebra.web.html5.util.View;
import org.geogebra.web.html5.util.debug.GeoGebraLogger;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HeaderPanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * The main frame containing every view / menu bar / .... This Panel (Frame is
 * resize able)
 */
public abstract class GeoGebraFrame extends FlowPanel implements
        HasAppletProperties {

	public static final int BORDER_WIDTH = 2;
	public static final int BORDER_HEIGHT = 2;
	private static ArrayList<GeoGebraFrame> instances = new ArrayList<GeoGebraFrame>();
	private static GeoGebraFrame activeInstance;

	/** Loads file into active GeoGebraFrame */
	public static LoadFilePresenter fileLoader = new LoadFilePresenter();

	/** The application */
	public AppW app;

	private boolean resize = false;
	private boolean move = false;
	private static int counter = 1;
	/**
	 * Splash Dialog to get it work quickly
	 */
	public SplashDialog splash;
	private int frameID;

	/** Creates new GeoGebraFrame */
	public GeoGebraFrame(GLookAndFeelI laf) {
		super();
		this.frameID = counter++;
		this.laf = laf;
		instances.add(this);
		activeInstance = this;
		addStyleName("GeoGebraFrame");
		DOM.sinkEvents(this.getElement(), Event.ONMOUSEDOWN | Event.ONMOUSEMOVE
		        | Event.ONMOUSEUP | Event.ONMOUSEOVER);
	}

	/**
	 * @param ae
	 *            ArticleElement
	 *
	 *            In the splashDialog onLoad handler will the application
	 *            loading continue
	 */
	public void createSplash(ArticleElement ae) {

		int splashWidth = 427;
		int splashHeight = 120;

		// to not touch the DOM twice when computing widht and height
		computeMinDim();
		computeMaxDim();
		preProcessFitToSceen();

		int width = computeWidth();
		int height = computeHeight();

		/*
		 * if (ae.getDataParamShowMenuBar()) { // The menubar has extra height:
		 * height += 31; } if (ae.getDataParamShowToolBar()) { // The toolbar
		 * has extra height: height += 57; }
		 */

		boolean showLogo = ((width >= splashWidth) && (height >= splashHeight));
		splash = new SplashDialog(showLogo, ae.getId(), this);

		if (splash.isPreviewExists()) {
			splashWidth = width;
			splashHeight = height;
		}

		if (width > 0 && height > 0) {
			setWidth((width - BORDER_WIDTH) + "px"); // 2: border
			setComputedWidth(width);
			setComputedHeight(height);
			setHeight((height - BORDER_HEIGHT) + "px"); // 2: border
			// Styleshet not loaded yet, add CSS directly
			splash.getElement().getStyle().setPosition(Position.RELATIVE);
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

	private void preProcessFitToSceen() {
		if (ae.getDataParamFitToScreen()) {
			Document.get().getDocumentElement().getStyle()
			        .setHeight(99, Unit.PCT);
			RootPanel.getBodyElement().getStyle().setHeight(99, Unit.PCT);
			RootPanel.getBodyElement().getStyle().setOverflow(Overflow.HIDDEN);
			ae.getStyle().setHeight(99, Unit.PCT);
		}

	}

	private int[] minDim;
	private int[] maxDim;

	private void computeMinDim() {
		minDim = ae.getDataParamMinDimensions();
	}

	private void computeMaxDim() {
		maxDim = ae.getDataParamMaxDimensions();
	}

	private int computeHeight() {
		// do we have data-param-height?
		int height = ae.getDataParamHeight();
		int minHeight = ae.getDataParamMinHeight();
		int maxHeight = ae.getDataParamMaxHeight();
		if (height > 0) {
			return height;
		}

		// do we have fit to screen?

		if (ae.getDataParamFitToScreen()) {
			height = ae.getOffsetHeight() - ae.getDataParamHeightCrop();
		}
		if (minHeight > 0 && height < minHeight) {
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
		// do we have data-param-width?
		int width = ae.getDataParamWidth();
		int minWidth = ae.getDataParamMinWidth();
		int maxWidth = ae.getDataParamMaxWidth();
		if (width > 0) {
			return width;
		}

		// do we have fit to screen?
		if (ae.getDataParamFitToScreen()) {
			width = RootPanel.getBodyElement().getOffsetWidth()
			        - ae.getDataParamWidthCrop();
		}
		if (minWidth > 0 && width < minWidth) {
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

	public ArticleElement ae;

	protected int computedWidth = 0;
	protected int computedHeight = 0;
	private final GLookAndFeelI laf;

	/**
	 * Callback from renderGGBElement to run, if everything is done
	 */
	private JavaScriptObject onLoadCallback = null;

	public JavaScriptObject getOnLoadCallback() {
		return onLoadCallback;
	}

	public void setComputedWidth(int width) {
		this.computedWidth = width;
		if (this.app != null) {
			this.app.setAppletWidth(width);
		}
	}

	public void setComputedHeight(int height) {
		this.computedHeight = height;
		if (this.app != null) {
			this.app.setAppletHeight(height);
		}
	}

	public int getComputedWidth() {
		return computedWidth;
	}

	public int getComputedHeight() {
		return computedHeight;
	}

	private static void setBorder(ArticleElement ae, GeoGebraFrame gf,
	        String dpBorder, int px) {
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
	 * 
	 * @param ae
	 *            article element
	 * @param gf
	 *            frame
	 */
	public static void useDataParamBorder(ArticleElement ae, GeoGebraFrame gf) {
		String dpBorder = ae.getDataParamBorder();
		if (dpBorder != null) {
			if (dpBorder.equals("none")) {
				setBorder(ae, gf, "transparent", 1);
			} else {
				setBorder(ae, gf, dpBorder, 1);
			}
		}
		gf.getElement().removeClassName(
		        GeoGebraConstants.APPLET_FOCUSED_CLASSNAME);
		gf.getElement().addClassName(
		        GeoGebraConstants.APPLET_UNFOCUSED_CLASSNAME);
	}

	/**
	 * Sets the border around the canvas to be highlighted. At the moment we use
	 * "#9999ff" for this purpose.
	 * 
	 * @param ae
	 *            article element
	 * @param gf
	 *            frame
	 */
	public static void useFocusedBorder(ArticleElement ae, GeoGebraFrame gf) {
		String dpBorder = ae.getDataParamBorder();
		gf.getElement().removeClassName(
		        GeoGebraConstants.APPLET_UNFOCUSED_CLASSNAME);
		gf.getElement()
		        .addClassName(GeoGebraConstants.APPLET_FOCUSED_CLASSNAME);
		if (dpBorder != null && dpBorder.equals("none")) {
			setBorder(ae, gf, "transparent", 1);
			return;
		}
	}

	public void runAsyncAfterSplash() {
		final GeoGebraFrame inst = this;
		final ArticleElement articleElement = this.ae;

		// GWT.runAsync(new RunAsyncCallback() {

		// public void onSuccess() {
		ResourcesInjector.injectResources();

		// More testing is needed how can we use
		// createApplicationSimple effectively
		// if (ae.getDataParamGuiOff())
		// inst.app = inst.createApplicationSimple(articleElement, inst);
		// else
		inst.app = inst.createApplication(articleElement, this.laf);

		inst.app.setCustomToolBar();
		// useDataParamBorder(articleElement, inst);
		// inst.add(inst.app.buildApplicationPanel());
		inst.app.buildApplicationPanel();
		// need to call setLabels here
		// to print DockPanels' titles
		inst.app.setLabels();
		// }

		// public void onFailure(Throwable reason) {
		// App.debug("Async load failed");
		// }
		// });
	}

	public static void finishAsyncLoading(ArticleElement articleElement,
	        GeoGebraFrame inst, AppW app) {
		handleLoadFile(articleElement, app);
	}

	private static void handleLoadFile(ArticleElement articleElement, AppW app) {
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
	 * 
	 * @param app
	 *            the application
	 */
	public void setApplication(AppW app) {
		this.app = app;
	}

	/**
	 * @param useFullGui
	 *            if false only one euclidianView will be available (without
	 *            menus / ...)
	 * @return the newly created instance of Application
	 */
	protected abstract AppW createApplication(ArticleElement ae,
	        GLookAndFeelI laf);

	/**
	 * @return list of instances of GeogebraFrame
	 */
	public static ArrayList<GeoGebraFrame> getInstances() {
		return instances;
	}

	@Override
	public void onBrowserEvent(Event event) {
		/*
		 * if (app == null || !app.getUseFullGui()){ return; } final int
		 * eventType = DOM.eventGetType(event);
		 * 
		 * switch (eventType) { case Event.ONMOUSEOVER: if
		 * (isCursorAtResizePosition(event)) {
		 * DOM.setStyleAttribute(getElement(), "cursor", "se-resize"); } else {
		 * DOM.setStyleAttribute(getElement(), "cursor", "default"); } break;
		 * case Event.ONMOUSEDOWN: if (isCursorAtResizePosition(event)) { if
		 * (resize == false) { resize = true; DOM.setCapture(getElement()); } }
		 * break; case Event.ONMOUSEMOVE: if (resize == true) { int width =
		 * DOM.eventGetClientX(event) + Window.getScrollLeft(); int height =
		 * DOM.eventGetClientY(event) + Window.getScrollTop(); int initialY =
		 * getAbsoluteTop(); int initialX = getAbsoluteLeft(); width -=
		 * initialX; height -= initialY;
		 * 
		 * setPixelSize(width, height); if (app.getGuiManager() != null) {
		 * app.getGuiManager().resize(width, height); } } else if (move == true)
		 * { // move is always false; if it will be true, the following thing
		 * should be tested: // note that eventGetClientX needs
		 * Window.getScrollLeft added RootPanel.get().setWidgetPosition(this,
		 * DOM.eventGetClientX(event) + Window.getScrollLeft(),
		 * DOM.eventGetClientY(event) + Window.getScrollTop()); } break; case
		 * Event.ONMOUSEUP: if (move == true) { move = false;
		 * DOM.releaseCapture(getElement()); } if (resize == true) { resize =
		 * false; DOM.releaseCapture(getElement()); } break; }
		 */
	}

	/**
	 * Determines if the mouse cursor is at the bottom right position of the
	 * Frame
	 * 
	 * @param event
	 *            the cursor event
	 * @return true if cursor is within a 10x10 frame on the bottom right
	 *         position
	 */
	protected boolean isCursorAtResizePosition(Event event) {
		int cursorY = DOM.eventGetClientY(event) + Window.getScrollTop();
		int initialY = getAbsoluteTop();
		int height = getOffsetHeight();

		int cursorX = DOM.eventGetClientX(event) + Window.getScrollLeft();
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
	 *            sets the geogebra-web applet widht
	 */
	public void setWidth(int width) {
		if (app.getGuiManager() != null) {
			app.getGuiManager().resize(width, getOffsetHeight());
		} else {
			setWidth(width - BORDER_WIDTH + "px");
			app.getEuclidianViewpanel().setPixelSize(width, getOffsetHeight());

			// maybe onResize is OK too
			app.getEuclidianViewpanel().deferredOnResize();
		}
	}

	/**
	 * @param height
	 * 
	 *            sets the geogebra-web applet height
	 */
	public void setHeight(int height) {
		if (app.getGuiManager() != null) {
			app.getGuiManager().resize(getOffsetWidth(), height);
		} else {
			setHeight(height - BORDER_HEIGHT + "px");
			app.getEuclidianViewpanel().setPixelSize(getOffsetWidth(), height);

			// maybe onResize is OK too
			app.getEuclidianViewpanel().deferredOnResize();
		}
	}

	/**
	 * @param width
	 * @param height
	 * 
	 *            sets the geogebra-web applet size (width, height)
	 */
	public void setSize(int width, int height) {
		setPixelSize(width, height);
		if (app.getGuiManager() != null) {
			app.getGuiManager().resize(width, height);
		}

	}

	/**
	 * After loading a new GGB file, the size should be set to "auto"
	 */
	public void resetAutoSize() {
		setWidth("auto");
		setHeight("auto");
	}

	/**
	 * @param enable
	 *            wheter geogebra-web applet rightclick enabled or not
	 */
	public void enableRightClick(boolean enable) {
		app.setRightClickEnabled(enable);
	}

	/**
	 * @param enable
	 * 
	 *            wheter labels draggable in geogebra-web applets or not
	 */
	public void enableLabelDrags(boolean enable) {
		app.setLabelDragsEnabled(enable);
	}

	/**
	 * @param enable
	 * 
	 *            wheter shift - drag - zoom enabled in geogebra-web applets or
	 *            not
	 */
	public void enableShiftDragZoom(boolean enable) {
		app.setShiftDragZoomEnabled(enable);
	}

	/**
	 * @param show
	 * 
	 *            wheter show the toolbar in geogebra-web applets or not
	 */
	public void showToolBar(boolean show) {
		if (app.getGuiManager() != null) {
			app.getGuiManager().showToolBar(show);
		}
	}

	/**
	 * @param show
	 * 
	 *            wheter show the menubar in geogebra-web applets or not
	 */
	public void showMenuBar(boolean show) {
		if (app.getGuiManager() != null) {
			app.getGuiManager().showMenuBar(show);
		}
	}

	/**
	 * @param show
	 * 
	 *            wheter show the algebrainput in geogebra-web applets or not
	 */
	public void showAlgebraInput(boolean show) {
		if (app.getGuiManager() != null) {
			app.getGuiManager().showAlgebraInput(show);
		}
	}

	/**
	 * @param show
	 * 
	 *            wheter show the reseticon in geogebra-web applets or not
	 */
	public void showResetIcon(boolean show) {
		app.setShowResetIcon(show);
		app.refreshViews();
	}

	/**
	 * @param element
	 *            Html Element
	 * @param frame
	 *            GeoGebraFrame subclasses
	 *
	 */
	public static void renderArticleElementWithFrame(final Element element,
	        GeoGebraFrame frame, JavaScriptObject onLoadCallback) {
		final ArticleElement article = ArticleElement.as(element);
		if(Log.logger == null){
			GeoGebraLogger.startLogger(article);
		}
		article.clear();
		Date creationDate = new Date();
		element.setId(GeoGebraConstants.GGM_CLASS_NAME + creationDate.getTime());
		final GeoGebraFrame inst = frame;
		inst.ae = article;
		inst.onLoadCallback = onLoadCallback;
		inst.createSplash(article);
		RootPanel.get(article.getId()).add(inst);
	}

	/**
	 * callback when renderGGBElement is ready
	 */
	public static native void renderGGBElementReady() /*-{
		if (typeof $wnd.renderGGBElementReady === "function") {
			$wnd.renderGGBElementReady();
		}
	}-*/;

	public Object getGlassPane() {
		return null;
	}

	public void attachGlass() {
	}

	/**
	 * removes applet from the page
	 */
	public void remove() {
		this.removeFromParent();
		// this does not do anything!
		GeoGebraFrame remove = GeoGebraFrame.getInstances().remove(
		        GeoGebraFrame.getInstances().indexOf(this));
		this.ae.removeFromParent();
		this.ae = null;
		this.app = null;
		fileLoader.setView(null);
		remove = null;
		if (GeoGebraFrame.getInstanceCount() == 0) {
			ResourcesInjector.removeResources();
		}
	}

	public abstract void showBrowser(HeaderPanel bg);

	public int getFrameID() {
		return frameID;
	}
}
