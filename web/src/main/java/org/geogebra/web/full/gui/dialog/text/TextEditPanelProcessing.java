package org.geogebra.web.full.gui.dialog.text;

import org.geogebra.keyboard.web.KeyboardListener;
import org.geogebra.web.html5.gui.textbox.GTextBox;
import org.gwtproject.dom.client.Document;
import org.gwtproject.dom.client.NativeEvent;
import org.gwtproject.user.client.Event;

/**
 * Connector for keyboard and text input dialog
 */
public class TextEditPanelProcessing implements KeyboardListener {
	private TextEditPanel field;

	/**
	 * @param field
	 *            textbox
	 */
	public TextEditPanelProcessing(TextEditPanel field) {
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
		NativeEvent event2 = Document.get().createKeyDownEvent(false, false,
				false, false, ENTER);
		field.onBrowserEvent(Event.as(event2));
	}

	@Override
	public void onBackSpace() {
		String oldText = field.getTextArea().getText();
		String newText = oldText.substring(0, oldText.length() - 1);
		field.setText(newText);
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
		String oldText = field.getTextArea().getText();
		String newText = oldText + text;
		field.setText(newText);
	}

	@Override
	public void onArrow(ArrowType type) {
		// solve later
	}

	@Override
	public boolean resetAfterEnter() {
		// overridden for RTI
		return false;
	}

	@Override
	public void setKeyBoardModeText(boolean text) {
		// overridden for RTI
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
	public TextEditPanel getField() {
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

