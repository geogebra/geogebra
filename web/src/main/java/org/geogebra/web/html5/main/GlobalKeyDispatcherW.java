package org.geogebra.web.html5.main;

import java.util.ArrayList;

import org.geogebra.common.euclidian.draw.DrawList;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.GWTKeycodes;
import org.geogebra.common.main.KeyCodes;
import org.geogebra.common.util.Unicode;
import org.geogebra.web.html5.euclidian.EuclidianViewW;
import org.geogebra.web.html5.gui.GeoGebraFrame;
import org.geogebra.web.html5.gui.GuiManagerInterfaceW;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;

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
	public boolean InFocus = true;

	/**
	 * @param app
	 *            application
	 */
	public GlobalKeyDispatcherW(App app) {
		super(app);
	}

	@Override
	public void handleFunctionKeyForAlgebraInput(int i, GeoElement geo) {
		// TODO Auto-generated method stub

	}

	public void onKeyPress(KeyPressEvent event) {
		App.debug("Key pressed:" + event.getCharCode());
		setDownKeys(event);
		event.stopPropagation();
		if (InFocus) {
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
			App.debug("Key pressed:" + event.getCharCode());
			this.renameStarted(event.getCharCode());
		}
	}

	public void onKeyUp(KeyUpEvent event) {
		setDownKeys(event);
		if (InFocus) {
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
		App.debug(InFocus + "");
		if (InFocus) {
			handleKeyPressed(event);
		} else if (event.getNativeKeyCode() == com.google.gwt.event.dom.client.KeyCodes.KEY_ENTER) {
			InFocus = true;
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

		return handleGeneralKeys(kc,
		        event.isShiftKeyDown(), event.isControlKeyDown(),
		        event.isAltKeyDown(), false, true);

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
		if (InFocus && handled) {
			keydownPreventsDefaultKeypressTAB = true;
		}

		// Now comes what were in KeyUpEvent for the TAB key,
		// necessary to move it to here because preventDefault only
		// works here for the TAB key, otherwise both the default
		// browser action (for tabindex) and custom code would run
		KeyCodes kc = KeyCodes.translateGWTcode(event.getNativeKeyCode());
		if (kc == KeyCodes.TAB) {

			// event.stopPropagation() is already called!
			boolean success = handleTab(event.isControlKeyDown(),
					event.isShiftKeyDown(), false);

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
			app.loseFocus();
			// here we shall focus on a dummy element that is
			// after all graphics views by one:
			if (GeoGebraFrame.lastDummy != null) {
				GeoGebraFrame.lastDummy.focus();
			}
		}

		if (keydownPreventsDefaultKeypressTAB) {
			event.preventDefault();
		}
	}

	/**
	 * This method is almost the same as GlobalKeyDispatcher.handleTab, just is
	 * also return a value whether the operation was successful in case of no
	 * cycle
	 */
	public boolean handleTab(boolean isControlDown, boolean isShiftDown, boolean cycle) {
		if (app.has(Feature.DRAW_DROPDOWNLISTS_TO_CANVAS)) {
			app.getActiveEuclidianView().closeDropdowns();
		}
		
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
		App.debug("unimplemented");
		return false;
	}

	@Override
	protected boolean handleEnter() {
		if (app.has(Feature.DRAW_DROPDOWNLISTS_TO_CANVAS)
				&& selection.getSelectedGeos().size() == 1) {
			GeoElement geo = selection.getSelectedGeos().get(0);
			if (geo.isGeoList()) {
				DrawList.asDrawable(app, geo).selectCurrentItem();
				return true;
			}

		}

		if (((AppW) app).isUsingFullGui()
		        && ((GuiManagerInterfaceW) app.getGuiManager()).noMenusOpen()) {
			if (app.showAlgebraInput()) {
				// && !((GuiManagerW) app.getGuiManager()).getAlgebraInput()
				// .hasFocus()) {

				((GuiManagerInterfaceW) app.getGuiManager()).getAlgebraInput()
				        .requestFocus();

				return true;
			}
		}


		return false;
	}

	@Override
	protected void copyDefinitionsToInputBarAsList(ArrayList<GeoElement> geos) {
		App.debug("unimplemented");
	}

	@Override
	protected void createNewWindow() {
		App.debug("unimplemented");
	}

	@Override
	protected void showPrintPreview(App app2) {
		App.debug("unimplemented");
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
}
