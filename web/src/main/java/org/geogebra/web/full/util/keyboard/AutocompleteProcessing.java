package org.geogebra.web.full.util.keyboard;

import org.geogebra.keyboard.web.KeyboardListener;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.Event;

/**
 * Connector for keyboard and input boxes
 */
public class AutocompleteProcessing implements KeyboardListener {

	private AutoCompleteTextFieldW field;

	/**
	 * Connector for keyboartd and input boxes
	 * 
	 * @param field
	 *            input box
	 */
	public AutocompleteProcessing(AutoCompleteTextFieldW field) {
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
	public void setKeyBoardModeText(boolean b) {
		//
	}

	@Override
	public void onEnter() {
		NativeEvent event = Document.get().createKeyDownEvent(false, false,
				false, false, ENTER);
		field.getTextField().onBrowserEvent(Event.as(event));

		event = Document.get().createKeyPressEvent(false, false, false, false,
				ENTER);
		field.getTextField().onBrowserEvent(Event.as(event));

		event = Document.get().createKeyUpEvent(false, false, false, false,
				ENTER);
		field.getTextField().onBrowserEvent(Event.as(event));
	}

	@Override
	public void onBackSpace() {
		field.onBackSpace();
	}

	@Override
	public boolean isSVCell() {
		return field.getStyleName().indexOf("SpreadsheetEditorCell") >= 0;
	}

	@Override
	public void insertString(String text) {
		field.insertString(text);

		if ("nroot".equals(text)) {
			field.insertString("()");
			onArrow(ArrowType.left);
		}
	}

	@Override
	public void onArrow(ArrowType type) {
		if (type == ArrowType.left) {
			field.onArrowLeft();
		} else if (type == ArrowType.right) {
			field.onArrowRight();
		}
	}

	@Override
	public boolean resetAfterEnter() {
		return false;
	}

	@Override
	public void scrollCursorIntoView() {
		// TODO Auto-generated method stub
	}

	@Override
	public void endEditing() {
		field.endOnscreenKeyboardEditing();
	}

	@Override
	public AutoCompleteTextFieldW getField() {
		return field;
	}

	@Override
	public void onKeyboardClosed() {
		if (isSVCell()) {
			if (field.getApplication().getGuiManager() != null) {
				field.getApplication().getGuiManager().getSpreadsheetView()
						.setKeyboardEnabled(false);
			}
		}

	}

	@Override
	public void ansPressed() {
		/// not needed
	}

	@Override
	public boolean requestsAns() {
		return false;
	}
}
