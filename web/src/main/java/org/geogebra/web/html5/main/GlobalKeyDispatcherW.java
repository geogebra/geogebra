package org.geogebra.web.html5.main;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import org.geogebra.common.gui.AccessibilityManagerInterface;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.GlobalKeyDispatcher;
import org.geogebra.common.util.CopyPaste;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.AlgebraInput;
import org.geogebra.web.html5.gui.GuiManagerInterfaceW;
import org.geogebra.web.html5.util.CopyPasteW;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.EventHandler;
import com.himamis.retex.editor.share.util.GWTKeycodes;
import com.himamis.retex.editor.share.util.KeyCodes;
import org.geogebra.web.html5.util.Dom;

import java.util.ArrayList;

/**
 * Handles keyboard events.
 */
public class GlobalKeyDispatcherW extends GlobalKeyDispatcher
		implements KeyUpHandler, KeyDownHandler, KeyPressHandler {
	private static boolean controlDown = false;
	private static boolean shiftDown = false;

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
	}

	private class GlobalShortcutHandler implements EventListener {

		@Override
		public void onBrowserEvent(Event event) {
			if (event.getTypeInt() == Event.ONKEYDOWN) {
				if (event.getKeyCode() == GWTKeycodes.KEY_X
						&& event.getCtrlKey()
						&& event.getAltKey()) {
					app.hideMenu();
					app.closePopups();
					if (app.getActiveEuclidianView() != null) {
						app.getActiveEuclidianView()
								.getEuclidianController()
								.hideDynamicStylebar();
					}
					app.getSelectionManager().clearSelectedGeos();
					app.getAccessibilityManager().focusInput(true);
					event.preventDefault();
					event.stopPropagation();
				}

				if (Browser.isiOS() && isControlKeyDown(event)) {
					handleIosKeyboard((char) event.getCharCode());
					event.preventDefault();
					event.stopPropagation();
				}

				KeyCodes kc = KeyCodes.translateGWTcode(event.getKeyCode());
				Log.debug("Hearing key code " + kc);
				if (kc == KeyCodes.TAB) {
					Element activeElement = Dom.getActiveElement();
					Log.debug(activeElement);
					if (activeElement != ((AppW) app).getAppletFrame().getLastElement()) {
						Log.debug("tabbing, should focus next element");
						event.preventDefault();
						event.stopPropagation();
					} else {
						Log.debug("successful escape from Alcatraz");
					}
				} else if (kc == KeyCodes.ESCAPE) {
					Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
						@Override
						public void execute() {
							((AppW) app).getAppletFrame().getLastElement().focus();
						}
					});
				}
			}
		}
	}

	public EventListener getGlobalShortcutHandler() {
		return new GlobalShortcutHandler();
	}

	@Override
	public void onKeyPress(KeyPressEvent event) {
		setDownKeys(event);
		KeyCodes kc = KeyCodes.translateGWTcode(event.getNativeEvent()
				.getKeyCode());
		// Do not prevent default for the v key, otherwise paste events are not fired
		if (kc != KeyCodes.TAB && event.getCharCode() != 'v'
				&& event.getCharCode() != 'c' && event.getCharCode() != 'x') {
			event.preventDefault();
			event.stopPropagation();
		}
		// this needs to be done in onKeyPress -- keyUp is not case sensitive
		if (!event.isAltKeyDown() && !event.isControlKeyDown() && !app.has(Feature.MOW_TEXT_TOOL)) {
			this.renameStarted(event.getCharCode());
		}
	}

	@Override
	public void onKeyUp(KeyUpEvent event) {
		setDownKeys(event);
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
		setDownKeys(event);

		// SELECTED GEOS:
		// handle function keys, arrow keys, +/- keys for selected geos, etc.
		handleSelectedGeosKeys(
		        KeyCodes.translateGWTcode(event.getNativeKeyCode()), app
		                .getSelectionManager().getSelectedGeos(),
		        event.isShiftKeyDown(), event.isControlKeyDown(),
		        event.isAltKeyDown(), false);

		if (preventBrowserCtrl(kc, event.isShiftKeyDown())
				&& event.isControlKeyDown()) {
			event.preventDefault();
			event.stopPropagation();
		}
	}

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
