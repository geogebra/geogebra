package org.geogebra.web.full.gui.dialog.text;

import org.geogebra.keyboard.web.KeyboardListener;
import org.geogebra.web.html5.gui.textbox.GTextBox;

import elemental2.dom.DomGlobal;

/**
 * Connector for keyboard and text input dialog
 */
public class TextEditPanelProcessing implements KeyboardListener {
	private final GeoTextEditor field;

	/**
	 * @param field
	 *            textbox
	 */
	public TextEditPanelProcessing(GeoTextEditor field) {
		this.field = field;
	}

	@Override
	public void setFocus(boolean focus) {
		if (field == null) {
			return;
		}

		field.setFocus(focus);
	}

	@Override
	public void onEnter() {
		field.newLine();
	}

	@Override
	public void onBackSpace() {
		field.onBackspace();
	}

	@Override
	public void insertString(String text) {
		insertAtEnd(text);
	}

	/**
	 * only for {@link GTextBox}
	 *
	 * @param text
	 *            text to be inserted
	 */
	private void insertAtEnd(String text) {
		field.insertElement(DomGlobal.document.createTextNode(text));
	}

	@Override
	public void onArrow(ArrowType type) {
		// solve later
	}

	@Override
	public boolean isSVCell() {
		return false;
	}

	@Override
	public void endEditing() {
		// not needed
	}

	@Override
	public GeoTextEditor getField() {
		return field;
	}

	@Override
	public void onKeyboardClosed() {
		// not needed
	}

	@Override
	public void ansPressed() {
		// not needed
	}

	@Override
	public boolean requestsAns() {
		return false;
	}
}

