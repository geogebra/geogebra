package org.geogebra.web.html5.main;

import org.geogebra.common.gui.AccessibilityManagerInterface;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.GlobalKeyDispatcher;
import org.geogebra.common.util.CopyPaste;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.AlgebraInput;
import org.geogebra.web.html5.gui.GeoGebraFrameW;
import org.geogebra.web.html5.gui.GuiManagerInterfaceW;
import org.geogebra.web.html5.util.ArticleElement;
import org.geogebra.web.html5.util.CopyPasteW;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Handles keyboard events.
 */
public class GlobalKeyDispatcherW extends GlobalKeyDispatcher
		implements KeyUpHandler, KeyDownHandler, KeyPressHandler {
	private static boolean controlDown = false;
	private static boolean shiftDown = false;

	/**
	 * Used if we need tab working properly
	 */
	private boolean inFocus = false;

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
	private static void setDownKeys(boolean control, boolean shift) {
		controlDown = control;
		shiftDown = shift;
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
				NativeEvent nativeEvent = event.getNativeEvent();
				EventTarget node = nativeEvent.getEventTarget();
				if (!Element.is(node) || app == null) {
					return;
				}
				Element targetElement = Element.as(node);
				ArticleElement targetArticle = getGGBArticle(targetElement);
				if (targetArticle == null && app.isApplet()) {
					// clicked outside of GGB
					return;
				}

				HashMap<String, AppW> articleMap = GeoGebraFrameW
						.getArticleMap();
				AppW targetApp = targetArticle != null
						? articleMap.get(targetArticle.getId()) : (AppW) app;
				if (targetApp == null) {
					return;
				}

				switch (event.getTypeInt()) {
				default:
					// do nothing
					break;
				case Event.ONKEYDOWN:
					if (nativeEvent.getKeyCode() == GWTKeycodes.KEY_X
							&& nativeEvent.getCtrlKey()
							&& nativeEvent.getAltKey()) {
						app.hideMenu();
						app.closePopups();
						if (app.getActiveEuclidianView() != null) {
							app.getActiveEuclidianView()
									.getEuclidianController()
									.hideDynamicStylebar();
						}
						app.getSelectionManager().clearSelectedGeos();
						app.getAccessibilityManager().focusInput(true);
						nativeEvent.preventDefault();
						nativeEvent.stopPropagation();
					}

					break;
				case Event.ONKEYPRESS:
					if (Browser.isiOS() && isControlKeyDown(nativeEvent) && inFocus) {
						handleIosKeyboard((char) nativeEvent.getCharCode());
					}
					break;
				}
			}

		});
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
	 * @param el
	 *            child
	 * @return parent article element corresponding to applet
	 */
	private ArticleElement getGGBArticle(Element el) {
		// if SVG clicked, getClassName returns non-string
		if ((el.getClassName() + "").contains("geogebraweb-dummy-invisible")) {
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

	@Override
	public void setFocused(boolean focus) {
		Log.debug("Focused: " + focus);
		((AppW) app).getAppletFrame().focusLastDummy();
		inFocus = focus;
	}

	@Override
	public void onKeyPress(KeyPressEvent event) {
		setDownKeys(event);
		event.stopPropagation();
		if (inFocus) {
			KeyCodes kc = KeyCodes.translateGWTcode(event.getNativeEvent()
					.getKeyCode());
			// Do not prevent default for the v key, otherwise paste events are not fired
			if (kc != KeyCodes.TAB && event.getCharCode() != 'v'
					&& event.getCharCode() != 'c' && event.getCharCode() != 'x') {
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
			event.preventDefault();
		}

		event.stopPropagation();
		handleGeneralKeys(event);
	}

	/**
	 * Handles key event by disassembling it into primitive types and handling
	 * it using the mothod from common
	 *
	 * @param event
	 *            event
	 */
	public void handleGeneralKeys(KeyUpEvent event) {
		KeyCodes kc = KeyCodes.translateGWTcode(event.getNativeKeyCode());

		boolean handled = handleGeneralKeys(kc,
				event.isShiftKeyDown(),
				isControlKeyDown(event.getNativeEvent()),
		        event.isAltKeyDown(), false, true);
		if (handled) {
			event.preventDefault();
		}
	}

	private static boolean isControlKeyDown(NativeEvent event) {
		return event.getCtrlKey()
				|| (Browser.isMacOS() || Browser.isiOS()) && event.getMetaKey();
	}

	/**
	 *
	 * @param event
	 *            native event
	 */
	public void handleSelectedGeosKeysNative(NativeEvent event) {
		handleSelectedGeosKeys(
				KeyCodes.translateGWTcode(event
						.getKeyCode()), selection.getSelectedGeos(),
				event.getShiftKey(), event.getCtrlKey(), event.getAltKey(),
				false);
	}

	@Override
	public void onKeyDown(KeyDownEvent event) {
		KeyCodes kc = KeyCodes.translateGWTcode(event.getNativeKeyCode());
		if (kc == KeyCodes.TAB) {
			if (inFocus) {
				Log.debug("tabbing, should focus next element");
				event.preventDefault();
				event.stopPropagation();
			}
			return;
		}

		setDownKeys(event);
		event.stopPropagation();

		// SELECTED GEOS:
		// handle function keys, arrow keys, +/- keys for selected geos, etc.
		handleSelectedGeosKeys(
		        KeyCodes.translateGWTcode(event.getNativeKeyCode()), app
		                .getSelectionManager().getSelectedGeos(),
		        event.isShiftKeyDown(), event.isControlKeyDown(),
		        event.isAltKeyDown(), false);

		if (inFocus && preventBrowserCtrl(kc, event.isShiftKeyDown())
				&& event.isControlKeyDown()) {
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
		// unimplemented
		return false;
	}

	@Override
	protected boolean handleEnter() {
		if (super.handleEnter()) {
			return true;
		}

		if (app.getGuiManager() != null
		        && app.getGuiManager().noMenusOpen()) {
			if (app.showAlgebraInput()) {
				AlgebraInput algebraInput = ((GuiManagerInterfaceW) app.getGuiManager())
						.getAlgebraInput();
				if (algebraInput != null) {
					algebraInput.requestFocus();
					return true;
				}
			}
		}
		return false;
	}

	@Override
	protected void copyDefinitionsToInputBarAsList(ArrayList<GeoElement> geos) {
		// unimplemented
	}

	@Override
	protected void createNewWindow() {
		// unimplemented
	}

	@Override
	protected void showPrintPreview(App app2) {
		// unimplemented
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

	private void handleIosKeyboard(char code) {
		switch (code) {
			case 'v':
				CopyPasteW.pasteInternal(app);
				break;
			case 'c':
				CopyPaste.handleCutCopy(app, false);
				break;
			case 'x':
				CopyPaste.handleCutCopy(app, true);
				break;
			default:
				break;
		}

	}
}
