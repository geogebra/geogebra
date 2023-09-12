package org.geogebra.web.html5.gui.inputfield;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.kernel.geos.properties.HorizontalAlignment;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.keyboard.KeyboardManagerInterface;
import org.gwtproject.dom.style.shared.TextAlign;
import org.gwtproject.event.dom.client.KeyDownEvent;
import org.gwtproject.event.dom.client.KeyPressEvent;

/**
 * Default implementation of TextFieldController.
 */
public final class DefaultTextFieldController implements TextFieldController {
	private final AutoCompleteTextFieldW textField;

	public DefaultTextFieldController(AutoCompleteTextFieldW textField) {
		this.textField = textField;
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

	@Override
	public void update() {
		// nothing to do.
	}

	@Override
	public void selectAll() {
		textField.getTextField().getValueBox().selectAll();
	}

	@Override
	public void addCursor() {
		// nothing to do.
	}

	@Override
	public void removeCursor() {
		// nothing to do.
	}

	@Override
	public void setFont(GFont font) {
		String size = font.getSize() + "px";
		Dom.setImportant(textField.getInputElement().getStyle(), "font-size", size);

	}

	@Override
	public void setHorizontalAlignment(HorizontalAlignment alignment) {
		textField.getInputElement().getStyle().setTextAlign(textAlignToCssAlign(alignment));
	}

	private TextAlign textAlignToCssAlign(HorizontalAlignment alignment) {
		switch (alignment) {
		default:
		case LEFT:
			return TextAlign.LEFT;
		case CENTER:
			return TextAlign.CENTER;
		case RIGHT:
			return TextAlign.RIGHT;
		}
	}

	@Override
	public void unselectAll() {
		// nothing to do.
	}


	@Override
	public void setForegroundColor(GColor color) {
		textField.getElement().getStyle()
				.setColor(GColor.getColorString(color));
	}

	@Override
	public void handleKeyboardEvent(KeyDownEvent e) {
		// nothing to do.
	}

	@Override
	public boolean shouldBeKeyPressInserted(KeyPressEvent event) {
		return false;
	}

	@Override
	public int getSelectionStart() {
		return textField.getTextField().getValueBox().getCursorPos();
	}

	@Override
	public int getSelectionEnd() {
		return getSelectionStart()
				+ textField.getTextField().getValueBox().getSelectionLength();
	}
}
