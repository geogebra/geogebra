package org.geogebra.web.html5.gui.inputfield;

import org.geogebra.common.kernel.geos.properties.HorizontalAlignment;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.keyboard.KeyboardManagerInterface;
import org.gwtproject.dom.client.Element;
import org.gwtproject.user.client.ui.FlowPanel;

import elemental2.dom.DomGlobal;
import elemental2.dom.Event;

public class CursorOverlayController {

	private final AppW app;
	private final AutoCompleteTextFieldW textField;
	private final FlowPanel main;
	private CursorOverlay cursorOverlay;
	private boolean hadSelection;
	private boolean enabled;
	private double blinkHandler;
	private boolean seleted;

	public CursorOverlayController(AutoCompleteTextFieldW textField, FlowPanel main) {
		this.app = textField.getApplication();
		this.textField = textField;
		this.main = main;
		enabled = false;
	}

	/**
	 * Hide keyboard and reset the keyaord field
	 * @param app application
	 */
	public static void hideKeyboard(AppW app) {
		if (CancelEventTimer.cancelKeyboardHide()) {
			return;
		}
		KeyboardManagerInterface kbManager = app.getKeyboardManager();
		if (app.hasPopup() && kbManager != null) {
			kbManager.setOnScreenKeyboardTextField(null);
			return;
		}
		app.hideKeyboard();
	}

	private void enableForTextField() {
		cursorOverlay = new CursorOverlay();
		textField.addFocusHandler(evt -> addDummyCursor());
		textField.addBlurHandler(evt -> removeDummyCursor());
		startBlinking();
		textField.updateInputBoxAlign();
		final Element element = textField.getInputElement();
		app.getGlobalHandlers().addEventListener(element, "touchstart",
				this::preventNativeSelection);

		app.getGlobalHandlers().addEventListener(main.getElement(),"touchstart",
				this::unselectOverlay);

	}

	private void startBlinking() {
		blinkHandler = DomGlobal.setInterval(event -> update(), 200);
	}

	private void preventNativeSelection(Event event) {
		event.preventDefault();
		if (cursorOverlay.hasFakeSelection()) {
			addDummyCursor();
		}
	}

	private void unselectOverlay(Event event) {
		event.preventDefault();
		event.stopPropagation();
		if (isSelected()) {
			unselectAll();
		}
	}

	public void update() {
		if (!enabled) {
			return;
		}
		startBlinking();
		cursorOverlay.update(textField.getCursorPos(), textField.getText());
	}

	public void selectAll() {
		if (!enabled) {
			return;
		}
		stopBlinking();
		cursorOverlay.addFakeSelection();
	}

	private void stopBlinking() {
		DomGlobal.clearTimeout(blinkHandler);
	}

	public void addDummyCursor() {
		main.add(cursorOverlay);
		main.addStyleName("withCursorOverlay");
		app.showKeyboard(textField, true);
		update();
	}

	public int removeDummyCursor() {
		// check for isAttached to avoid infinite recursion
		if (cursorOverlay.isAttached()) {
			cursorOverlay.removeFromParent();
			main.removeStyleName("withCursorOverlay");
			hideKeyboard(app);
		}
		return textField.getCaretPosition();
	}

	public void setFontSize(String size) {
		if (!enabled) {
			return;
		}

		Dom.setImportant(cursorOverlay.getElement().getStyle(), "font-size",
				size);
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void enable() {
		enabled = true;
		enableForTextField();
	}

	public void setHorizontalAlignment(HorizontalAlignment alignment) {
		cursorOverlay.setHorizontalAlignment(alignment);
	}

	public void unselectAll() {
		cursorOverlay.removeFakeSelection();
	}

	public boolean isSelected() {
		return cursorOverlay.hasFakeSelection();
	}

}
