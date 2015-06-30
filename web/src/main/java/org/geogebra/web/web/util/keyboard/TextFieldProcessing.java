package org.geogebra.web.web.util.keyboard;

import java.util.HashSet;

import org.geogebra.common.main.App;
import org.geogebra.common.main.GWTKeycodes;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.Unicode;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.textbox.GTextBox;
import org.geogebra.web.html5.gui.view.algebra.MathKeyboardListener;
import org.geogebra.web.web.gui.view.algebra.EquationEditorListener;
import org.geogebra.web.web.gui.view.algebra.NewRadioButtonTreeItem;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.Event;

/**
 * manages the processing of the different types of widgets that
 * {@link OnScreenKeyBoard} can work with
 */
public class TextFieldProcessing {

	/**
	 * arrow keys of the keyboard
	 */
	public enum ArrowType {
		/**
		 * arrow left key
		 */
		left,

		/**
		 * arrow right key
		 */
		right
	}

	private enum State {
		empty, autoCompleteTextField, gTextBox, equationEditorListener, newRadioButtonTreeItem, other;
	}

	/** ASCII */
	private static final int BACKSPACE = 8;
	private static final int ENTER = '\r'; // 13;
	private static final int LPARENTHESIS = '('; // 40;
	private static final int CIRCUMFLEX = '^';// 94;
	private static final int T_LOWER_CASE = 't'; // 116;
	private static final int PIPE = '|'; // 124;

	private MathKeyboardListener field;
	private State state = State.empty;
	private HashSet<String> needsLeftParenthesis = new HashSet<String>();

	public TextFieldProcessing() {
		initNeedsLeftParenthesis();
	}

	/**
	 * add the default Strings
	 */
	private void initNeedsLeftParenthesis() {
		needsLeftParenthesis.add("sin");
		needsLeftParenthesis.add("cos");
		needsLeftParenthesis.add("tan");
		needsLeftParenthesis.add("ln");
		needsLeftParenthesis.add("sinh");
		needsLeftParenthesis.add("cosh");
		needsLeftParenthesis.add("tanh");
		needsLeftParenthesis.add("arcsin");
		needsLeftParenthesis.add("arccos");
		needsLeftParenthesis.add("arctan");
	}

	/**
	 * change language specific notations
	 * 
	 * @param loc
	 */
	public void updateForNewLanguage(Localization loc) {
		needsLeftParenthesis.clear();
		initNeedsLeftParenthesis();

		needsLeftParenthesis.add(loc.getPlain("Function.sin"));
		needsLeftParenthesis.add(loc.getPlain("Function.cos"));
		needsLeftParenthesis.add(loc.getPlain("Function.tan"));
		needsLeftParenthesis.add(loc.getPlain("Function.sinh"));
		needsLeftParenthesis.add(loc.getPlain("Function.cosh"));
		needsLeftParenthesis.add(loc.getPlain("Function.tanh"));
	}

	/**
	 * @param field
	 *            the field that should receive all actions
	 */
	public void setField(MathKeyboardListener field) {
		this.field = field;
		if (field == null) {
			state = State.empty;
		} else if (field instanceof AutoCompleteTextFieldW) {
			state = State.autoCompleteTextField;
		} else if (field instanceof GTextBox) {
			state = State.gTextBox;
		} else if (field instanceof NewRadioButtonTreeItem) {
			state = State.newRadioButtonTreeItem;
		} else if (field instanceof EquationEditorListener) {
			state = State.equationEditorListener;
		} else {
			state = State.other;
		}
	}

