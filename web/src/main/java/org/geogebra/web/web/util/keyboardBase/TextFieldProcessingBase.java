package org.geogebra.web.web.util.keyboardBase;

import java.util.HashSet;

import org.geogebra.common.main.Localization;
import org.geogebra.common.util.Unicode;
import org.geogebra.web.html5.gui.textbox.GTextBox;
import org.geogebra.web.html5.gui.view.algebra.MathKeyboardListener;
import org.geogebra.web.web.gui.view.algebra.EquationEditorListener;
import org.geogebra.web.web.gui.view.algebra.NewRadioButtonTreeItem;
import org.geogebra.web.web.util.keyboard.KeyboardConstants;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.Event;

/**
 * manages the processing of the different types of widgets that
 * {@link OnScreenKeyBoardBase} can work with
 */
public class TextFieldProcessingBase {

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
		empty, gTextBox, equationEditorListener, newRadioButtonTreeItem, other;
	}

	/** ASCII */
	private static final int BACKSPACE = 8;
	private static final int ENTER = 13;
	private static final int LBRACE = 40;
	private static final int CIRCUMFLEX = 94;
	private static final int T_LOWER_CASE = 116;
	private static final int PIPE = 124;

	/** Javascript char codes */
	private static final int LEFT_ARROW = 37;
	private static final int RIGHT_ARROW = 39;

	private MathKeyboardListener field;
	private State state = State.empty;
	private HashSet<String> needsLbrace = new HashSet<String>();

	public TextFieldProcessingBase() {
		initNeedsLbrace();
	}

	/**
	 * add the default Strings
	 */
	private void initNeedsLbrace() {
		needsLbrace.add("sin");
		needsLbrace.add("cos");
		needsLbrace.add("tan");
		needsLbrace.add("ln");
		needsLbrace.add("sinh");
		needsLbrace.add("cosh");
		needsLbrace.add("tanh");
		needsLbrace.add("arcsin");
		needsLbrace.add("arccos");
		needsLbrace.add("arctan");
	}

	/**
	 * change language specific notations
	 * 
	 * @param loc
	 */
	public void updateForNewLanguage(Localization loc) {
		needsLbrace.clear();
		initNeedsLbrace();

		needsLbrace.add(loc.getPlain("Function.sin"));
		needsLbrace.add(loc.getPlain("Function.cos"));
		needsLbrace.add(loc.getPlain("Function.tan"));
		needsLbrace.add(loc.getPlain("Function.sinh"));
		needsLbrace.add(loc.getPlain("Function.cosh"));
		needsLbrace.add(loc.getPlain("Function.tanh"));
	}

	/**
	 * @param field
	 *            the field that should receive all actions
	 */
	public void setField(MathKeyboardListener field) {
		this.field = field;
		if (field == null) {
			state = State.empty;
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
		case gTextBox:
			((GTextBox) field).setFocus(focus);
			break;
		case equationEditorListener:
		case newRadioButtonTreeItem:
			((EquationEditorListener) field).setFocus(true);
			break;
		}
	}



	

	/**
	 * simulates an enter key event
	 */
	public void onEnter() {
		switch (state) {
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
		return state == State.equationEditorListener;
	}

	/**
	 * simulates a backspace key event
	 */
	public void onBackSpace() {
		switch (state) {
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
				((EquationEditorListener) field).keydown(LEFT_ARROW, false,
						false, false);
				break;
			case right:
				((EquationEditorListener) field).keydown(RIGHT_ARROW, false,
						false, false);
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
		case gTextBox:
			insertAtEnd(text);
			break;
		case equationEditorListener:
		case newRadioButtonTreeItem:
			if (text.equals(KeyboardConstants.A_POWER_X)) {
				if (((EquationEditorListener) field).getText().length() == 0) {
					return;
				}
				((EquationEditorListener) field).keypress(CIRCUMFLEX, false,
						false, false);
			} else if (text.startsWith(Unicode.EULER_GAMMA_STRING)) {
				((EquationEditorListener) field).insertString("e");
				// inserts: ^{}
				((EquationEditorListener) field).keypress(CIRCUMFLEX, false,
						false, false);
			} else if (needsLbrace.contains(text)) {
				((EquationEditorListener) field).insertString(text);
				// inserts: ()
				((EquationEditorListener) field).keypress(LBRACE, false, false,
						false);
			} else if (text.equals("nroot")) {
				((EquationEditorListener) field).insertString("nroo");
				((EquationEditorListener) field).keypress(T_LOWER_CASE, false,
						false, true);
			} else if (text.equals("log")) {
				((EquationEditorListener) field).insertString("log_{10}");
				((EquationEditorListener) field).keypress(LBRACE, false, false,
						false);
			} else if (text.equals(KeyboardConstants.A_SQUARE)) {
				((EquationEditorListener) field)
						.insertString(Unicode.Superscript_2 + "");
			} else if (keyPressNeeded(text)) {
				((EquationEditorListener) field).keypress(
				// text.codePointAt is the same as text.charAt for low ranges
				// but I think String.fromCharCode will wait for Unicode int
						text.codePointAt(0), false, false, false);
			} else if (text.equals("abs")) {
				((EquationEditorListener) field).keypress(PIPE, false, false,
						false);
			} else {
				((EquationEditorListener) field).insertString(text);
				((EquationEditorListener) field).popupSuggestions();
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
				|| text.equals(Unicode.SQUARE_ROOT);
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
