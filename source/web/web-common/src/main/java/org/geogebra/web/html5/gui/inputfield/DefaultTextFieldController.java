/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
import org.gwtproject.user.client.ui.ValueBoxBase;

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
		textField.getTextField().getValueBox().getElement().getStyle()
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

	@Override
	public void clearSelection() {
		ValueBoxBase<String> valueBox = textField.getTextField().getValueBox();
		int start = textField.getText()
				.indexOf(valueBox.getSelectedText());
		int end = start + valueBox.getSelectionLength();
		// clear selection if there is one
		if (start != end) {
			int pos = textField.getCaretPosition();
			String oldText = textField.getText();
			String sb = oldText.substring(0, start) + oldText.substring(end);
			textField.setText(sb);
			if (pos < sb.length()) {
				textField.setCaretPosition(pos);
			}
		}

	}
}
