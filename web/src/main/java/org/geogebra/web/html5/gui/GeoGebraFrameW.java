package org.geogebra.web.html5.gui;

import java.util.ArrayList;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.gui.laf.GLookAndFeelI;
import org.geogebra.web.html5.js.ResourcesInjector;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.HasAppletProperties;
import org.geogebra.web.html5.util.ArticleElement;
import org.geogebra.web.html5.util.Dom;
import org.geogebra.web.html5.util.LoadFilePresenter;
import org.geogebra.web.html5.util.ViewW;
import org.geogebra.web.html5.util.debug.LoggerW;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * The main frame containing every view / menu bar / .... This Panel (Frame is
 * resize able)
 */
public abstract class GeoGebraFrameW extends FlowPanel implements
        HasAppletProperties {

	public static final int BORDER_WIDTH = 2;
	public static final int BORDER_HEIGHT = 2;
	private static ArrayList<GeoGebraFrameW> instances = new ArrayList<GeoGebraFrameW>();
	private static GeoGebraFrameW activeInstance;

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

	public static SpanElement firstDummy = null;
	public static SpanElement lastDummy = null;
	public static ArrayList<SpanElement> dummies = new ArrayList<SpanElement>();
	public static ArrayList<FocusPanel> dummies2 = new ArrayList<FocusPanel>();
	public static final int GRAPHICS_VIEW_TABINDEX = 10000;

	/** Creates new GeoGebraFrame */
	public GeoGebraFrameW(GLookAndFeelI laf) {
		super();
		this.frameID = counter++;
		this.laf = laf;
		instances.add(this);
		activeInstance = this;
		addStyleName("GeoGebraFrame");
		DOM.sinkEvents(this.getElement(), Event.ONMOUSEDOWN | Event.ONMOUSEMOVE
		        | Event.ONMOUSEUP | Event.ONMOUSEOVER);
	}

	protected static native void programFocusEvent(Element firstd, Element lastd) /*-{
		// this might be needed in case of tabbing by TAB key (more applets)
		firstd.onfocus = function(evnt) {
			lastd.focus();
		};
	}-*/;

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

	protected static void addDummies(Element el, int i) {
		SpanElement item = DOM.createSpan().cast();
		item.addClassName("geogebraweb-dummy-invisible2");
		item.addClassName("temp_" + i);
		item.setTabIndex(GeoGebraFrameW.GRAPHICS_VIEW_TABINDEX);
		addFocusEventForDummy(item);
		dummies.add(item);
		// parentElement.appendChild(item);
		// siblingElement.getParentElement().appendChild(item);
		el.getParentElement().insertAfter(item, el);

		FocusPanel fp = new FocusPanel();
		fp.addStyleName("tmp_" + i);
		// FocusPanel fp = new SimplePanel();
		fp.addFocusHandler(new FocusHandler() {

			public void onFocus(FocusEvent event) {
				Log.debug("onFooooooooooooooocus");
			}


		});

		dummies2.add(fp);
		addNativeHandlersForDummy2(fp.getElement(), i);
		el.getParentElement().insertBefore(fp.getElement(), el);

	}

	protected static void addLastDummy2(Element el) {
		Log.debug("addLastDummy!");
		SimplePanel lastDummy2 = new SimplePanel();
		lastDummy2.addStyleName("geogebraweb_lastdummy");
		lastDummy2.getElement().setTabIndex(0);
		el.getParentElement().insertAfter(lastDummy2.getElement(), el);
	}

	private static native void addNativeHandlersForDummy2(Element dummy, int i)/*-{
		dummy.onfocus = function(event) {
			$wnd.console.log("dummy2 focus - " + i);
			$wnd.console.log(dummy);
			//@org.geogebra.web.html5.gui.GeoGebraFrameW::fireTabOnDummyElement(I)(i);

			var $ = $wnd.$ggbQuery || $wnd.jQuery;
			$(dummy).next().focus();
		}

		dummy.onblur = function(event) {
			$wnd.console.log("dummy2 blur - " + i);
		}

		dummy.onkeydown = function(event) {
			$wnd.console.log("keydown on dummy2: " + event.keyCode);
		}
	}-*/;

	private static void fireTabOnDummyElement(int i) {
		Log.debug("fire tab on dummy: " + i);
		Log.debug("this dummy: " + dummies2.get(i).getStyleName());
		DomEvent.fireNativeEvent(
				Document.get()
						.createKeyDownEvent(false, false, false, false,
						9),
				dummies2.get(i));
	}

	protected static native void addFocusEventForDummy(Element dummy) /*-{

		dummy.onkeydown = function(event) {
			$wnd.console.log("dummy onkeydown - keyCode: " + event.keyCode);
			//$wnd.console.log(document.activeElement);
			//			if (evnt.keyCode == 13) {
			//				evnt.keyCode = 9;
			//				this.dispatchEvent(evnt);
			//			}
			//			
			if (event.keyCode == 13) {
				var keyEvent = document.createEvent("KeyboardEvent");
				keyEvent.initKeyboardEvent("onkeydown", true, true, window, 9,
						event.location, "", event.repeat, event.locale);
				event.currentTarget.dispatchEvent(keyEvent);
			}
		}

		// this might be needed in case of tabbing by TAB key (more applets)
		dummy.onfocus = function(evnt) {
			$wnd.console.log("dummy onfocus");
			//			this.fire(document, 'keypress', {
			//				keyCode : 9
			//			});

			//			var keyboardEvent = document.createEvent("KeyboardEvent");
			//			var initMethod = typeof keyboardEvent.initKeyboardEvent !== 'undefined' ? "initKeyboardEvent"
			//					: "initKeyEvent";
			//
			//			keyboardEvent[initMethod]("keydown", // event type : keydown, keyup, keypress
			//			true, // bubbles
			//			true, // cancelable
			//			window, // viewArg: should be window
			//			false, // ctrlKeyArg
			//			false, // altKeyArg
			//			false, // shiftKeyArg
			//			false, // metaKeyArg
			//			9, // keyCodeArg : unsigned long the virtual key code, else 0
			//			0 // charCodeArgs : unsigned long the Unicode character associated with the depressed key, else 0
			//			);
			//			dummy.dispatchEvent(keyboardEvent);

			//			var TAB_KEY = 9;
			//			var keyboardEvent = document.createEvent("KeyboardEvent");
			//			var initMethod = typeof keyboardEvent.initKeyboardEvent !== 'undefined' ? "initKeyboardEvent"
			//					: "initKeyEvent";
			//			keyboardEvent[initMethod]("keydown", true, true, window, 0, 0, 0,
			//					0, 9, 0);
			//
			//			//keyboardEvent.keyCode = 9;
			//
			//			$wnd.console.log("keyCode in the new event: "
			//					+ keyboardEvent.keyCode);
			//
			//			//(typeArg, canBubbleArg, cancelableArg,
			//			//                           viewArg, charArg, keyArg,
			//			//                           locationArg, modifiersListArg, repeat)
			//
			//			//keyboardEvent[initMethod]("keydown", true, true, $wnd, 0, 9);
			//			//			keyboardEvent[initMethod]("keydown", true, true,
			//			//					document.defaultView, 9, 0, "", 0);
			//
			//			var hasfeature = document.implementation.hasFeature(
			//					"KeyboardEvents", "3.0");
			//			$wnd.console.log("hasfeature: " + hasfeature);

			//			keyboardEvent[initMethod]("keydown", true, true,
			//					document.defaultView, "a", 0, "Shift", 0);

			//keyboardEvent.which = 9;

			//			keyboardEvent.charCode = 0;

			//			var temp = this.dispatchEvent(keyboardEvent);
			//			$wnd.console.log("dispatched: " + temp);

			//			var jQueryObject = $wnd.$ggbQuery || $wnd.jQuery;
			//			jQueryObject.event.trigger({
			//				type : 'keydown',
			//				which : 9,
			//			});

			//			this.on("keydown", 9);

			//			var $ = $wnd.$ggbQuery || $wnd.jQuery;
			//			$wnd.console.log($(dummy).next("article"));
			//			$(dummy).next("article").focus();
			//			$wnd.console.log(document.activeElement);

			//$(dummy).on("keydown", 9);

			//$(dummy).blur();

			//			$(dummy).fire(document, 'keydown', {
			//				keyCode : 9
			//			});

		};

		dummy.onblur = function(evnt) {
			$wnd.console.log("dummy onblur");
		}
	}-*/;

	public static void reCheckForDummies(Element el) {

		if ((firstDummy != null) && (lastDummy != null)) {
			return;
		}

		NodeList<Element> nodes = Dom
				.getElementsByClassName(GeoGebraConstants.GGM_CLASS_NAME);

		if (nodes.getLength() > 0) {

			if (dummies.size() == 0) {
				Log.debug("dummies size 0");
				for (int i = 0; i < nodes.getLength(); i++) {
					addDummies(nodes.getItem(i), i);
				}
				addLastDummy2(nodes.getItem(nodes.getLength() - 1));
			}

			// if (nodes.getItem(0) == el) {
			// // firstDummy!
			// // now we can create dummy elements before & after each applet
			// // with tabindex 10000, for ticket #5158
			// if (firstDummy == null) {
			// tackleFirstDummy(el);
			//
			// if (lastDummy != null) {
			// programFocusEvent(firstDummy, lastDummy);
			// }
			// }
			// } else if (nodes.getItem(nodes.getLength() - 1) == el) {
			// // lastDummy!
			// if (lastDummy == null) {
			// tackleLastDummy(el);
			//
			// if (firstDummy != null) {
			// programFocusEvent(firstDummy, lastDummy);
			// }
			// }
			// }
		} else {
			// it would be better for the article tags to always have
			// GeoGebraConstants.GGM_CLASS_NAME, but in case they do not,
			// then they are probably child elements of class name
			// "applet_container"
			Element ell;
			nodes = Dom.getElementsByClassName("applet_scaler");
			Log.debug(nodes.getLength() + " scalers found");
			// so "nodes" is meaning something else here actually
			if (nodes.getLength() > 0) {
				// get the first node that really contains an articleElement
				for (int i = 0; i < nodes.getLength(); i++) {
					ell = nodes.getItem(i);

					Node elChild = Element.as(ell.getChild(1));
					Log.debug(elChild);
					if (elChild != null
							&& Element.as(elChild).hasTagName("ARTICLE")) {
						// found!!
						if (elChild == el) {
							Log.debug("first article");
							// firstDummy!
							// now we can create dummy elements before & after
							// each applet
							// with tabindex 10000, for ticket #5158
							if (firstDummy == null) {
								tackleFirstDummy(el);

								if (lastDummy != null) {
									programFocusEvent(firstDummy, lastDummy);
								}
							}
						}
						break;
					}
				}
				// get the last node that really contains an articleElement
				for (int i = nodes.getLength() - 1; i >= 0; i--) {
					ell = nodes.getItem(i);
					Node elChild = ell.getChild(1);
					if (elChild != null
							&& Element.as(elChild).hasTagName("ARTICLE")) {
						// found!!
						if (elChild == el) {
							// lastDummy!
							if (lastDummy == null) {
								tackleLastDummy(el);

								if (firstDummy != null) {
									programFocusEvent(firstDummy, lastDummy);
								}
							}
						}
						break;
					}
				}

				if (dummies.size() == 0) {
					Log.debug("dummies size 0");
					for (int i = 0; i < nodes.getLength(); i++) {
						addDummies(nodes.getItem(i), i);
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
		splash = new SplashDialog(showLogo, articleElement.getId(), this);

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

	/** Article element */
	public ArticleElement ae;

	private int computedWidth = 0;
	private int computedHeight = 0;
	private final GLookAndFeelI laf;

	/**
	 * Callback from renderGGBElement to run, if everything is done
	 */
	private JavaScriptObject onLoadCallback = null;

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
		ae.getStyle().setBorderWidth(0, Style.Unit.PX);
		ae.getStyle().setBorderStyle(Style.BorderStyle.SOLID);
		ae.getStyle().setBorderColor(dpBorder);
		gf.getStyleElement().getStyle().setBorderWidth(px, Style.Unit.PX);
		gf.getStyleElement().getStyle().setBorderStyle(Style.BorderStyle.SOLID);
		gf.getStyleElement().getStyle().setBorderColor(dpBorder);
	}

	private static void setBorder(ArticleElement ae, Element gfE,
			String dpBorder, int px) {
		ae.getStyle().setBorderWidth(0, Style.Unit.PX);
		ae.getStyle().setBorderStyle(Style.BorderStyle.SOLID);
		ae.getStyle().setBorderColor(dpBorder);
		gfE.getStyle().setBorderWidth(px, Style.Unit.PX);
		gfE.getStyle().setBorderStyle(Style.BorderStyle.SOLID);
		gfE.getStyle().setBorderColor(dpBorder);
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

	public static void useDataParamBorder(ArticleElement ae, Element gfE) {
		// Log.debug("useDataParamBorder - " + ae.getClassName());
		String dpBorder = ae.getDataParamBorder();
		if (dpBorder != null) {
			if (dpBorder.equals("none")) {
				setBorder(ae, gfE, "transparent", 1);
			} else {
				setBorder(ae, gfE, dpBorder, 1);
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
		if (dpBorder != null && dpBorder.equals("none")) {
			setBorder(ae, gf, "transparent", 1);
			return;
		}
	}

	public static void useFocusedBorder(ArticleElement ae, Element gfE) {
		// Log.debug("useFocusedBorder - " + ae.getClassName());
		String dpBorder = ae.getDataParamBorder();
		gfE.removeClassName(GeoGebraConstants.APPLET_UNFOCUSED_CLASSNAME);
		gfE.addClassName(GeoGebraConstants.APPLET_FOCUSED_CLASSNAME);
		if (dpBorder != null && dpBorder.equals("none")) {
			setBorder(ae, gfE, "transparent", 1);
			return;
		}
	}

	public void runAsyncAfterSplash() {
		final GeoGebraFrameW inst = this;
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
	        GeoGebraFrameW inst, AppW app) {
		handleLoadFile(articleElement, app);
	}

	private static void handleLoadFile(ArticleElement articleElement, AppW app) {
		ViewW view = new ViewW(articleElement, app);
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
			setWidth(width - BORDER_WIDTH + "px");
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
			setHeight(height - BORDER_HEIGHT + "px");
		} else {
			setHeight(height - BORDER_HEIGHT + "px");
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
	 * @param onLoadCallback
	 *            load callback
	 *
	 */
	public static void renderArticleElementWithFrame(final Element element,
	        GeoGebraFrameW frame, JavaScriptObject onLoadCallback) {

		final ArticleElement article = ArticleElement.as(element);
		if(Log.logger == null){
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
	public void remove() {
		this.removeFromParent();
		// this does not do anything!
		GeoGebraFrameW.getInstances()
				.remove(
		        GeoGebraFrameW.getInstances().indexOf(this));
		this.ae.removeFromParent();
		this.ae = null;
		this.app = null;
		fileLoader.setView(null);
		if (GeoGebraFrameW.getInstanceCount() == 0) {
			ResourcesInjector.removeResources();
		}
	}

	/**
	 * @return frame ID, unused
	 */
	public int getFrameID() {
		return frameID;
	}
}
