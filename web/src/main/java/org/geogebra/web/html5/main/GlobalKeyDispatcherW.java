package org.geogebra.web.html5.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.GWTKeycodes;
import org.geogebra.common.main.KeyCodes;
import org.geogebra.common.util.Unicode;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.euclidian.EuclidianViewW;
import org.geogebra.web.html5.gui.GeoGebraFrameW;
import org.geogebra.web.html5.gui.GuiManagerInterfaceW;
import org.geogebra.web.html5.util.ArticleElement;
import org.geogebra.web.html5.util.Dom;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.FocusPanel;

/**
 * Handles keyboard events.
 */
public class GlobalKeyDispatcherW extends
        org.geogebra.common.main.GlobalKeyDispatcher implements KeyUpHandler,
        KeyDownHandler, KeyPressHandler {

	private static boolean controlDown = false;
	private static boolean shiftDown = false;
	private boolean keydownPreventsDefaultKeypressTAB = false;

	public static boolean getControlDown() {
		return controlDown;
	}

	public static boolean getShiftDown() {
		return shiftDown;
	}

	public static void setDownKeys(KeyEvent ev) {
		controlDown = ev.isControlKeyDown();
		shiftDown = ev.isShiftKeyDown();
	}

	/**
	 * Used if we need tab working properly
	 */
	public boolean inFocus = false;

	public static boolean isHandlingTab;

	/**
	 * @param app
	 *            application
	 */
	public GlobalKeyDispatcherW(App app) {
		super(app);
		initNativeKeyHandlers();
	}

	boolean tabfixdebug = false;

	// Temp for testing
	public void debug(String s){
		if (tabfixdebug)
			Log.debug(s);
	}

	private void initNativeKeyHandlers() {
		Event.addNativePreviewHandler(new NativePreviewHandler() {

			public void onPreviewNativeEvent(NativePreviewEvent event) {

				switch (event.getTypeInt()) {
					case Event.ONKEYDOWN:

					Element targetElement = Element.as(event.getNativeEvent()
						.getEventTarget());
					debug("keydown (" + event.getNativeEvent().getKeyCode()
							+ ") in target element: "
							+ targetElement.toString());
					ArticleElement targetArticle = getGGBArticle(targetElement);
					if (targetArticle == null) {
						// if (targetElement.getClassName().indexOf(
						// "geogebraweb-dummy-invisible") < 0) {
						// event.cancel();
						// isHandlingTab = true;
						// ArrayList<ArticleElement> mobileTags = ArticleElement
						// .getGeoGebraMobileTags();
						// mobileTags.get(0).focus();
						// }
						return;

					}
					debug("target article: " + targetArticle.getClassName());


					if (event.getNativeEvent().getKeyCode() == 9) { // TAB
																	// pressed

						debug("targetElement class: "
								+ targetElement.getClassName());

						if (!isFocused()) {
							debug("not focused");
							event.cancel();
							// FocusPanel nextDummy = getNextDummy(ggbApplet);
							isHandlingTab = true;
							// debug("nextDummy - focus");
							// nextDummy.setFocus(true);

							// TODO - set border in an other place...
							GeoGebraFrameW.useDataParamBorder(targetArticle,
									getChildElementByStyleName(targetArticle,
											"GeoGebraFrame"));
							ArticleElement nextArticle = getNextArticle(targetArticle);
							if (nextArticle == null) {
								// TODO: go to a dummy after last article
								Dom.getElementsByClassName(
										"geogebraweb-dummy-invisible")
										.getItem(1)
										.focus();

							} else {
								debug("nextArticle.focus()");
								debug(nextArticle.toString());
								nextArticle.focus();
							}

							//nextDummy.fireEvent(event);
							// DomEvent.fireNativeEvent(Document.get().createKeyDownEvent(false,
							// false, false, false, 9), nextDummy);

							// nextDummy.fireEvent(new KeyDownEvent(){
							//
							// });
							//
							// ne
							//
							// class TabKeyEvent extends KeyDownEvent{
							// public TabKeyEvent(){
							// super();
							// this.
							// }
							// }
							
//							ArticleElement nextApplet = getNextGGBApplet(ggbApplet);
//							if (nextApplet == null) {
//								// TODO: go to last dummy - maybe won't dummies
//								// for
//								// all GGW Articles...
//								GeoGebraFrameW.dummies.get(
//										GeoGebraFrameW.dummies.size() - 1)
//										.focus();
//								
//							} else {
//								debug("next applet gets the focus");
//
//								nextApplet.focus();
//
//								// !!! ????

//								GeoGebraFrameW.useFocusedBorder(nextApplet,
//										nextApplet.getFirstChildElement());

//								GeoGebraFrameW.useDataParamBorder(ggbApplet,
//										(GeoGebraFrameW) ((AppW) app)
//												.getAppletFrame());
							// }
						} else {
							// event.cancel();
							// FocusPanel nextDummy = getNextDummy(ggbApplet);
							// isHandleTab = true;
							// nextDummy.setFocus(true);
							// nextDummy.fireEvent(event);
						}
					} 
					
					/*else if (event.getNativeEvent().getKeyCode() == 13) { // ENTER

						// pressed
						debug("enter pressed - isFocused: " + isFocused());
						if (!isFocused()) {
							event.cancel();
							setFocused(true);
						}
					} else {
						// debug("this key pressed: "
						// + event.getNativeEvent().getKeyCode());
					}*/


						//break;

				// case Event.ONKEYUP:
				// debug("keyup!");
				//
				// if (isGGBApplet(targetElement)) {
				// event.cancel();
				// }
				// break;
				// }
				}
			}

		});
	}

	public Element getChildElementByStyleName(Element parent,
			String childName){
		NodeList<Element> elements = Dom.getElementsByClassName(childName);
		for (int i = 0; i < elements.getLength(); i++) {
			if (elements.getItem(i).getParentElement() == parent) {
				return elements.getItem(i);
			}
		}
		return null;
	}


	FocusPanel getNextDummy(ArticleElement ggbapp) {
		debug("getNextDummy - " + ggbapp.getClassName() + " -> ");
		ArrayList<ArticleElement> mobileTags = ArticleElement
				.getGeoGebraMobileTags();
		for (int i = 0; i < mobileTags.size() - 1; i++) {
			if (mobileTags.get(i).equals(ggbapp)) {
				// GeoGebraFrameW.dummies2.get(i + 1).getElement().focus();

				debug((i + 1) + "");
				return GeoGebraFrameW.dummies2.get(i + 1);
			}
		}
		debug("0");
		return GeoGebraFrameW.dummies2.get(0);
	}

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

	/*
	 * Returns true, if el is ggb applet, but not the dummy element
	 */
	boolean isGGBApplet(Element el) {
		if (el.getClassName().indexOf("geogebraweb-dummy-invisible") >= 0) {
			return false;
		}
		return hasParentWithClassName(el, "geogebraweb")
				&& el.getClassName().indexOf("geogebraweb-dummy-invisible") < 0; // TODO:
																		// not
																// "geogebraweb"
	}

	ArticleElement getGGBArticle(Element el) {
		if (el.getClassName().indexOf("geogebraweb-dummy-invisible") >= 0) {
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

	// TODO - not only parent element
	private boolean hasParentWithClassName(Element el, String className) {
		do {

			List<String> classnames = Arrays.asList(el.getClassName()
					.split(" "));
			if (classnames.contains(className)) {
				return true;
			}
			if (el.hasParentElement())
				el = el.getParentElement();
		} while (el.hasParentElement());

		return false;
	}

	// TODO - not only parent element
	private Element getParentWithClassName(Element el, String className) {
		do {

			List<String> classnames = Arrays.asList(el.getClassName()
					.split(" "));
			if (classnames.contains(className)) {
				return el;
			}
			if (el.hasParentElement())
				el = el.getParentElement();
		} while (el.hasParentElement());
		return null;
	}

	public void setFocused(boolean f) {
		debug("Focus set to: " + f);
		inFocus = f;
	}

	public void setFocusedIfNotTab(){
		debug("setFocusedIfNotTab - isHandlingTab: " + isHandlingTab);
		if (isHandlingTab) {
			isHandlingTab = false;
		} else {
			setFocused(true);
		}
	}

	public boolean isFocused() {
		debug("focused: " + inFocus);
		return inFocus;
	}

	public void onKeyPress(KeyPressEvent event) {
		debug("GKDW - onKeyPress");
		setDownKeys(event);
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
		if (!event.isAltKeyDown() && !event.isControlKeyDown()) {
			this.renameStarted(event.getCharCode());
		}
	}

	public void onKeyUp(KeyUpEvent event) {
		debug("GKDW - onKeyUp");
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
		debug("GKDW - dispatchEvent");
		// we Must find out something here to identify the component that fired
		// this, like class names for example,
		// id-s or data-param-attributes

		// we have keypress here only
		// do this only, if we really have focus

		debug("GKDW.dispathEvent - inFocus: " + inFocus);

		if (inFocus) {
			handleKeyPressed(event);
		} else if (event.getNativeKeyCode() == com.google.gwt.event.dom.client.KeyCodes.KEY_ENTER) {
			setFocused(true);
		}

	}

	private boolean handleKeyPressed(KeyUpEvent event) {
		debug("GKDW - handleKeyPressed");
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
		debug("GKDW - handleGeneralkeys");

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
		        event.isShiftKeyDown(), event.isControlKeyDown(),
		        event.isAltKeyDown(), false, true);
		if (handled) {
			event.preventDefault();
		}
		return handled;

	}

	private boolean handleSelectedGeosKeys(KeyUpEvent event,
	        ArrayList<GeoElement> geos) {

		return handleSelectedGeosKeys(
		        KeyCodes.translateGWTcode(event.getNativeKeyCode()), geos,
		        event.isShiftKeyDown(), event.isControlKeyDown(),
		        event.isAltKeyDown(), false);
	}

	public boolean handleSelectedGeosKeysNative(NativeEvent event) {
		return handleSelectedGeosKeys(
		        org.geogebra.common.main.KeyCodes.translateGWTcode(event
		                .getKeyCode()), selection.getSelectedGeos(),
		        event.getShiftKey(), event.getCtrlKey(), event.getAltKey(),
		        false);
	}

	public void onKeyDown(KeyDownEvent event) {
		Log.debug("KEY pressed::"
				+ KeyCodes.translateGWTcode(event.getNativeKeyCode()));
		setDownKeys(event);
		// AbstractApplication.debug("onkeydown");

		EuclidianViewW.tabPressed = false;

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
		KeyCodes kc = KeyCodes.translateGWTcode(event.getNativeKeyCode());
		if (kc == KeyCodes.TAB) {

			debug("InFocus (GKDW.onkeydown): " + this.inFocus);

			// event.stopPropagation() is already called!
			boolean success = handleTab(event.isControlKeyDown(),
					event.isShiftKeyDown(), false);
			debug("handletab success: " + success);

			if (!success) {
				// should select first GeoElement in next applet
				// this should work well except from last to first
				// so there will be a blur handler there

				// it would be too hard to select the first GeoElement
				// from here, so this will be done in the focus handler
				// of the other applet, depending on whether really
				// this code called it, and it can be done by a static
				// variable for the short term
				EuclidianViewW.tabPressed = true;

				// except EuclidianViewW.lastInstance, do not prevent:
				if (EuclidianViewW.lastInstance.isInFocus()) {
					keydownPreventsDefaultKeypressTAB = true;
					EuclidianViewW.lastInstance.getCanvas().getElement().blur();
				} else {
					keydownPreventsDefaultKeypressTAB = false;
				}
			} else {
				EuclidianViewW.tabPressed = false;
				keydownPreventsDefaultKeypressTAB = true;
			}
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

			if (GeoGebraFrameW.dummies.size() > 0) {
				debug("dummies! :)");
			}

			inFocus = false;

			// printActiveElement();

		} else if (inFocus && preventBrowserCtrl(kc)
				&& event.isControlKeyDown()) {
			event.preventDefault();
		}

		if (keydownPreventsDefaultKeypressTAB) {
			event.preventDefault();
		}

	}

	private boolean preventBrowserCtrl(KeyCodes kc) {
		return kc == KeyCodes.S || kc == KeyCodes.O;
	}

	public static native void printActiveElement() /*-{
		$wnd.console.log($wnd.document.activeElement);
	}-*/;

	/**
	 * This method is almost the same as GlobalKeyDispatcher.handleTab, just is
	 * also return a value whether the operation was successful in case of no
	 * cycle
	 */
	public boolean handleTab(boolean isControlDown, boolean isShiftDown, boolean cycle) {

		debug("GKDW - handleTab");

		app.getActiveEuclidianView().closeDropdowns();
		
		if (isShiftDown) {
			selection.selectLastGeo(app.getActiveEuclidianView());
			return true;
		}
		boolean forceRet = false;
		if (selection.getSelectedGeos().size() == 0) {
			forceRet = true;
		}
		return selection.selectNextGeo(app.getActiveEuclidianView(), cycle)
				|| forceRet;
	}

	@Override
	protected boolean handleCtrlShiftN(boolean isAltDown) {
		Log.debug("unimplemented");
		return false;
	}

	@Override
	protected boolean handleEnter() {
		debug("GKDW - handleEnter");
		if (super.handleEnter()) {
			return true;
		}

		if (((AppW) app).isUsingFullGui()
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
	 * @param keyCode
	 *            GWT / JavaScript keycode
	 * @return ug superscript 2 for Alt-2
	 */
	public static String processAltCode(int keyCode) {
		switch (keyCode) {

		case GWTKeycodes.KEY_O:
			return Unicode.DEGREE;

		case GWTKeycodes.KEY_P:
			if (shiftDown) {
				return Unicode.Pi + "";
			} else {
				return Unicode.pi + "";
			}

		case GWTKeycodes.KEY_I:
			return Unicode.IMAGINARY;

		case GWTKeycodes.KEY_A:
			if (shiftDown) {
				return Unicode.Alpha + "";
			} else {
				return Unicode.alpha + "";
			}

		case GWTKeycodes.KEY_B:
			if (shiftDown) {
				return Unicode.Beta + "";
			} else {
				return Unicode.beta + "";
			}

		case GWTKeycodes.KEY_G:
			if (shiftDown) {
				return Unicode.Gamma + "";
			} else {
				return Unicode.gamma + "";
			}

		case GWTKeycodes.KEY_T:
			if (shiftDown) {
				return Unicode.Theta + "";
			} else {
				return Unicode.theta + "";
			}

		case GWTKeycodes.KEY_U:
			// U, euro sign is shown on HU
			return Unicode.INFINITY + "";

		case GWTKeycodes.KEY_L:
			// L, \u0141 sign is shown on HU
			if (shiftDown) {
				return Unicode.Lambda + "";
			} else {
				return Unicode.lambda + "";
			}

		case GWTKeycodes.KEY_M:
			if (shiftDown) {
				return Unicode.Mu + "";
			} else {
				return Unicode.mu + "";
			}

		case GWTKeycodes.KEY_W:
			// Alt-W is | needed for abs()
			if (shiftDown) {
				return Unicode.Omega + "";
			} else {
				return Unicode.omega + "";
			}

		case GWTKeycodes.KEY_R:
			return Unicode.SQUARE_ROOT + "";

		case GWTKeycodes.KEY_1:
			return Unicode.Superscript_1 + "";

		case GWTKeycodes.KEY_2:
			return Unicode.Superscript_2 + "";

		case GWTKeycodes.KEY_3:
			return Unicode.Superscript_3 + "";

		case GWTKeycodes.KEY_4:
			return Unicode.Superscript_4 + "";

		case GWTKeycodes.KEY_5:
			return Unicode.Superscript_5 + "";

		case GWTKeycodes.KEY_6:
			return Unicode.Superscript_6 + "";

		case GWTKeycodes.KEY_7:
			return Unicode.Superscript_7 + "";

		case GWTKeycodes.KEY_8:
			return Unicode.Superscript_8 + "";

		case GWTKeycodes.KEY_9:
			return Unicode.Superscript_9 + "";

		case GWTKeycodes.KEY_0:
			return Unicode.Superscript_0 + "";

		case GWTKeycodes.KEY_MINUS:
			return Unicode.Superscript_Minus + "";

		case GWTKeycodes.KEY_X:
			return "^x";

		case GWTKeycodes.KEY_Y:
			return "^y";

		default:
			return null;
		}
	}



	/**
	 * 
	 * @param e
	 *            The KeyEvent
	 * @return true if unwanted key combination has pressed.
	 */
	public static boolean isBadKeyEvent(KeyEvent e) {
		return e.isAltKeyDown() && !e.isControlKeyDown()
				&& e.getNativeEvent().getCharCode() > 128;
	}
}
