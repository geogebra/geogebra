package org.geogebra.web.html5.main;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.gui.AccessibilityManagerInterface;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.App;
import org.geogebra.common.main.GlobalKeyDispatcher;
import org.geogebra.common.util.CopyPaste;
import org.geogebra.common.util.debug.Log;
import org.geogebra.gwtutil.NavigatorUtil;
import org.geogebra.web.html5.gui.AlgebraInput;
import org.geogebra.web.html5.gui.GuiManagerInterfaceW;
import org.geogebra.web.html5.util.CopyPasteW;
import org.gwtproject.dom.client.NativeEvent;
import org.gwtproject.event.dom.client.KeyDownEvent;
import org.gwtproject.event.dom.client.KeyDownHandler;
import org.gwtproject.event.dom.client.KeyEvent;
import org.gwtproject.event.dom.client.KeyPressEvent;
import org.gwtproject.event.dom.client.KeyPressHandler;
import org.gwtproject.event.dom.client.KeyUpEvent;
import org.gwtproject.event.dom.client.KeyUpHandler;
import org.gwtproject.user.client.DOM;
import org.gwtproject.user.client.Event;
import org.gwtproject.user.client.EventListener;

import com.himamis.retex.editor.share.util.GWTKeycodes;
import com.himamis.retex.editor.share.util.JavaKeyCodes;
import com.himamis.retex.editor.share.util.KeyCodes;
import com.himamis.retex.editor.web.MathFieldW;

import elemental2.dom.DomGlobal;

/**
 * Handles keyboard events.
 */
