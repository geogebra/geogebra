package org.geogebra.web.html5.gui.inputfield;

import org.geogebra.common.kernel.geos.properties.HorizontalAlignment;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.FlowPanel;

import elemental2.dom.DomGlobal;

public class CursorOverlayController {

	private final AppW app;
	private final AutoCompleteTextFieldW textField;
	private final FlowPanel main;
	private CursorOverlay cursorOverlay;
	private boolean hadSelection;
	private boolean enabled;

	public CursorOverlayController(AppW app, AutoCompleteTextFieldW textField, FlowPanel main) {
		this.app = app;
		this.textField = textField;
		this.main = main;
		enabled = false;
	}

	private void enableForTextField() {
		cursorOverlay = new CursorOverlay();
		textField.addFocusHandler(evt -> addDummyCursor());
		textField.addBlurHandler(evt -> removeDummyCursor());
		DomGlobal.setInterval(event -> update(), 200);
		textField.updateInputBoxAlign();
	}

	public void update() {
		if (!enabled) {
			return;
		}

		if (cursorOverlay.isAttached()) {
			toggleOverlay();
		}
	}

	private void toggleOverlay() {
		if (!hadSelection && hasSelection()) {
			cursorOverlay.removeFromParent();
			main.removeStyleName("withCursorOverlay");
			hadSelection = true;
		} else if (!hadSelection) {
			if (!cursorOverlay.isAttached()) {
				add();
			}
			hadSelection = false;
		}
		cursorOverlay.update(textField.getCursorPos(), textField.getText());
	}
	private boolean hasSelection() {
		return !textField.getTextField().getValueBox().getSelectedText().isEmpty();
	}

	private void enableCursorOverlay() {
	}

	public void addDummyCursor() {
		if (!enabled) {
			return;
		}

		add();
		update();
	}

	void add() {
		if (!enabled) {
			return;
		}

		main.add(cursorOverlay);
		main.addStyleName("withCursorOverlay");
		app.showKeyboard(textField, true);
	}

	public int removeDummyCursor() {
		if (!enabled) {
			return -1;
		}

		// check for isAttached to avoid infinite recursion
		if (cursorOverlay.isAttached()) {
			cursorOverlay.removeFromParent();
			main.removeStyleName("withCursorOverlay");
			CursorOverlay.hideKeyboard(app);
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
		if (!enabled) {
			return;
		}

		cursorOverlay.getElement().getStyle().setProperty("justifyContent",
				alignment.toString());
	}
}
