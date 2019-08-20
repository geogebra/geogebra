package org.geogebra.web.html5.gui;

import java.util.ArrayList;
import java.util.HashMap;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.laf.GLookAndFeelI;
import org.geogebra.web.html5.js.ResourcesInjector;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.HasAppletProperties;
import org.geogebra.web.html5.util.ArticleElement;
import org.geogebra.web.html5.util.ArticleElementInterface;
import org.geogebra.web.html5.util.Dom;
import org.geogebra.web.html5.util.LoadFilePresenter;
import org.geogebra.web.html5.util.ViewW;
import org.geogebra.web.html5.util.Visibility;
import org.geogebra.web.html5.util.debug.LoggerW;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.SpanElement;
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

	private static SpanElement firstDummy = null;
	private static SpanElement lastDummy = null;
	/** Tab index for graphics */
	public static final int GRAPHICS_VIEW_TABINDEX = 10000;

	private static final int LOGO_WIDTH = 427;

	private static final int LOGO_HEIGHT = 120;

	private static HashMap<String, AppW> articleMap = new HashMap<>();
	/** Article element */
	private ArticleElementInterface articleElement;

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
	 * @param articleElement
	 *            applet parameters
	 */
	public GeoGebraFrameW(GLookAndFeelI laf, ArticleElementInterface articleElement) {
		this(laf, ArticleElement.getDataParamFitToScreen(articleElement.getElement()));
		this.articleElement = articleElement;
	}

	/**
	 * Add a dummy element to the parent
	 *
	 * @param parentElement
	 *            parent
	 */
	protected static void tackleLastDummy(Element parentElement) {
		if (!Browser.needsAccessibilityView()) {
			lastDummy = DOM.createSpan().cast();
			lastDummy.addClassName("geogebraweb-dummy-invisible");
			lastDummy.setTabIndex(GeoGebraFrameW.GRAPHICS_VIEW_TABINDEX);
			parentElement.appendChild(lastDummy);
		}
	}

	/**
	 * @return map article id -&gt; article
	 */
	public static HashMap<String, AppW> getArticleMap() {
		return articleMap;
	}

	/**
	 * @param el
	 *            ArticleElement to be used as dummy parent, if it's the last
	 *            one
	 */
	public static void reCheckForDummies(Element el) {

		if ((firstDummy != null) && (lastDummy != null)) {
			return;
		}

		NodeList<Element> nodes = Dom
				.getElementsByClassName(GeoGebraConstants.GGM_CLASS_NAME);

		if (nodes.getLength() == 0) {
			// it would be better for the article tags to always have
			// GeoGebraConstants.GGM_CLASS_NAME, but in case they do not,
			// then they are probably child elements of class name
			// "applet_container"
			nodes = Dom.getElementsByClassName("applet_scaler");
			Log.debug(nodes.getLength() + " scalers found");
			// so "nodes" is meaning something else here actually
			if (nodes.getLength() > 0) {
				// no need to get the first node with articleElement

				checkForDummiesInScaler(nodes, el);

			}
		}
	}

	private static void checkForDummiesInScaler(NodeList<Element> nodes,
			Element el) {
		// get the last node that really contains an articleElement
		for (int i = nodes.getLength() - 1; i >= 0; i--) {
			Element ell = nodes.getItem(i);
			for (int j = 0; j < ell.getChildCount(); j++) {
				Node elChild = ell.getChild(j);
				if (elChild != null
						&& Element.as(elChild).hasTagName("ARTICLE")) {
					// found!!
					if (elChild == el && lastDummy == null) {
						tackleLastDummy(el);
					}
					return;
				}
			}
		}
	}

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
		splash = new SplashDialog(showLogo, articleElement, this);

		if (splash.isPreviewExists()) {
			splashWidth = width;
			splashHeight = height;
		}

		if (width > 0 && height > 0) {
			setWidth(width + "px"); // 2: border
			setComputedWidth(width);
			setComputedHeight(height);
			setHeight(height + "px"); // 2: border
			// Styleshet not loaded yet, add CSS directly
			splash.getElement().getStyle().setPosition(Position.RELATIVE);
			splash.getElement().getStyle()
			        .setTop((height / 2) - (splashHeight / 2), Unit.PX);
			if (!articleElement.isRTL()) {
				splash.getElement().getStyle()
				        .setLeft((width / 2) - (splashWidth / 2), Unit.PX);
			} else {
				splash.getElement().getStyle()
				        .setRight((width / 2) - (splashWidth / 2), Unit.PX);
			}
			useDataParamBorder();
		}
		addStyleName("jsloaded");
		add(splash);
	}

	private void preProcessFitToSceen() {
		if (articleElement.getDataParamFitToScreen()) {
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
				|| articleElement.getDataParamMarginTop() <= 0;
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
		return articleElement.getDataParamMarginTop() <= 0;
	}

	private static boolean hasSmallWindow() {
		return Window.getClientWidth() < 600 || Window.getClientHeight() < 600;
	}

	private void setHeightWithCompactHeader() {
		articleElement.getElement().getStyle().setProperty("height",
				"calc(100% - " + getSmallScreenHeaderHeight() + "px)");
	}

	/**
	 * @return height of header for small screens
	 */
	protected int getSmallScreenHeaderHeight() {
		return SMALL_SCREEN_HEADER_HEIGHT;
	}

	private void setHeightWithTallHeader() {
		int headerHeight = articleElement.getDataParamMarginTop();
		articleElement.getElement().getStyle().setProperty("height",
				"calc(100% - " + headerHeight + "px)");
	}

	/**
	 * Resize to fill browser
	 */
	public void fitSizeToScreen() {
		if (articleElement.getDataParamFitToScreen()) {
			updateHeaderSize();
			app.getGgbApi().setSize(Window.getClientWidth(), computeHeight());
			app.getAccessibilityManager().focusFirstElement();
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
		int width = articleElement.getDataParamWidth();

		if (width > 0) {
			return width - articleElement.getBorderThickness();
		}

		// do we have fit to screen?
		if (articleElement.getDataParamFitToScreen()) {
			width = RootPanel.getBodyElement().getOffsetWidth();
		}

		return width;
	}

	/**
	 * @return app height
	 */
	public int computeHeight() {
		// do we have data-param-height?
		int height = articleElement.getDataParamHeight() - articleElement.getBorderThickness();

		// do we have fit to screen?
		if (articleElement.getDataParamFitToScreen()) {
			int margin = shouldHaveSmallScreenLayout() ? getSmallScreenHeaderHeight()
					: articleElement.getDataParamMarginTop();
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
		setBorder(articleElement.getElement(), getStyleElement(), dpBorder, px);
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
		// Log.debug("useDataParamBorder - " + articleElement.getClassName());
		String dpBorder = articleElement.getDataParamBorder();
		int thickness = articleElement.getBorderThickness() / 2;
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
		articleElement.getElement().getStyle().setOutlineStyle(OutlineStyle.NONE);
	}

	/**
	 * @param ae
	 *            article element
	 * @param gfE
	 *            app frame element
	 */
	public static void useDataParamBorder(ArticleElement ae, Element gfE) {
		// Log.debug("useDataParamBorder - " + ae.getClassName());
		String dpBorder = ae.getDataParamBorder();
		int thickness = ae.getBorderThickness() / 2;
		if (dpBorder != null) {
			if ("none".equals(dpBorder)) {
				setBorder(ae, gfE, "transparent", thickness);
			} else {
				setBorder(ae, gfE, dpBorder, thickness);
			}
		}
		gfE.removeClassName(APPLET_FOCUSED_CLASSNAME);
		gfE.addClassName(APPLET_UNFOCUSED_CLASSNAME);
	}

	/**
	 * Sets the border around the canvas to be highlighted. At the moment we use
	 * "#9999ff" for this purpose.
	 */
	public void useFocusedBorder() {
		// Log.debug("useFocusedBorder - " + articleElement.getClassName());
		String dpBorder = articleElement.getDataParamBorder();
		getElement().removeClassName(
				APPLET_UNFOCUSED_CLASSNAME);
		getElement()
				.addClassName(APPLET_FOCUSED_CLASSNAME);
		int thickness = articleElement.getBorderThickness() / 2;
		if (dpBorder != null && "none".equals(dpBorder)) {
			setBorder("transparent", thickness);
		}
	}

	/**
	 * @param ae
	 *            article element
	 * @param gfE
	 *            app frame element
	 */
	public static void useFocusedBorder(ArticleElement ae, Element gfE) {
		// Log.debug("useFocusedBorder - " + articleElement.getClassName());
		String dpBorder = ae.getDataParamBorder();
		gfE.removeClassName(APPLET_UNFOCUSED_CLASSNAME);
		gfE.addClassName(APPLET_FOCUSED_CLASSNAME);
		int thickness = ae.getBorderThickness() / 2;
		if (dpBorder != null && "none".equals(dpBorder)) {
			setBorder(ae, gfE, "transparent", thickness);
			return;
		}
	}

	/**
	 * Splash screen callback
	 */
	public void runAsyncAfterSplash() {
		final GeoGebraFrameW inst = this;

		// GWT.runAsync(new RunAsyncCallback() {

		// public void onSuccess() {
		ResourcesInjector
				.injectResources(articleElement);
		ResourcesInjector.loadFont(articleElement.getDataParamFontsCssUrl());
		// More testing is needed how can we use
		// createApplicationSimple effectively
		// if (articleElement.getDataParamGuiOff())
		// inst.app = inst.createApplicationSimple(articleElement, inst);
		// else
		inst.app = inst.createApplication(articleElement, this.laf);
		inst.app.setCustomToolBar();
		// useDataParamBorder(articleElement, inst);
		// inst.add(inst.app.buildApplicationPanel());
		boolean showAppPicker = app.isPerspectivesPopupVisible();
		// inst.app.buildApplicationPanel();
		if (showAppPicker) {
			app.showPerspectivesPopup();
		}
		// need to call setLabels here
		// to print DockPanels' titles
		inst.app.setLabels();
		fitSizeToScreen();
	}

	/**
	 * @param articleElement
	 *            article
	 * @param app
	 *            app
	 */
	public static void handleLoadFile(ArticleElementInterface articleElement,
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
	 * @param lookAndFeel
	 *            look and feel
	 * @return the newly created instance of Application
	 */
	protected abstract AppW createApplication(ArticleElementInterface article,
			GLookAndFeelI lookAndFeel);

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
			setWidth(width - app.getArticleElement().getBorderThickness()
					+ "px");
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
			setHeight(height - app.getArticleElement().getBorderThickness()
					+ "px");
			app.persistWidthAndHeight();
		} else {
			setSizeSimple(getOffsetWidth(), height);
		}
	}

	private void setSizeSimple(int width, int height) {
		int innerHeight = height - app.getArticleElement().getBorderThickness();
		int innerWidth = width - app.getArticleElement().getBorderThickness();
		setHeight(innerHeight + "px");
		setWidth(innerWidth + "px");
		app.setAppletHeight(innerHeight);
		app.setAppletWidth(innerWidth);
		app.getEuclidianViewpanel().setPixelSize(innerWidth, innerHeight);

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
		// setPixelSize(width, height);
		if (app.getGuiManager() != null) {
			app.getGuiManager().resize(width, height);
			setFramePixelSize(width, height);
			app.persistWidthAndHeight();
		} else {
			setSizeSimple(width, height);
		}
	}

	private void setFramePixelSize(int width, int height) {
		setWidth(width - app.getArticleElement().getBorderThickness() + "px");
		setHeight(height - app.getArticleElement().getBorderThickness() + "px");
	}

	/**
	 * After loading a new GGB file, the size should be set to "auto"
	 */
	@Override
	public void resetAutoSize() {
		if (app.getArticleElement().getDataParamWidth() > 0) {
			setFramePixelSize(app.getArticleElement().getDataParamWidth(),
					app.getArticleElement().getDataParamHeight());
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
	 * @param frame
	 *            GeoGebraFrame subclasses
	 * @param onLoadCallback
	 *            load callback
	 *
	 */
	public static void renderArticleElementWithFrame(final Element element,
	        GeoGebraFrameW frame, JavaScriptObject onLoadCallback) {

		final ArticleElement article = ArticleElement.as(element);
		if (Log.getLogger() == null) {
			LoggerW.startLogger(article);
		}
		article.clear();
		article.initID(0);
		frame.onLoadCallback = onLoadCallback;
		frame.createSplash();
		RootPanel root = RootPanel.get(article.getId());
		if (root != null) {
			root.add(frame);
		} else {
			Log.error("Cannot find article with ID " + article.getId());
		}
	}

	/**
	 * callback when renderGGBElement is ready
	 */
	public static native void renderGGBElementReady() /*-{
		if (typeof $wnd.renderGGBElementReady === "function") {
			$wnd.renderGGBElementReady();
		}
	}-*/;

	/**
	 * removes applet from the page
	 */
	@Override
	public void remove() {
		removeFromParent();
		// this does not do anything!
		GeoGebraFrameW.getInstances()
				.remove(
		        GeoGebraFrameW.getInstances().indexOf(this));
		articleElement.getElement().removeFromParent();
		articleElement = null;
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
}
