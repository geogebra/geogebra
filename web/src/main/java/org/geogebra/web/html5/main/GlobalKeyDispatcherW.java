package org.geogebra.web.html5.main;

import java.util.ArrayList;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.KeyCodes;
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
			event.preventDefault();
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
			event.preventDefault();
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

		event.stopPropagation();

		// SELECTED GEOS:
		// handle function keys, arrow keys, +/- keys for selected geos, etc.
		boolean handled = handleSelectedGeosKeys(
		        KeyCodes.translateGWTcode(event.getNativeKeyCode()), app
		                .getSelectionManager().getSelectedGeos(),
		        event.isShiftKeyDown(), event.isControlKeyDown(),
		        event.isAltKeyDown(), false);
		// if not handled, do not consume so that keyPressed works
		if (InFocus && handled) {
			event.preventDefault();
		}

		// Now comes what were in KeyUpEvent for the TAB key,
		// necessary to move it to here because preventDefault only
		// works here for the TAB key, otherwise both the default
		// browser action (for tabindex) and custom code would run
		KeyCodes kc = KeyCodes.translateGWTcode(event.getNativeKeyCode());
		if (kc == KeyCodes.TAB) {
			event.preventDefault();
			// event.stopPropagation() is already called!
			handleTab(event.isControlKeyDown(), event.isShiftKeyDown());
		} else if (kc == KeyCodes.ESCAPE) {
			event.preventDefault();
			app.loseFocus();
		}
	}

	@Override
	protected boolean handleCtrlShiftN(boolean isAltDown) {
		App.debug("unimplemented");
		return false;
	}

	@Override
	protected boolean handleEnter() {
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
}
