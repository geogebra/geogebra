package org.geogebra.web.html5.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.geogebra.common.gui.AccessibilityManagerInterface;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.GlobalKeyDispatcher;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.euclidian.EuclidianViewW;
import org.geogebra.web.html5.gui.GeoGebraFrameW;
import org.geogebra.web.html5.gui.GuiManagerInterfaceW;
import org.geogebra.web.html5.util.ArticleElement;
import org.geogebra.web.html5.util.Dom;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.himamis.retex.editor.share.util.GWTKeycodes;
import com.himamis.retex.editor.share.util.KeyCodes;

/**
 * Handles keyboard events.
 */
public class GlobalKeyDispatcherW extends GlobalKeyDispatcher
		implements KeyUpHandler, KeyDownHandler, KeyPressHandler {
	private static boolean controlDown = false;
	private static boolean shiftDown = false;
	private boolean keydownPreventsDefaultKeypressTAB = false;

	/**
	 * Used if we need tab working properly
	 */
	private boolean inFocus = false;

	private static boolean isHandlingTab;

	/**
	 * @return whether ctrl is pressed
	 */
	public static boolean getControlDown() {
		return controlDown;
	}

	/**
	 * @return whether shift is pressed
	 */
	public static boolean getShiftDown() {
		return shiftDown;
	}

	/**
	 * Update ctrl, shift flags
	 *
	 * @param ev
	 *            key event
	 */
	public static void setDownKeys(KeyEvent<? extends EventHandler> ev) {
		setDownKeys(ev.isControlKeyDown(), ev.isShiftKeyDown());
	}

	/**
	 * Update ctrl, shift flags
	 *
	 * @param control
	 *            if control is down.
	 * @param shift
	 *            if shift is down.
	 */
	public static void setDownKeys(boolean control, boolean shift) {
		controlDown = control;
		shiftDown = shift;
	}

	/**
	 * @param tab
	 *            whether tab event was registered by preview handler
	 */
	static void setHandlingTab(boolean tab) {
		isHandlingTab = tab;
	}

	/**
	 * @param app
	 *            application
	 */
	public GlobalKeyDispatcherW(App app) {
		super(app);
		initNativeKeyHandlers();
	}

	private void initNativeKeyHandlers() {
		Event.addNativePreviewHandler(new NativePreviewHandler() {

			@Override
			public void onPreviewNativeEvent(NativePreviewEvent event) {
				EventTarget node = event.getNativeEvent().getEventTarget();
				if (!Element.is(node)) {
					return;
				}
				Element targetElement = Element.as(node);
				ArticleElement targetArticle = getGGBArticle(targetElement);
				if (targetArticle == null) {
					return;
				}

				HashMap<String, AppW> articleMap = GeoGebraFrameW
						.getArticleMap();
				if (articleMap.get(targetArticle.getId()) == null) {
					return;
				}
				boolean appfocused = articleMap.get(targetArticle.getId())
						.getGlobalKeyDispatcher().isFocused();

				switch (event.getTypeInt()) {
				default:
					// do nothing
					break;
				case Event.ONKEYDOWN:

					if (event.getNativeEvent()
							.getKeyCode() == GWTKeycodes.KEY_TAB) { // TAB
																	// pressed
						if (!app.getAccessibilityManager().isTabOverGeos()) {
							return;
						}
						if (!appfocused) {
							event.cancel();
							setHandlingTab(true);

							// TODO - set border in an other place...
							GeoGebraFrameW.useDataParamBorder(targetArticle,
									getChildElementByStyleName(targetArticle,
											"GeoGebraFrame"));
							ArticleElement nextArticle = getNextArticle(targetArticle);
							focusArticle(nextArticle);
						}
					} else if (app != null
							&& event.getNativeEvent()
									.getKeyCode() == GWTKeycodes.KEY_X
							&& event.getNativeEvent().getCtrlKey()
							&& event.getNativeEvent().getAltKey()) {
						app.hideMenu();
						app.closePopups();
						if (app.getActiveEuclidianView() != null) {
							app.getActiveEuclidianView()
									.getEuclidianController()
									.hideDynamicStylebar();
						}
						app.getSelectionManager().clearSelectedGeos();
						app.getAccessibilityManager().focusInput(true);
						event.getNativeEvent().preventDefault();
						event.getNativeEvent().stopPropagation();
					}

					preventIfNotTabOrEnter(event, appfocused);
					break;
				case Event.ONKEYPRESS:
				case Event.ONKEYUP:
					// not TAB and not ENTER
					preventIfNotTabOrEnter(event, appfocused);
				}
			}

		});
	}

	/**
	 * Focus article or dummy (if article is null)
	 *
	 * @param nextArticle
	 *            article to be focused
	 */
	protected void focusArticle(ArticleElement nextArticle) {
		if (nextArticle == null) {
			// TODO: go to a dummy after last article
			NodeList<Element> dummies = Dom
					.getElementsByClassName("geogebraweb-dummy-invisible");
			if (dummies.getLength() > 0) {
				dummies.getItem(0).focus();
			} else {
				Log.warn("No dummy found.");
			}

		} else {
			nextArticle.focus();
		}
	}

	/**
	 * @param event
	 *            native event
	 * @param appfocused
	 *            whether app is focused
	 */
	protected void preventIfNotTabOrEnter(NativePreviewEvent event,
			boolean appfocused) {
		if (event.getNativeEvent().getKeyCode() != 9
				&& event.getNativeEvent().getKeyCode() != 13) {
			if (!appfocused) {
				event.cancel();
			}
		}
	}

	/**
	 * @return whether tab is handled
	 */
	public static boolean getIsHandlingTab() {
		return isHandlingTab;
	}

	/**
	 * @param parent
	 *            parent
	 * @param childName
	 *            class name of child
	 * @return children with given class name
	 */
	public Element getChildElementByStyleName(Element parent,
			String childName) {
		NodeList<Element> elements = Dom.getElementsByClassName(childName);
		for (int i = 0; i < elements.getLength(); i++) {
			if (elements.getItem(i).getParentElement() == parent) {
				return elements.getItem(i);
			}
		}
		return null;
	}

	/**
	 * @param ggbapp
	 *            current article
	 * @return next article
	 */
	ArticleElement getNextArticle(ArticleElement ggbapp) {
		ArrayList<ArticleElement> mobileTags = ArticleElement
				.getGeoGebraMobileTags();
		for (int i = 0; i < mobileTags.size() - 1; i++) {
			if (mobileTags.get(i).equals(ggbapp)) {
				return mobileTags.get(i + 1);
			}
		}

		NodeList<Element> appletscalers = Dom
				.getElementsByClassName("applet_scaler");
		for (int i = 0; i < appletscalers.getLength() - 1; i++) {
			Element actualArticle = appletscalers.getItem(i)
					.getElementsByTagName("Article").getItem(0);
			if (ggbapp.equals(actualArticle)) {

				return ArticleElement.as(appletscalers.getItem(i + 1)
						.getFirstChildElement());
			}
		}
		return null;
	}

	/**
	 * @param el
	 *            child
	 * @return parent article element corresponding to applet
	 */
	ArticleElement getGGBArticle(Element el) {
		// if SVG clicked, getClassName returns non-string
		if ((el.getClassName() + "")
				.indexOf("geogebraweb-dummy-invisible") >= 0) {
			return null;
		}

		// TODO: sure ArticleElement?
		Element ggwparent = getParentWithClassName(el, "geogebraweb");
		// debug("ggwparent tagname: " + ggwparent.getTagName());
		if (ggwparent != null && ggwparent.getTagName().equals("ARTICLE")) {
			return ArticleElement.as(ggwparent);
		}

		ggwparent = getParentWithClassName(el, "applet_scaler");
		if (ggwparent != null) {
			NodeList<Element> articles = ggwparent
					.getElementsByTagName("article");
			if (articles.getLength() > 0) {
				return ArticleElement.as(articles.getItem(0));
			}
		}
		return null;
	}

	private static Element getParentWithClassName(Element child,
			String className) {
		Element el = child;
		do {
			List<String> classnames = Arrays
					.asList((el.getClassName() + "")
					.split(" "));
			if (classnames.contains(className)) {
				return el;
			}
			if (el.hasParentElement()) {
				el = el.getParentElement();
			}
		} while (el.hasParentElement());
		return null;
	}

	/**
	 * @param focus
	 *            whether this applet has focus
	 */
	public void setFocused(boolean focus) {
		inFocus = focus;
	}

	/**
	 * Set to focus unless we are handlin tab key
	 */
	public void setFocusedIfNotTab() {
		if (isHandlingTab) {
			setHandlingTab(false);
		} else {
			setFocused(true);
		}
	}

	/**
	 * @return whether this applet has focus
	 */
	public boolean isFocused() {
		return inFocus;
	}

	@Override
	public void onKeyPress(KeyPressEvent event) {
		setDownKeys(event);
		Log.debug("PRESS");
		event.stopPropagation();
		if (inFocus) {
			// in theory, default action of TAB is not triggered here
			// but it seems Firefox triggers the default action of TAB
			// here (or some place other than onKeyDown), so we only
			// have to call preventdefault if it is not a TAB key!
			// TAB only fires in Firefox here, and it only has a keyCode!
			KeyCodes kc = KeyCodes.translateGWTcode(event.getNativeEvent()
					.getKeyCode());
			if (kc != KeyCodes.TAB) {
				event.preventDefault();
			} else if (keydownPreventsDefaultKeypressTAB) {
				// we only have to allow default action for TAB
				// if the onKeyDown handler allowed it, so we
				// have to check this boolean here, which is double
				// useful for some other reason as well,
				// in EuclidianViewW, for checking action on focus
				event.preventDefault();
			}
		}

		// this needs to be done in onKeyPress -- keyUp is not case sensitive
		if (!event.isAltKeyDown() && !event.isControlKeyDown() && !app.has(Feature.MOW_TEXT_TOOL)) {
			this.renameStarted(event.getCharCode());
		}
	}

	@Override
	public void onKeyUp(KeyUpEvent event) {
		setDownKeys(event);
		if (inFocus) {
			// KeyCodes kc =
			// KeyCodes.translateGWTcode(event.getNativeKeyCode());
			// if (kc != KeyCodes.TAB) {
				// maybe need to check TAB in Firefox, or in onKeyPress
			// but probably not, so this check is commented out
				event.preventDefault();
			// }
		}
		event.stopPropagation();
		// now it is private, but can be public, also it is void, but can return
		// boolean as in desktop, if needed
		dispatchEvent(event);
	}

	private void dispatchEvent(KeyUpEvent event) {
		// we Must find out something here to identify the component that fired
		// this, like class names for example,
		// id-s or data-param-attributes

		// we have keypress here only
		// do this only, if we really have focus

		if (inFocus) {
			handleKeyPressed(event);
		} else if (event.getNativeKeyCode() == com.google.gwt.event.dom.client.KeyCodes.KEY_ENTER) {
			setFocused(true);
		}
	}

	private boolean handleKeyPressed(KeyUpEvent event) {
		// GENERAL KEYS:
		// handle ESC, function keys, zooming with Ctrl +, Ctrl -, etc.
		if (handleGeneralKeys(event)) {
			return true;
		}

		// SELECTED GEOS:
		// handle function keys, arrow keys, +/- keys for selected geos, etc.
		// if (handleSelectedGeosKeys(event,
		// app.getSelectionManager().getSelectedGeos())) {
		// return true;
		// }
		return false;
	}

	/**
	 * Handles key event by disassembling it into primitive types and handling
	 * it using the mothod from common
	 *
	 * @param event
	 *            event
	 * @return whether event was consumed
	 */
	public boolean handleGeneralKeys(KeyUpEvent event) {

		KeyCodes kc = KeyCodes.translateGWTcode(event.getNativeKeyCode());
		if (kc == KeyCodes.TAB || kc == KeyCodes.ESCAPE) {
			// the problem is that we want to prevent the default action
			// of the TAB key event... but this is too late to do
			// in KeyUpEvent, so instead, we're going to handle TAB
			// as early as KeyDownEvent, and do nothing here to make
			// sure things are not executed twice (assuming return true)

			// maybe in Chrome this is needed here as well...
			event.preventDefault();

			// in theory, this is already called, but maybe not in case of
			// AlgebraInputW.onKeyUp, AutoCompleteTextFieldW.onKeyUp
			event.stopPropagation();

			return true;
		}

		boolean handled = handleGeneralKeys(kc,
				event.isShiftKeyDown(),
				isControlKeyDown(event),
		        event.isAltKeyDown(), false, true);
		if (handled) {
			event.preventDefault();
		}
		return handled;
	}

	private static boolean isControlKeyDown(KeyUpEvent event) {
		return event.isControlKeyDown()
				|| Browser.isMacOS() && event.isMetaKeyDown();
	}

	/**
	 *
	 * @param event
	 *            native event
	 * @return whether it was handled
	 */
	public boolean handleSelectedGeosKeysNative(NativeEvent event) {
		return handleSelectedGeosKeys(
				KeyCodes.translateGWTcode(event
		                .getKeyCode()), selection.getSelectedGeos(),
		        event.getShiftKey(), event.getCtrlKey(), event.getAltKey(),
		        false);
	}

	@Override
	public void onKeyDown(KeyDownEvent event) {
		Log.debug("KEY pressed::"
				+ KeyCodes.translateGWTcode(event.getNativeKeyCode()) + " in "
				+ getActive());
		KeyCodes kc = KeyCodes.translateGWTcode(event.getNativeKeyCode());
		if (!app.getAccessibilityManager().isTabOverGeos()
				&& kc == KeyCodes.TAB) {
			event.stopPropagation();
			if (app.getKernel().getConstruction().isEmpty()) {
				event.preventDefault();
				app.getAccessibilityManager().focusFirstElement();
				return;
			}

			app.getAccessibilityManager().focusNext(null, -1);

		}

		setDownKeys(event);
		// AbstractApplication.debug("onkeydown");

		EuclidianViewW.resetTab();

		event.stopPropagation();

		// this is quite complex, call at the end of the method
		keydownPreventsDefaultKeypressTAB = false;

		// SELECTED GEOS:
		// handle function keys, arrow keys, +/- keys for selected geos, etc.
		boolean handled = handleSelectedGeosKeys(
		        KeyCodes.translateGWTcode(event.getNativeKeyCode()), app
		                .getSelectionManager().getSelectedGeos(),
		        event.isShiftKeyDown(), event.isControlKeyDown(),
		        event.isAltKeyDown(), false);
		// if not handled, do not consume so that keyPressed works
		if (inFocus && handled) {
			keydownPreventsDefaultKeypressTAB = true;
		}

		// Now comes what were in KeyUpEvent for the TAB key,
		// necessary to move it to here because preventDefault only
		// works here for the TAB key, otherwise both the default
		// browser action (for tabindex) and custom code would run
		if (kc == KeyCodes.TAB) {

			// event.stopPropagation() is already called!
			boolean success = handleTab(event.isControlKeyDown(),
					event.isShiftKeyDown());
			keydownPreventsDefaultKeypressTAB = EuclidianViewW
					.checkTabPress(success);

		} else if (kc == KeyCodes.ESCAPE) {
			keydownPreventsDefaultKeypressTAB = true;
			// EuclidianViewW.tabPressed = false;
			// if (app.isApplet()) {
			// app.loseFocus();
			// }
			app.setMoveMode();
			// here we shall focus on a dummy element that is
			// after all graphics views by one:
			// if (GeoGebraFrameW.lastDummy != null) {
			// GeoGebraFrameW.lastDummy.focus();
			// }

			setFocused(false);

			// printActiveElement();

		} else if (inFocus && preventBrowserCtrl(kc, event.isShiftKeyDown())
				&& event.isControlKeyDown()) {
			event.preventDefault();
		}

		if (keydownPreventsDefaultKeypressTAB) {
			event.preventDefault();
		}
	}

	private native String getActive() /*-{
		return $doc.activeElement ? $doc.activeElement.tagName + "."
				+ $doc.activeElement.className : "?";
	}-*/;

	private static boolean preventBrowserCtrl(KeyCodes kc, boolean shift) {
		return kc == KeyCodes.S || kc == KeyCodes.O
				|| (kc == KeyCodes.D && shift) || (kc == KeyCodes.C && shift);
	}

	@Override
	public boolean handleTab(boolean isControlDown, boolean isShiftDown) {
		AccessibilityManagerInterface am = app.getAccessibilityManager();
		if (!am.isTabOverGeos()) {
			return true;
		}

		app.getActiveEuclidianView().closeDropdowns();

		if (am.isCurrentTabExitGeos(isShiftDown)) {
			return true;
		}

		if (isShiftDown) {
			if (!am.tabEuclidianControl(false)) {
				selection.selectLastGeo(app.getActiveEuclidianView());
			}

			return true;
		}

		boolean forceRet = false;
		if (selection.getSelectedGeos().size() == 0) {
			forceRet = true;
		}
		if (am.tabEuclidianControl(true)) {
			return true;
		}

		boolean hasNext = selection.selectNextGeo(app.getActiveEuclidianView());

		return hasNext || forceRet;
	}

	@Override
	protected boolean handleCtrlShiftN(boolean isAltDown) {
		Log.debug("unimplemented");
		return false;
	}

	@Override
	protected boolean handleEnter() {
		if (super.handleEnter()) {
			return true;
		}

		if (app.getGuiManager() != null
		        && ((GuiManagerInterfaceW) app.getGuiManager()).noMenusOpen()) {
			if (app.showAlgebraInput()) {
				// && !((GuiManagerW) app.getGuiManager()).getAlgebraInput()
				// .hasFocus()) {

				if (((GuiManagerInterfaceW) app.getGuiManager())
						.getAlgebraInput() != null) {
					((GuiManagerInterfaceW) app.getGuiManager())
							.getAlgebraInput().requestFocus();
					return true;
				}
			}
		}
		return false;
	}

	@Override
	protected void copyDefinitionsToInputBarAsList(ArrayList<GeoElement> geos) {
		Log.debug("unimplemented");
	}

	@Override
	protected void createNewWindow() {
		Log.debug("unimplemented");
	}

	@Override
	protected void showPrintPreview(App app2) {
		Log.debug("unimplemented");
	}

	/**
	 *
	 * @param e
	 *            The KeyEvent
	 * @return true if unwanted key combination has pressed.
	 */
	public static boolean isBadKeyEvent(KeyEvent<? extends EventHandler> e) {
		return e.isAltKeyDown() && !e.isControlKeyDown()
				&& e.getNativeEvent().getCharCode() > 128;
	}

	/**
	 * @return new focus handler that unblocks keyboard features in this applet
	 */
	public FocusHandler getFocusHandler() {
		return new FocusHandler() {
			@Override
			public void onFocus(FocusEvent event) {
				GlobalKeyDispatcherW.this.setFocused(true);

			}
		};
	}

	@Override
	protected KeyCodes translateKey(int i) {
		return KeyCodes.translateGWTcode(i);
	}

}