	/**
	 * Focus/Blur the text field
	 * 
	 * @param focus
	 *            true: focus; false: blur
	 */
	public void setFocus(boolean focus) {
		switch (state) {
		case autoCompleteTextField:
			((AutoCompleteTextFieldW) field).setFocus(focus);
			break;
		case gTextBox:
			((GTextBox) field).setFocus(focus);
			break;
		case equationEditorListener:
		case newRadioButtonTreeItem:
			((EquationEditorListener) field).setFocus(true, false);
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
		App.debug("STATE" + state);
		switch (state) {
		case autoCompleteTextField:
			NativeEvent event = Document.get().createKeyDownEvent(false, false,
					false, false, ENTER);
			((AutoCompleteTextFieldW) field).getTextField().onBrowserEvent(
					Event.as(event));

			event = Document.get().createKeyPressEvent(false, false, false,
					false, ENTER);
			((AutoCompleteTextFieldW) field).getTextField().onBrowserEvent(
					Event.as(event));

			event = Document.get().createKeyUpEvent(false, false, false, false,
					ENTER);
			((AutoCompleteTextFieldW) field).getTextField().onBrowserEvent(
					Event.as(event));
			break;
		case gTextBox:
			NativeEvent event2 = Document.get().createKeyDownEvent(false,
					false, false, false, ENTER);
			((GTextBox) field).onBrowserEvent(Event.as(event2));
			break;
		case equationEditorListener:
		case newRadioButtonTreeItem:
			((EquationEditorListener) field).keyup(ENTER, false, false, false);
			break;
		}
	}

	public boolean resetAfterEnter() {
		return state == State.equationEditorListener
				&& ((EquationEditorListener) field).resetAfterEnter();
	}

	/**
	 * simulates a backspace key event
	 */
	public void onBackSpace() {
		switch (state) {
		case autoCompleteTextField:
			((AutoCompleteTextFieldW) field).onBackSpace();
			break;
		case gTextBox:
			int start = ((GTextBox) field).getCursorPos();
			int end = start + ((GTextBox) field).getSelectionLength();

			if (((GTextBox) field).getSelectionLength() < 1) {
				// nothing selected -> delete character before cursor
				end = start;
				start--;
			}

			if (start > 0) {
				// cursor not at the beginning of text -> delete something
				String oldText = ((GTextBox) field).getText();
				String newText = oldText.substring(0, start)
						+ oldText.substring(end);
				((GTextBox) field).setText(newText);
				((GTextBox) field).setCursorPos(start);
			}
			break;
		case equationEditorListener:
		case newRadioButtonTreeItem:
			((EquationEditorListener) field).keydown(BACKSPACE, false, false,
					false);
			break;
		}
	}

	/**
	 * simulates arrow events
	 * 
	 * @param type
	 *            {@link ArrowType}
	 */
	public void onArrow(ArrowType type) {
		switch (state) {
		case autoCompleteTextField:
			int caretPos = ((AutoCompleteTextFieldW) field).getCaretPosition();
			switch (type) {
			case left:
				if (caretPos > 0)
					((AutoCompleteTextFieldW) field)
							.setCaretPosition(caretPos - 1);
				break;
			case right:
				if (caretPos < ((AutoCompleteTextFieldW) field).getText()
						.length()) {
					((AutoCompleteTextFieldW) field)
							.setCaretPosition(caretPos + 1);
				}
				break;
			}
			break;
		case gTextBox:
			int cursorPos = ((GTextBox) field).getCursorPos();
			switch (type) {
			case left:
				if (cursorPos > 0)
					((GTextBox) field).setCursorPos(cursorPos - 1);
				break;
			case right:
				if (cursorPos < ((GTextBox) field).getText().length()) {
					((GTextBox) field).setCursorPos(cursorPos + 1);
				}
				break;
			}
			break;
		case equationEditorListener:
		case newRadioButtonTreeItem:
			switch (type) {
			case left:
				((EquationEditorListener) field).keydown(GWTKeycodes.KEY_LEFT,
						false, false, false);
				break;
			case right:
				((EquationEditorListener) field).keydown(GWTKeycodes.KEY_RIGHT,
						false, false, false);
				break;
			}
			break;
		}
	}

	/**
	 * Inserts the given text at the caret position
	 * 
	 * @param text
	 *            text to be inserted
	 */
	public void insertString(String text) {
		switch (state) {
		case autoCompleteTextField:
			((AutoCompleteTextFieldW) field).insertString(text);
			if (text.startsWith("(")) {
				// moves inside the brackets
				onArrow(ArrowType.left);
			} else if (text.equals(KeyboardConstants.A_POWER_X)) {
				((AutoCompleteTextFieldW) field).insertString("^");
			} else if (text.equals("nroot")) {
				((AutoCompleteTextFieldW) field).insertString("()");
				onArrow(ArrowType.left);
			}
			break;
		case gTextBox:
			insertAtEnd(text);
			break;
		case equationEditorListener:
		case newRadioButtonTreeItem:
			if (text.equals(KeyboardConstants.A_POWER_X)) {
				if (((EquationEditorListener) field).getText().length() == 0) {
					return;
				}
				((EquationEditorListener) field).keypress('^', false,
						false, false);
			} else if (text.startsWith(Unicode.EULER_STRING)) {
				// this should be like this, in order to avoid confusion
				// with a possible variable name called "e"
				((EquationEditorListener) field).insertString(Unicode.EULER_STRING);
				// inserts: ^{}
				((EquationEditorListener) field).keypress('^', false,
						false, false);
			} else if (needsLeftParenthesis.contains(text)) {
				((EquationEditorListener) field).insertString(text);
				// inserts: ()
				((EquationEditorListener) field).keypress('(', false, false,
						false);
			} else if (text.equals("nroot")) {
				((EquationEditorListener) field).insertString("nroo");
				((EquationEditorListener) field).keypress('t', false,
						false, true);
			} else if (text.equals("log")) {
				((EquationEditorListener) field).insertString("log_{10}");
				((EquationEditorListener) field).keypress('(', false, false,
						false);
			} else if (text.equals(KeyboardConstants.A_SQUARE)) {
				((EquationEditorListener) field)
						.insertString(Unicode.Superscript_2 + "");
			} else if (keyPressNeeded(text)) {
				((EquationEditorListener) field).keypress(text.charAt(0),
						false, false, false);
			} else if (text.equals("abs")) {
				((EquationEditorListener) field).keypress('|', false, false,
						false);
			} else if (text.equals("quotes")) {
				((EquationEditorListener) field).keypress('"', false, false,
						false);
			} else {
				//if (text.length() == 1) {
				//	((EquationEditorListener) field).keypress(text.charAt(0),
				//			false, false, false);
				//} else {
					((EquationEditorListener) field).insertString(text);
				//}
				// in case of keypress, we shall wait until the keypress event
				// is really effective and only check for show suggestions then...
				// but this is non-trivial unless we deal with it in the keypress
				// event, not sure it's worth the work when we can also use
				// insertString in this case as well...
				((EquationEditorListener) field).showOrHideSuggestions();
			}
			break;
		}
	}

	/**
	 * @param text
	 *            to insert
	 * @return {@code true} if the RadioButtonTreeItem needs a keyPress event.
	 */
	private static boolean keyPressNeeded(String text) {
		return text.equals("/") || text.equals("_") || text.equals("$")
				|| text.equals(" ") || text.equals("|") || text.equals(",")
				|| text.equals("*") || text.startsWith("(")
				|| text.equals(Unicode.SQUARE_ROOT + "");
	}

	/**
	 * only for {@link GTextBox}
	 * 
	 * @param text
	 */
	private void insertAtEnd(String text) {
		String oldText = ((GTextBox) field).getText();
		int caretPos = ((GTextBox) field).getCursorPos();

		String newText = oldText.substring(0, caretPos) + text
				+ oldText.substring(caretPos);
		((GTextBox) field).setText(newText);
		((GTextBox) field).setCursorPos(caretPos + text.length());
	}

	/**
	 * Method just used for RadioButtonTreeItem for now
	 */
	public void scrollCursorIntoView() {
		switch (state) {
		case newRadioButtonTreeItem:
		case equationEditorListener:
			((EquationEditorListener) field).scrollCursorIntoView();
			break;
		default:
			break;
		}
	}

	public Object getTextField() {
		return field;
	}
}
