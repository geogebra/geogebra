package org.geogebra.web.html5.gui;

import java.util.ArrayList;
import java.util.HashMap;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.gui.laf.GLookAndFeelI;
import org.geogebra.web.html5.js.ResourcesInjector;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.HasAppletProperties;
import org.geogebra.web.html5.util.ArticleElement;
import org.geogebra.web.html5.util.Dom;
import org.geogebra.web.html5.util.ViewW;
import org.geogebra.web.html5.util.debug.LoggerW;

import com.google.gwt.core.client.JavaScriptObject;
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
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * The main frame containing every view / menu bar / .... This Panel (Frame is
 * resize able)
 */
public abstract class GeoGebraFrameW extends FlowPanel implements
        HasAppletProperties {

	private static ArrayList<GeoGebraFrameW> instances = new ArrayList<GeoGebraFrameW>();



	/** The application */
	public AppW app;

	/**
	 * Splash Dialog to get it work quickly
	 */
	public SplashDialog splash;

	private static SpanElement firstDummy = null;
	private static SpanElement lastDummy = null;
	public static final int GRAPHICS_VIEW_TABINDEX = 10000;

	private static HashMap<String, AppW> articleMap = new HashMap<String, AppW>();

	/** Creates new GeoGebraFrame */
	public GeoGebraFrameW(GLookAndFeelI laf) {
		super();
		this.laf = laf;
		instances.add(this);
		addStyleName("GeoGebraFrame");
		DOM.sinkEvents(this.getElement(), Event.ONMOUSEDOWN | Event.ONMOUSEMOVE
		        | Event.ONMOUSEUP | Event.ONMOUSEOVER);
	}

	protected static void tackleFirstDummy(Element parentElement) {
		firstDummy = DOM.createSpan().cast();
		firstDummy.addClassName("geogebraweb-dummy-invisible");
		firstDummy.setTabIndex(GeoGebraFrameW.GRAPHICS_VIEW_TABINDEX);
		parentElement.insertFirst(firstDummy);
	}

	protected static void tackleLastDummy(Element parentElement) {
		lastDummy = DOM.createSpan().cast();
		lastDummy.addClassName("geogebraweb-dummy-invisible");
		lastDummy.setTabIndex(GeoGebraFrameW.GRAPHICS_VIEW_TABINDEX);
		parentElement.appendChild(lastDummy);
	}

	/**
	 * @return map article id -&gt; article
	 */
	public static HashMap<String, AppW> getArticleMap() {
		return articleMap;
	}

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
			Element ell;
			nodes = Dom.getElementsByClassName("applet_scaler");
			Log.debug(nodes.getLength() + " scalers found");
			// so "nodes" is meaning something else here actually
			if (nodes.getLength() > 0) {
				// no need to get the first node with articleElement

				// get the last node that really contains an articleElement
				for (int i = nodes.getLength() - 1; i >= 0; i--) {
					ell = nodes.getItem(i);
					for (int j = 0; j < ell.getChildCount(); j++) {
						Node elChild = ell.getChild(j);
						if (elChild != null
								&& Element.as(elChild).hasTagName("ARTICLE")) {
							// found!!
							if (elChild == el) {
								// lastDummy!
								if (lastDummy == null) {
									tackleLastDummy(el);
								}
							}
							return;
						}
					}
				}

			}
		}
	}

	/**
	 * The application loading continues in the splashDialog onLoad handler
	 * 
	 * @param articleElement
	 *            ArticleElement
	 */
	public void createSplash(ArticleElement articleElement) {

		int splashWidth = 427;
		int splashHeight = 120;

		// to not touch the DOM twice when computing widht and height
		preProcessFitToSceen();

		int width = computeWidth();
		int height = computeHeight();

		/*
		 * if (ae.getDataParamShowMenuBar()) { // The menubar has extra height:
		 * height += 31; } if (ae.getDataParamShowToolBar()) { // The toolbar
		 * has extra height: height += 57; }
		 */

		boolean showLogo = ((width >= splashWidth) && (height >= splashHeight));
		splash = new SplashDialog(showLogo, articleElement.getId(), this);

		if (splash.isPreviewExists()) {
			splashWidth = width;
			splashHeight = height;
		}
		int borderWidth = articleElement.getBorderThickness();
		if (width > 0 && height > 0) {
			setWidth((width - borderWidth) + "px"); // 2: border
			setComputedWidth(width);
			setComputedHeight(height);
			setHeight((height - borderWidth) + "px"); // 2: border
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
		}
		addStyleName("jsloaded");
		add(splash);
	}

	private void preProcessFitToSceen() {
		if (ae.getDataParamFitToScreen()) {
			Document.get().getDocumentElement().getStyle()
					.setHeight(100, Unit.PCT);
			RootPanel.getBodyElement().getStyle().setHeight(100, Unit.PCT);
			RootPanel.getBodyElement().getStyle().setOverflow(Overflow.HIDDEN);
			ae.getStyle().setHeight(100, Unit.PCT);
		}

	}




	private int computeHeight() {
		// do we have data-param-height?
		int height = ae.getDataParamHeight();
		if (height > 0) {
			return height;
		}

		// do we have fit to screen?

		if (ae.getDataParamFitToScreen()) {
			height = ae.getOffsetHeight();
		}

		return height;
	}

	private int computeWidth() {
		// do we have data-param-width?
		int width = ae.getDataParamWidth();

		if (width > 0) {
			return width;
		}

		// do we have fit to screen?
		if (ae.getDataParamFitToScreen()) {
			width = RootPanel.getBodyElement().getOffsetWidth();
		}

		return width;
	}

	/** Article element */
	public ArticleElement ae;

	private int computedWidth = 0;
	private int computedHeight = 0;
	private final GLookAndFeelI laf;

	/**
	 * Callback from renderGGBElement to run, if everything is done
	 */
	private JavaScriptObject onLoadCallback = null;

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
	 * Needs running {@link #setComputedWidth(int)} first
	 * 
	 * @return width computed from applet parameters
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

	private static void setBorder(ArticleElement ae, GeoGebraFrameW gf,
	        String dpBorder, int px) {
		setBorder(ae, gf.getStyleElement(), dpBorder, px);
	}

	private static void setBorder(ArticleElement ae, Element gfE,
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
	 * 
	 * @param ae
	 *            article element
	 * @param gf
	 *            frame
	 */
	public static void useDataParamBorder(ArticleElement ae, GeoGebraFrameW gf) {
		// Log.debug("useDataParamBorder - " + ae.getClassName());
		String dpBorder = ae.getDataParamBorder();
		int thickness = ae.getBorderThickness() / 2;
		if (dpBorder != null) {
			if ("none".equals(dpBorder)) {
				setBorder(ae, gf, "transparent", thickness);
			} else {
				setBorder(ae, gf, dpBorder, thickness);
			}
		}
		gf.getElement().removeClassName(
		        GeoGebraConstants.APPLET_FOCUSED_CLASSNAME);
		gf.getElement().addClassName(
		        GeoGebraConstants.APPLET_UNFOCUSED_CLASSNAME);
		ae.getStyle().setOutlineStyle(OutlineStyle.NONE);
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
		gfE.removeClassName(GeoGebraConstants.APPLET_FOCUSED_CLASSNAME);
		gfE.addClassName(GeoGebraConstants.APPLET_UNFOCUSED_CLASSNAME);
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
	public static void useFocusedBorder(ArticleElement ae, GeoGebraFrameW gf) {
		// Log.debug("useFocusedBorder - " + ae.getClassName());
		String dpBorder = ae.getDataParamBorder();
		gf.getElement().removeClassName(
		        GeoGebraConstants.APPLET_UNFOCUSED_CLASSNAME);
		gf.getElement()
		        .addClassName(GeoGebraConstants.APPLET_FOCUSED_CLASSNAME);
		int thickness = ae.getBorderThickness() / 2;
		if (dpBorder != null && "none".equals(dpBorder)) {
			setBorder(ae, gf, "transparent", thickness);
			return;
		}
	}

	/**
	 * @param ae
	 *            article element
	 * @param gfE
	 *            app frame element
	 */
	public static void useFocusedBorder(ArticleElement ae, Element gfE) {
		// Log.debug("useFocusedBorder - " + ae.getClassName());
		String dpBorder = ae.getDataParamBorder();
		gfE.removeClassName(GeoGebraConstants.APPLET_UNFOCUSED_CLASSNAME);
		gfE.addClassName(GeoGebraConstants.APPLET_FOCUSED_CLASSNAME);
		int thickness = ae.getBorderThickness() / 2;
		if (dpBorder != null && "none".equals(dpBorder)) {
			setBorder(ae, gfE, "transparent", thickness);
			return;
		}
	}

	public void runAsyncAfterSplash() {
		final GeoGebraFrameW inst = this;
		final ArticleElement articleElement = this.ae;

		// GWT.runAsync(new RunAsyncCallback() {

		// public void onSuccess() {
		ResourcesInjector
				.injectResources("true".equals(ae.getDataParamPrerelease())
						|| "canary".equals(ae.getDataParamPrerelease()));
		// More testing is needed how can we use
		// createApplicationSimple effectively
		// if (ae.getDataParamGuiOff())
		// inst.app = inst.createApplicationSimple(articleElement, inst);
		// else
		inst.app = inst.createApplication(articleElement, this.laf);

		inst.app.setCustomToolBar();
		// useDataParamBorder(articleElement, inst);
		// inst.add(inst.app.buildApplicationPanel());
		boolean showAppPicker = app.isPerspectivesPopupVisible();
		inst.app.buildApplicationPanel();
		if (showAppPicker) {
			app.showPerspectivesPopup();
		}
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
	        GeoGebraFrameW inst, AppW app) {
		handleLoadFile(articleElement, app);
	}

	private static void handleLoadFile(ArticleElement articleElement, AppW app) {
		ViewW view = new ViewW(articleElement, app);
		ViewW.fileLoader.setView(view);
		ViewW.fileLoader.onPageLoad();
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
	 * @param article
	 *            article element
	 * @param lookAndFeel
	 *            look and feel
	 * @return the newly created instance of Application
	 */
	protected abstract AppW createApplication(ArticleElement article,
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

	public static int getInstanceCount() {
		return instances.size();
	}

	/**
	 * @param width
	 * 
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
			setWidth(width - app.getArticleElement().getBorderThickness()
					+ "px");
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
	@Override
	public void setHeight(int height) {
		if (app.getGuiManager() != null) {
			app.getGuiManager().resize(getOffsetWidth(), height);
			setHeight(height - app.getArticleElement().getBorderThickness()
					+ "px");
			app.persistWidthAndHeight();
		} else {
			setHeight(height - app.getArticleElement().getBorderThickness()
					+ "px");
			app.getEuclidianViewpanel().setPixelSize(getOffsetWidth(), height);

			// maybe onResize is OK too
			app.getEuclidianViewpanel().deferredOnResize();
		}
	}

	/**
	 * sets the geogebra-web applet size (width, height)
	 * 
	 * @param width
	 *            width in pixels
	 * @param height
	 *            height in pixels
	 * 
	 * 
	 */
	@Override
	public void setSize(int width, int height) {
		// setPixelSize(width, height);
		if (app.getGuiManager() != null) {
			app.getGuiManager().resize(width, height);
			setWidth(width - app.getArticleElement().getBorderThickness()
					+ "px");
			setHeight(height - app.getArticleElement().getBorderThickness()
					+ "px");
			app.persistWidthAndHeight();
		}

	}

	/**
	 * After loading a new GGB file, the size should be set to "auto"
	 */
	@Override
	public void resetAutoSize() {
		setWidth("auto");
		setHeight("auto");
	}



	/**
	 * @param show
	 * 
	 *            wheter show the toolbar in geogebra-web applets or not
	 */
	@Override
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
	@Override
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
	@Override
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
		final GeoGebraFrameW inst = frame;
		inst.ae = article;
		inst.onLoadCallback = onLoadCallback;
		inst.createSplash(article);
		RootPanel root = RootPanel.get(article.getId());
		if (root != null) {
			RootPanel.get(article.getId()).add(inst);
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
		this.removeFromParent();
		// this does not do anything!
		GeoGebraFrameW.getInstances()
				.remove(
		        GeoGebraFrameW.getInstances().indexOf(this));
		this.ae.removeFromParent();
		this.ae = null;
		this.app = null;
		ViewW.fileLoader.setView(null);
		if (GeoGebraFrameW.getInstanceCount() == 0) {
			ResourcesInjector.removeResources();
		}
	}

}
