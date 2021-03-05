package org.geogebra.web.html5.gui;

import java.util.ArrayList;

import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.GeoGebraGlobal;
import org.geogebra.web.html5.gui.laf.GLookAndFeelI;
import org.geogebra.web.html5.js.ResourcesInjector;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.HasAppletProperties;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.html5.util.Dom;
import org.geogebra.web.html5.util.GeoGebraElement;
import org.geogebra.web.html5.util.LoadFilePresenter;
import org.geogebra.web.html5.util.StringConsumer;
import org.geogebra.web.html5.util.ViewW;
import org.geogebra.web.html5.util.Visibility;
import org.geogebra.web.html5.util.debug.LoggerW;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.OutlineStyle;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootPanel;

import elemental2.core.Function;

/**
 * The main frame containing every view / menu bar / .... This Panel (Frame is
 * resize able)
 */
public abstract class GeoGebraFrameW extends FlowPanel implements
		HasAppletProperties {
	private static final String APPLET_FOCUSED_CLASSNAME = "applet-focused";
	private static final String APPLET_UNFOCUSED_CLASSNAME = "applet-unfocused";
	private static ArrayList<GeoGebraFrameW> instances = new ArrayList<>();
	private static final int SMALL_SCREEN_HEADER_HEIGHT = 48;
	/** The application */
	protected AppW app;

	/**
	 * Splash Dialog to get it work quickly
	 */
	private SplashDialog splash;

	private static final int LOGO_WIDTH = 427;

	private static final int LOGO_HEIGHT = 120;

	/** GeoGebra element */
	private GeoGebraElement geoGebraElement;
	private AppletParameters appletParameters;

	private int computedWidth = 0;
	private int computedHeight = 0;
	private final GLookAndFeelI laf;
	private Visibility forcedHeaderVisibility = Visibility.NOT_SET;
	private boolean isHeaderVisible;

	/**
	 * Callback from renderGGBElement to run, if everything is done
	 */
	private JavaScriptObject onLoadCallback = null;

	private GeoGebraFrameW(GLookAndFeelI laf, boolean mainTag) {
		super(mainTag ? "main" : DivElement.TAG);
		this.laf = laf;
		instances.add(this);
		addStyleName("GeoGebraFrame");
		DOM.sinkEvents(this.getElement(), Event.ONMOUSEDOWN | Event.ONMOUSEMOVE
				| Event.ONMOUSEUP | Event.ONMOUSEOVER);
		if (mainTag) {
			getElement().setAttribute("role", "main");
		}
	}

	/**
	 * Creates new GeoGebraFrame
	 *
	 * @param laf
	 *            look and feel
	 * @param geoGebraElement
	 *            applet parameters
	 */
	public GeoGebraFrameW(GLookAndFeelI laf, GeoGebraElement geoGebraElement,
			AppletParameters appletParameters) {
		this(laf, appletParameters.getDataParamFitToScreen());
		this.geoGebraElement = geoGebraElement;
		this.appletParameters = appletParameters;

		if (!appletParameters.getDataParamApp()) {
			addFocusHandlers(this.geoGebraElement.getElement());
		}
	}

	private native void addFocusHandlers(Element e) /*-{
		var that = this;
		e.addEventListener('focusin', function() {
			that.@org.geogebra.web.html5.gui.GeoGebraFrameW::useFocusedBorder()();
		});
		e.addEventListener('focusout', function() {
			that.@org.geogebra.web.html5.gui.GeoGebraFrameW::useDataParamBorder()();
		});
	}-*/;

	/**
	 * The application loading continues in the splashDialog onLoad handler
	 */
	public void createSplash() {

		int splashWidth = LOGO_WIDTH;
		int splashHeight = LOGO_HEIGHT;

		// to not touch the DOM twice when computing width and height
		preProcessFitToSceen();

		int width = computeWidth();
		int height = computeHeight();

		boolean showLogo = ((width >= LOGO_WIDTH) && (height >= LOGO_HEIGHT));
		splash = new SplashDialog(showLogo, geoGebraElement, appletParameters, this);

		if (splash.isPreviewExists()) {
			splashWidth = width;
			splashHeight = height;
		}
		if ("evaluator".equals(appletParameters.getDataParamAppName())) {
			if (width > 0) {
				setWidth(width + "px");
				setComputedWidth(width);
			}
			if (height > 0) {
				setHeight(height + "px");
				setComputedHeight(height);
			}
			useDataParamBorder();
		}
		if (width > 0 && height > 0) {
			setWidth(width + "px"); // 2: border
			setComputedWidth(width);
			setComputedHeight(height);
			setHeight(height + "px"); // 2: border
			// Styleshet not loaded yet, add CSS directly
			splash.getElement().getStyle().setPosition(Position.RELATIVE);
			splash.getElement().getStyle()
					.setTop((height - splashHeight) / 2d, Unit.PX);
			if (!geoGebraElement.isRTL()) {
				splash.getElement().getStyle()
					.setLeft((width - splashWidth) / 2d, Unit.PX);
			} else {
				splash.getElement().getStyle()
						.setRight((width - splashWidth) / 2d, Unit.PX);
			}
			useDataParamBorder();
		}
		addStyleName("jsloaded");
		add(splash);
	}

	private void preProcessFitToSceen() {
		if (appletParameters.getDataParamFitToScreen()) {
			Document.get().getDocumentElement().getStyle()
					.setHeight(100, Unit.PCT);
			RootPanel.getBodyElement().getStyle().setHeight(100, Unit.PCT);
			RootPanel.getBodyElement().getStyle().setOverflow(Overflow.HIDDEN);
			updateArticleHeight();
		}
	}

	@Override
	public void updateArticleHeight() {
		if (hasSmallWindowOrCompactHeader()) {
			setHeightWithCompactHeader();
		} else {
			setHeightWithTallHeader();
		}
		if (app != null) {
			app.adjustScreen(false);
		}
	}

	/**
	 * @param visible
	 *            force visibility
	 */
	public void forceHeaderVisibility(Visibility visible) {
		forcedHeaderVisibility = visible;
		updateHeaderVisible();
		fitSizeToScreen();
	}

	/**
	 * @return whether to use small screen design
	 */
	public boolean shouldHaveSmallScreenLayout() {
		switch (forcedHeaderVisibility) {
			case VISIBLE:
				return false;
			case HIDDEN:
				return true;
			case NOT_SET:
				return hasSmallWindowOrCompactHeader();
		}
		return false;
	}

	/**
	 * @return whether the header should be hidden or not
	 */
	public boolean shouldHideHeader() {
		return forcedHeaderVisibility == Visibility.HIDDEN
				|| appletParameters.getDataParamMarginTop() <= 0;
	}

	/**
	 * @return True if the frame is shown in a small window or if it has a compact header.
	 */
	public boolean hasSmallWindowOrCompactHeader() {
		return hasSmallWindow() || isExternalHeaderHidden();
	}

	/**
	 * @return whether header was forced to be closed by param
	 */
	protected boolean isExternalHeaderHidden() {
		return appletParameters.getDataParamMarginTop() <= 0;
	}

	private static boolean hasSmallWindow() {
		return Window.getClientWidth() < 600 || Window.getClientHeight() < 600;
	}

	private void setHeightWithCompactHeader() {
		geoGebraElement.getStyle().setProperty("height",
				"calc(100% - " + getSmallScreenHeaderHeight() + "px)");
	}

	/**
	 * @return height of header for small screens
	 */
	protected int getSmallScreenHeaderHeight() {
		return SMALL_SCREEN_HEADER_HEIGHT;
	}

	private void setHeightWithTallHeader() {
		int headerHeight = appletParameters.getDataParamMarginTop();
		geoGebraElement.getStyle().setProperty("height",
				"calc(100% - " + headerHeight + "px)");
	}

	/**
	 * Resize to fill browser
	 */
	public void fitSizeToScreen() {
		if (appletParameters.getDataParamFitToScreen()) {
			Element focusBefore = Dom.getActiveElement();
			updateHeaderSize();
			app.getGgbApi().setSize(Window.getClientWidth(), computeHeight());
			if (focusBefore != null) {
				focusBefore.focus();
			}
		}
		app.checkScaleContainer();
	}

	/**
	 * Update size of external header if applicable.
	 */
	public void updateHeaderSize() {
		// overriden later
	}

	private void updateHeaderVisible() {
		Element header = Dom.querySelector("GeoGebraHeader");
		if (header != null) {
			boolean visible = forcedHeaderVisibility != Visibility.HIDDEN;
			header.getStyle().setProperty("display", visible ? "" : "none");
			if (isHeaderVisible != visible) {
				isHeaderVisible = visible;
				app.onHeaderVisible();
			}
			updateArticleHeight();
		}
	}

	/**
	 * Called if header visibility is changed.
	 */
	public void onHeaderVisible() {
		// TODO listener (?)
	}

	private int computeWidth() {
		// do we have data-param-width?
		int width = appletParameters.getDataParamWidth();

		// do we have fit to screen?
		if (appletParameters.getDataParamFitToScreen()) {
			width = RootPanel.getBodyElement().getOffsetWidth();
		}

		return width;
	}

	/**
	 * @return app height
	 */
	public int computeHeight() {
		// do we have data-param-height?
		int height = appletParameters.getDataParamHeight();

		// do we have fit to screen?
		if (appletParameters.getDataParamFitToScreen()) {
			int margin;
			if (shouldHaveSmallScreenLayout()) {
				margin = hasSmallWindow() ? getSmallScreenHeaderHeight() : 0;
			}
			else {
				margin = appletParameters.getDataParamMarginTop();
			}
			height = Window.getClientHeight() - margin;
		}

		return Math.max(height, 0);
	}

	@Override
	public JavaScriptObject getOnLoadCallback() {
		return onLoadCallback;
	}

	/**
	 * @param width
	 *            width computed from article parameters
	 */
	public void setComputedWidth(int width) {
		this.computedWidth = width;
		if (this.app != null) {
			this.app.setAppletWidth(width);
		}
	}

	/**
	 * @param height
	 *            height computed from article parameters
	 */
	public void setComputedHeight(int height) {
		this.computedHeight = height;
		if (this.app != null) {
			this.app.setAppletHeight(height);
		}
	}

	/**
	 * Needs running {@link #setComputedWidth(int)} first let parameters
	 * 
	 * @return computed width
	 */
	public int getComputedWidth() {
		return computedWidth;
	}

	/**
	 * Needs running {@link #setComputedHeight(int)} first
	 *
	 * @return height computed from applet parameters
	 */
	public int getComputedHeight() {
		return computedHeight;
	}

	private void setBorder(String dpBorder, int px) {
		setBorder(geoGebraElement, getStyleElement(), dpBorder, px);
	}

	private static void setBorder(Element ae, Element gfE,
			String dpBorder, int px) {
		ae.getStyle().setBorderWidth(0, Style.Unit.PX);
		ae.getStyle().setBorderStyle(Style.BorderStyle.SOLID);
		ae.getStyle().setBorderColor(dpBorder);
		gfE.getStyle().setBorderWidth(px, Style.Unit.PX);
		gfE.getStyle().setBorderStyle(Style.BorderStyle.SOLID);
		gfE.getStyle().setBorderColor(dpBorder);
		ae.getStyle().setOutlineStyle(OutlineStyle.NONE);
	}

	/**
	 * Sets the border around the canvas to the data-param-bordercolor property
	 * or leaves it invisible if "none" was set.
	 */
	public void useDataParamBorder() {
		String dpBorder = appletParameters.getDataParamBorder();
		int thickness = appletParameters.getBorderThickness() / 2;
		if (dpBorder != null) {
			if ("none".equals(dpBorder)) {
				setBorder("transparent", thickness);
			} else {
				setBorder(dpBorder, thickness);
			}
		}
		getElement().removeClassName(
				APPLET_FOCUSED_CLASSNAME);
		getElement().addClassName(
				APPLET_UNFOCUSED_CLASSNAME);
		geoGebraElement.getStyle().setOutlineStyle(OutlineStyle.NONE);
	}

	/**
	 * Sets the border around the canvas to be highlighted. At the moment we use
	 * "#9999ff" for this purpose.
	 */
	public void useFocusedBorder() {
		String dpBorder = appletParameters.getDataParamBorder();
		getElement().removeClassName(
				APPLET_UNFOCUSED_CLASSNAME);
		getElement()
				.addClassName(APPLET_FOCUSED_CLASSNAME);
		int thickness = appletParameters.getBorderThickness() / 2;
		if ("none".equals(dpBorder)) {
			setBorder("transparent", thickness);
		}
		getApp().getGlobalKeyDispatcher().setEscPressed(false);
	}

	/**
	 * Splash screen callback
	 */
	public void runAsyncAfterSplash() {
		ResourcesInjector resourcesInjector = getResourcesInjector(appletParameters);
		resourcesInjector.injectResources(appletParameters);
		resourcesInjector.loadWebFont(appletParameters.getDataParamFontsCssUrl());

		app = createApplication(geoGebraElement, appletParameters, this.laf);
		app.setCustomToolBar();

		Event.sinkEvents(geoGebraElement.getElement(), Event.ONKEYPRESS | Event.ONKEYDOWN);
		Event.setEventListener(geoGebraElement.getElement(),
				app.getGlobalKeyDispatcher().getGlobalShortcutHandler());

		if (app.isPerspectivesPopupVisible()) {
			app.showPerspectivesPopupIfNeeded();
		}
		// need to call setLabels here
		// to print DockPanels' titles
		app.setLabels();
		fitSizeToScreen();
	}

	protected ResourcesInjector getResourcesInjector(AppletParameters appletParameters) {
		return new ResourcesInjector();
	}

	/**
	 * @param articleElement
	 *            article
	 * @param app
	 *            app
	 */
	public static void handleLoadFile(AppletParameters articleElement,
			AppW app) {
		ViewW view = app.getViewW();
		new LoadFilePresenter().onPageLoad(articleElement, app, view);
	}

	/**
	 * @return the application
	 */
	public AppW getApp() {
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
		initAppDependentFields();
	}

	private void initAppDependentFields() {
		isHeaderVisible = !shouldHaveSmallScreenLayout();
	}

	/**
	 * @param article
	 *            article element
	 * @param parameters applet parameters
	 * @param lookAndFeel
	 *            look and feel
	 * @return the newly created instance of Application
	 */
	protected abstract AppW createApplication(GeoGebraElement article,
			AppletParameters parameters, GLookAndFeelI lookAndFeel);

	/**
	 * @return list of instances of GeogebraFrame
	 */
	public static ArrayList<GeoGebraFrameW> getInstances() {
		return instances;
	}

	@Override
	public void onBrowserEvent(Event event) {
		// do nothing
	}

	/**
	 * @return number of existing frames
	 */
	public static int getInstanceCount() {
		return instances.size();
	}

	/**
	 * @param width
	 *            sets the geogebra-web applet widht
	 */
	@Override
	public void setWidth(int width) {
		if (app.getGuiManager() != null) {
			app.getGuiManager().resize(width, getOffsetHeight());
			setWidth(width + "px");
			app.persistWidthAndHeight();
		} else {
			setSizeSimple(width, getOffsetHeight());
		}
	}

	/**
	 * @param height
	 *            sets the geogebra-web applet height
	 */
	@Override
	public void setHeight(int height) {
		if (app.getGuiManager() != null) {
			app.getGuiManager().resize(getOffsetWidth(), height);
			setHeight(height + "px");
			app.persistWidthAndHeight();
		} else {
			setSizeSimple(getOffsetWidth(), height);
		}
	}

	private void setSizeSimple(int width, int height) {
		setWidth(width + "px");
		setHeight(height + "px");
		app.setAppletWidth(width);
		app.setAppletHeight(height);
		app.getEuclidianViewpanel().setPixelSize(width, height);

		// maybe onResize is OK too
		app.getEuclidianViewpanel().deferredOnResize();
	}

	/**
	 * sets the geogebra-web applet size (width, height)
	 *
	 * @param width
	 *            width in pixels
	 * @param height
	 *            height in pixels
	 */
	@Override
	public void setSize(int width, int height) {
		if (app.getGuiManager() != null) {
			app.getGuiManager().resize(width, height);
			setFramePixelSize(width, height);
			app.persistWidthAndHeight();
		} else {
			setSizeSimple(width, height);
		}
	}

	protected void setFramePixelSize(int width, int height) {
		setWidth(width + "px");
		setHeight(height + "px");
	}

	/**
	 * After loading a new GGB file, the size should be set to "auto"
	 */
	@Override
	public void resetAutoSize() {
		if (app.getAppletParameters().getDataParamWidth() > 0) {
			setFramePixelSize(app.getAppletParameters().getDataParamWidth(),
					app.getAppletParameters().getDataParamHeight());
		}
	}

	/**
	 * @param show
	 *
	 *            wheter show the reseticon in geogebra-web applets or not
	 */
	@Override
	public void showResetIcon(boolean show) {
		app.setShowResetIcon(show);
		app.refreshViews();
	}

	/**
	 * @param element
	 *            Html Element
	 * @param onLoadCallback
	 *            load callback
	 */
	public void renderArticleElementWithFrame(GeoGebraElement element,
			JavaScriptObject onLoadCallback) {
		element.clear();
		element.initID(0);
		if (Log.getLogger() == null) {
			LoggerW.startLogger(appletParameters);
		}
		this.onLoadCallback = onLoadCallback;
		createSplash();
		RootPanel root = RootPanel.get(element.getId());
		if (root != null) {
			root.add(this);
		} else {
			Log.error("Cannot find article with ID " + element.getId());
		}
	}

	/**
	 * callback when renderGGBElement is ready
	 */
	public static void renderGGBElementReady() {
		Function renderGGBElementReady = GeoGebraGlobal.getRenderGGBElementReady();
		if (renderGGBElementReady != null) {
			renderGGBElementReady.call();
		}
	}

	/**
	 * removes applet from the page
	 */
	@Override
	public void remove() {
		removeFromParent();
		// this does not do anything!
		GeoGebraFrameW.getInstances().remove(this);
		geoGebraElement.getElement().removeFromParent();
		geoGebraElement = null;
		app = null;
		if (GeoGebraFrameW.getInstanceCount() == 0) {
			ResourcesInjector.removeResources();
		}
	}

	/**
	 * Hide
	 */
	public void hideSplash() {
		if (splash != null) {
			splash.canNowHide();
		}
	}

	public void getScreenshotBase64(StringConsumer callback) {
		callback.consume(app.getEuclidianView1().getCanvasBase64WithTypeString());
	}
}
