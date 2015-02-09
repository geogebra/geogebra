package geogebra.web.util.keyboard;

import geogebra.html5.gui.inputfield.AutoCompleteTextFieldW;
import geogebra.html5.main.DrawEquationWeb;
import geogebra.web.gui.view.algebra.NewRadioButtonTreeItem;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;

/**
 * manages the processing of the different types of widgets that
 * {@link OnScreenKeyBoard} can work with
 */
public class TextFieldProcessing {

	private Widget field;
	private State state = State.empty;

	private enum State {
		empty, autoCompleteTextField, radioButtonTreeItem, other;
	}

	public void setField(Widget field) {
		this.field = field;
		if (field == null) {
			state = State.empty;
		} else if (field instanceof AutoCompleteTextFieldW) {
			state = State.autoCompleteTextField;
		} else if (field instanceof NewRadioButtonTreeItem) {
			state = State.radioButtonTreeItem;
		} else {
			state = State.other;
		}
	}

	public void setFocus(boolean focus) {
		switch (state) {
		case autoCompleteTextField:
			((AutoCompleteTextFieldW) field).setFocus(focus);
			break;
		case radioButtonTreeItem:
			((NewRadioButtonTreeItem) field).setFocus(focus);
			break;
		}
	}

	public void setKeyBoardUsed(boolean used) {
		if (state == State.autoCompleteTextField) {
			((AutoCompleteTextFieldW) field).setKeyBoardUsed(used);
		}
	}

	public void setKeyBoardModeText(boolean b) {
		if (state == State.autoCompleteTextField) {
			((AutoCompleteTextFieldW) field).setKeyBoardModeText(b);
		}
	}

	/**
	 * simulates an enter key event
	 */
	public void onEnter() {
		switch (state) {
		case autoCompleteTextField:
			NativeEvent event = Document.get().createKeyUpEvent(false, false,
			        false, false, 13);
			((AutoCompleteTextFieldW) field).getTextField().onBrowserEvent(
			        Event.as(event));
			break;
		case radioButtonTreeItem:
			// TODO workaround; will be removed
			DrawEquationWeb.newFormulaCreatedMathQuillGGB(
			        (NewRadioButtonTreeItem) field,
			        ((NewRadioButtonTreeItem) field).getText());
			((NewRadioButtonTreeItem) field).startEditing();
			break;
		}
	}

	/**
	 * simulates a backspace key event
	 */
	public void onBackSpace() {
		switch (state) {
		case autoCompleteTextField:
			((AutoCompleteTextFieldW) field).onBackSpace();
			break;
		case radioButtonTreeItem:
			// TODO
			break;
		}
	}

	public void insertString(String text) {
		switch (state) {
		case autoCompleteTextField:
			((AutoCompleteTextFieldW) field).insertString(text);
			break;
		case radioButtonTreeItem:
			((NewRadioButtonTreeItem) field).setFocus(true);
			((NewRadioButtonTreeItem) field).insertString(text);
			break;
		}
	}

}