public class GlobalKeyDispatcherW extends GlobalKeyDispatcher
		implements KeyUpHandler, KeyDownHandler, KeyPressHandler {

	private static boolean controlDown = false;
	private static boolean shiftDown = false;
	private static boolean spaceDown = false;

	private static boolean rightAltDown = false;

	private static boolean leftAltDown = false;

	private boolean escPressed = false;

	/**
	 * Used to make sure the old mode is set correctly after grabbing and moving the Graphics View
	 */
	private int oldMode = EuclidianConstants.MODE_MOVE;

	/**
	 * @return whether ctrl is pressed
	 */
	public static boolean getControlDown() {
		return controlDown;
	}

	/**
	 * @return whether rightAlt is pressed
	 */
	public static boolean isRightAltDown() {
		return rightAltDown;
	}

	/**
	 * @return whether leftAlt is pressed
	 */
	public static boolean isLeftAltDown() {
		return leftAltDown;
	}

	/**
	 * @return whether shift is pressed
	 */
	public static boolean getShiftDown() {
		return shiftDown;
	}

	/**
	 * @return Whether space is pressed
	 */
	public static boolean getSpaceDown() {
		return spaceDown;
	}

	/**
	 * Update ctrl, shift flags
	 *
	 * @param ev
	 *            key event
	 */
	public static void setDownKeys(KeyEvent<?> ev) {
		setDownKeys(ev.isControlKeyDown(), ev.isShiftKeyDown());
	}

	/**
	 * setting left and right alt flags
	 * @param ev event
	 * @param down flag indicating if key was down or released
	 */
	public static void setDownAltKeys(KeyEvent<?> ev, boolean down) {
		if (MathFieldW.isRightAlt(ev.getNativeEvent())) {
			rightAltDown = down;
		}
		if (MathFieldW.isLeftAlt(ev.getNativeEvent())) {
			if (leftAltDown != down) {
				Log.warn("Left alt down: " + down);
			}
			leftAltDown = down;
		}
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
	public GlobalKeyDispatcherW(AppW app) {
		super(app);
		app.getGlobalHandlers().addEventListener(DomGlobal.window, "focus",
				event -> releaseAlts());
	}

	private void releaseAlts() {
		leftAltDown = false;
		rightAltDown = false;
	}

	private class GlobalShortcutHandler implements EventListener {

		private boolean grabModeSet = false;

		@Override
		public void onBrowserEvent(Event event) {
			if (CopyPasteW.incorrectTarget(event.getEventTarget().cast())
						&& !isGlobalEvent(event)) {
					return;
			}

			if (DOM.eventGetType(event) == Event.ONKEYDOWN) {
				if (handleKeyDown(event)) {
					event.preventDefault();
					event.stopPropagation();
				}
			} else if (DOM.eventGetType(event) == Event.ONKEYUP) {
				if (handleKeyUp(event)) {
					event.preventDefault();
					event.stopPropagation();
				}
			}
		}

		private boolean handleKeyDown(Event event) {
			boolean handled = false;

			if (event.getKeyCode() == GWTKeycodes.KEY_X
					&& event.getCtrlKey()
					&& event.getAltKey()) {
				handleCtrlAltX();
				handled = true;
			}
			if (NavigatorUtil.isiOS() && isControlKeyDown(event)) {
				handleIosKeyboard((char) event.getCharCode());
				handled = true;
			}
			if (isControlKeyDown(event)) {
				handled = handleCtrlKeys(NavigatorUtil.translateGWTcode(event.getKeyCode()),
						event.getShiftKey(), false, true);
			}
			KeyCodes kc = NavigatorUtil.translateGWTcode(event.getKeyCode());
			if (kc == KeyCodes.TAB) {
				if (!escPressed) {
					handled = handleTab(event.getShiftKey());
				}
			} else if (kc == KeyCodes.ESCAPE) {
				escPressed = true;
				handleEscForDropdown();
				if (app.isApplet()) {
					((AppW) GlobalKeyDispatcherW.this.app).moveFocusToLastWidget();
				} else {
					handleEscapeForNonApplets();
				}
				handled = true;
			} else if (isSpace(kc) || isShift(kc)) {
				handleSpaceOrShiftKey(kc, true);
				handled = true;
			} else {
				handled = handled || handleSelectedGeosKeys(event);
			}
			return handled;
		}

		private boolean handleKeyUp(Event event) {
			boolean handled = false;
			KeyCodes kc = NavigatorUtil.translateGWTcode(event.getKeyCode());
			if (isSpace(kc) || isShift(kc)) {
				handleSpaceOrShiftKey(kc, false);
				handled = true;
			}
			return handled;
		}

		private void handleSpaceOrShiftKey(KeyCodes kc, boolean down) {
			if (!shouldHandleSpaceOrShiftKeyForGrabMode(kc, down)) {
				return;
			}
			if (down) {
				oldMode = app.getMode();
				app.setMode(EuclidianConstants.MODE_GRAB);
				grabModeSet = true;
			} else {
				app.setMode(oldMode);
				grabModeSet = false;
			}
			if (isSpace(kc)) {
				spaceDown = down;
			} else {
				shiftDown = down;
			}
		}

		private boolean isSpace(KeyCodes kc) {
			return kc == KeyCodes.SPACE;
		}

		private boolean isShift(KeyCodes kc) {
			return kc == KeyCodes.SHIFT;
		}

		/**
		 * Checks whether the spaec/shift key should be handled.<br/>
		 * In case the space key is pressed, checks whether the space key should toggle the
		 * grab mode, by making sure no element is selected that needs special handling.
		 * @see App#handleSpaceKey()
		 * @param kc Key Code
		 * @param down Whether the key is down or up
		 * @return True if space/shift key should be handled for setting the grab mode, false else
		 */
		private boolean shouldHandleSpaceOrShiftKeyForGrabMode(KeyCodes kc, boolean down) {
			if (down && grabModeSet) {
				return false;
			}

			ArrayList<GeoElement> selectedGeos = app.getSelectionManager().getSelectedGeos();
			if (isShift(kc) || selectedGeos.isEmpty() || selectedGeos.size() > 1
					|| !app.getSelectionManager().isSelectableForEV(selectedGeos.get(0))) {
				return true;
			}

			GeoElement selectedGeo = app.getSelectionManager().getSelectedGeos().get(0);
			if (selectedGeo.isGeoBoolean()
					|| selectedGeo.isGeoInputBox()
					|| (selectedGeo.isGeoList() && ((GeoList) selectedGeo).drawAsComboBox())
					|| (selectedGeo.isGeoNumeric() && ((GeoNumeric) selectedGeo).isAnimatable()
					&& app.isRightClickEnabled())
					|| selectedGeo.getAuralTextForSpace() != null
					|| selectedGeo.hasScripts()) {
				return false;
			}
			return true;
		}
	}

	@Override
	protected void toggleTableView() {
		if (!app.getConfig().hasTableView()) {
			return;
		}

		((GuiManagerInterfaceW) app.getGuiManager()).toggleTableValuesView();
	}

	private void handleCtrlAltX() {
		app.hideMenu();
		app.closePopups();
		if (app.getActiveEuclidianView() != null) {
			app.getActiveEuclidianView()
					.getEuclidianController()
					.hideDynamicStylebar();
		}
		app.getSelectionManager().clearSelectedGeos();
		boolean force = !((GuiManagerInterfaceW) app.getGuiManager()).isAlgebraViewActive();
		app.getAccessibilityManager().focusInput(true, force);
	}

	private void handleEscapeForNonApplets() {
		app.setMoveMode();
		app.getActiveEuclidianView().getEuclidianController().clearSelections();
		app.getActiveEuclidianView().setSelectionRectangle(null);
		app.getActiveEuclidianView().getEuclidianController().resetLastMowHit();
	}

	public EventListener getGlobalShortcutHandler() {
		return new GlobalShortcutHandler();
	}

	@Override
	public void onKeyPress(KeyPressEvent event) {
		setDownKeys(event);
		KeyCodes kc = NavigatorUtil.translateGWTcode(event.getNativeEvent()
				.getKeyCode());
		// Do not prevent default for the v key, otherwise paste events are not fired
		if (kc != KeyCodes.TAB && event.getCharCode() != 'v'
				&& event.getCharCode() != 'c' && event.getCharCode() != 'x') {
			event.preventDefault();
			event.stopPropagation();
		}
		// this needs to be done in onKeyPress -- keyUp is not case sensitive
		if (!event.isAltKeyDown() && !event.isControlKeyDown() && !app.isWhiteboardActive()) {
			this.renameStarted(event.getCharCode());
		}
	}

	@Override
	public void onKeyUp(KeyUpEvent event) {
		setDownKeys(event);
		handleGeneralKeys(event);
		storeUndoInfoIfChanged();
	}

	/**
	 * Handles key event by disassembling it into primitive types and handling
	 * it using the method from common
	 *
	 * @param event
	 *            event
	 */
	public void handleGeneralKeys(KeyUpEvent event) {
		KeyCodes kc = NavigatorUtil.translateGWTcode(event.getNativeKeyCode());

		boolean handled = handleGeneralKeys(kc,
				event.isShiftKeyDown(),
				isControlKeyDown(event.getNativeEvent()),
				event.isAltKeyDown(), false, true);
		if (handled) {
			event.preventDefault();
		}
	}

	private static boolean isControlKeyDown(NativeEvent event) {
		return (NavigatorUtil.isMacOS() || NavigatorUtil.isiOS()) ? event.getMetaKey()
				: event.getCtrlKey();
	}

	/**
	 * handle function keys, arrow keys, +/- keys for selected geos, etc.
	 * @param event
	 *            native event
	 * @return if key was consumed
	 */
	public boolean handleSelectedGeosKeys(NativeEvent event) {
		return handleSelectedGeosKeys(
				NavigatorUtil.translateGWTcode(event
						.getKeyCode()), selection.getSelectedGeos(),
				event.getShiftKey(), event.getCtrlKey(), event.getAltKey(),
				false);
	}

	@Override
	public void onKeyDown(KeyDownEvent event) {
		KeyCodes kc = NavigatorUtil.translateGWTcode(event.getNativeKeyCode());
		setDownKeys(event);

		boolean handled = handleSelectedGeosKeys(event.getNativeEvent());

		if (handled) {
			event.stopPropagation();
		}
		if (handled || preventBrowserCtrl(kc, event.isShiftKeyDown())
				&& event.isControlKeyDown()) {
			event.preventDefault();
		}
	}

	private static boolean preventBrowserCtrl(KeyCodes kc, boolean shift) {
		return kc == KeyCodes.S || kc == KeyCodes.O
				|| (kc == KeyCodes.D && shift) || (kc == KeyCodes.C && shift);
	}

	/**
	 * @param isShiftDown whether Shift+Tab was pressed
	 * @return whether the tab was handled internally
	 */
	public boolean handleTab(boolean isShiftDown) {
		AccessibilityManagerInterface am = app.getAccessibilityManager();

		app.getActiveEuclidianView().closeDropdowns();

		if (isShiftDown) {
			return am.focusPrevious();
		} else {
			return am.focusNext();
		}
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
	protected void copyDefinitionsToInputBarAsList(List<GeoElement> geos) {
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
	public static boolean isBadKeyEvent(KeyEvent<?> e) {
		return e.isAltKeyDown() && !e.isControlKeyDown()
				&& e.getNativeEvent().getCharCode() > 128;
	}

	private void handleIosKeyboard(char code) {
		switch (code) {
			case 'v':
				CopyPasteW.pasteInternal((AppW) app);
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

	public void setEscPressed(boolean escPressed) {
		this.escPressed = escPressed;
	}

	/**
	 * Whether an event should be handled globally rather than by specific textfield
	 * @param event keybaord event
	 * @return whether event is global
	 */
	public static boolean isGlobalEvent(NativeEvent event) {
		int code = event.getKeyCode();
		if (isControlKeyDown(event)) {
			return code == JavaKeyCodes.VK_S;
		} else {
			return code == JavaKeyCodes.VK_F4;
		}
	}
}
