package geogebra.web.util.keyboard;

import geogebra.html5.gui.inputfield.AutoCompleteTextFieldW;
import geogebra.html5.gui.textbox.GTextBox;
import geogebra.web.gui.view.algebra.NewRadioButtonTreeItem;
import geogebra.web.gui.view.algebra.RadioButtonTreeItem;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;

/**
 * manages the processing of the different types of widgets that
 * {@link OnScreenKeyBoard} can work with
 */
public class TextFieldProcessing {

	public enum ArrowType {
		left, right
	}

	private enum State {
		empty, autoCompleteTextField, gTextBox, radioButtonTreeItem, newRadioButtonTreeItem, other;
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

	private Widget field;
	private State state = State.empty;
	private ArrayList<String> needsLbrace = new ArrayList<String>();
	/** contains the unicode string for the specific letter with acute accent */
	private HashMap<String, String> accentAcute = new HashMap<String, String>();
	/** contains the unicode string for the specific letter with grave accent */
	private HashMap<String, String> accentGrave = new HashMap<String, String>();
	/** contains the unicode string for the specific letter with caron accent */
	private HashMap<String, String> accentCaron = new HashMap<String, String>();
	/** contains the unicode string for the specific letter with circumflex */
	private HashMap<String, String> accentCircumflex = new HashMap<String, String>();
	private String accent;

	/** needed for accents */
	private boolean accentWaiting = false;

	public TextFieldProcessing() {
		initNeedsLbrace();
		initAccentAcuteLetters();
		initAccentGraveLetters();
		initAccentCaronLetters();
		initAccentCircumflexLetters();
	}

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

	private void initAccentAcuteLetters() {
		accentAcute.put("a", "\u00e1");
		accentAcute.put("A", "\u00c1");
		accentAcute.put("e", "\u00e9");
		accentAcute.put("E", "\u00C9");
		accentAcute.put("i", "\u00ed");
		accentAcute.put("I", "\u00cd");
		accentAcute.put("l", "\u013A");
		accentAcute.put("L", "\u0139");
		accentAcute.put("o", "\u00f3");
		accentAcute.put("O", "\u00d3");
		accentAcute.put("r", "\u0155");
		accentAcute.put("R", "\u0154");
		accentAcute.put("u", "\u00fa");
		accentAcute.put("U", "\u00da");
		accentAcute.put("y", "\u00fd");
		accentAcute.put("Y", "\u00dd");
	}

	private void initAccentGraveLetters() {
		accentGrave.put("a", "\u00e0");
		accentGrave.put("A", "\u00c0");
		accentGrave.put("e", "\u00e8");
		accentGrave.put("E", "\u00C8");
		accentGrave.put("i", "\u00ec");
		accentGrave.put("I", "\u00cc");
		accentGrave.put("o", "\u00f2");
		accentGrave.put("O", "\u00d2");
		accentGrave.put("u", "\u00f9");
		accentGrave.put("U", "\u00d9");
	}

	private void initAccentCaronLetters() {
		accentCaron.put("c", "\u010d");
		accentCaron.put("C", "\u010c");
		accentCaron.put("d", "\u010F");
		accentCaron.put("D", "\u010e");
		accentCaron.put("e", "\u011b");
		accentCaron.put("E", "\u011A");
		accentCaron.put("l", "\u013E");
		accentCaron.put("L", "\u013D");
		accentCaron.put("n", "\u0148");
		accentCaron.put("N", "\u0147");
		accentCaron.put("r", "\u0159");
		accentCaron.put("R", "\u0158");
		accentCaron.put("s", "\u0161");
		accentCaron.put("S", "\u0160");
		accentCaron.put("t", "\u0165");
		accentCaron.put("T", "\u0164");
		accentCaron.put("z", "\u017e");
		accentCaron.put("Z", "\u017d");
	}

	private void initAccentCircumflexLetters() {
		accentCircumflex.put("a", "\u00e2");
		accentCircumflex.put("A", "\u00c2");
		accentCircumflex.put("e", "\u00ea");
		accentCircumflex.put("E", "\u00Ca");
		accentCircumflex.put("i", "\u00ee");
		accentCircumflex.put("I", "\u00ce");
		accentCircumflex.put("o", "\u00f4");
		accentCircumflex.put("O", "\u00d4");
		accentCircumflex.put("u", "\u00fb");
		accentCircumflex.put("U", "\u00db");
	}

	public void setField(Widget field) {
		this.field = field;
		if (field == null) {
			state = State.empty;
		} else if (field instanceof AutoCompleteTextFieldW) {
			state = State.autoCompleteTextField;
		} else if (field instanceof GTextBox) {
			state = State.gTextBox;
		} else if (field instanceof NewRadioButtonTreeItem) {
			state = State.newRadioButtonTreeItem;
		} else if (field instanceof RadioButtonTreeItem) {
			state = State.radioButtonTreeItem;
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
		case radioButtonTreeItem:
		case newRadioButtonTreeItem:
			((RadioButtonTreeItem) field).setFocus(true);
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
			NativeEvent event = Document.get().createKeyDownEvent(false, false,
					false, false, ENTER);
			((AutoCompleteTextFieldW) field).getTextField().onBrowserEvent(
					Event.as(event));

			event = Document.get().createKeyPressEvent(false,
					false, false, false, ENTER);
			((AutoCompleteTextFieldW) field).getTextField().onBrowserEvent(
					Event.as(event));

			event = Document.get().createKeyUpEvent(false, false,
					false, false, ENTER);
			((AutoCompleteTextFieldW) field).getTextField().onBrowserEvent(
					Event.as(event));
			break;
		case gTextBox:
			NativeEvent event2 = Document.get().createKeyDownEvent(false,
			        false, false, false, ENTER);
			((GTextBox) field).onBrowserEvent(Event.as(event2));
			break;
		case radioButtonTreeItem:
		case newRadioButtonTreeItem:
			((RadioButtonTreeItem) field).keyup(ENTER, false, false, false);
			break;
		}
	}

	/**
	 * remember clicked accent
	 * 
	 * @param ac
	 *            which accent (caron, acute)
	 */
	public void onAccent(String ac) {
		this.accent = ac;
		if (accentWaiting) {
			// if accent was clicked twice, two accent have to be inserted
			insertString(this.accent);
		} else {
			accentWaiting = true;
		}
	}

	public boolean resetAfterEnter() {
		return state == State.radioButtonTreeItem;
	}

	/**
	 * simulates a backspace key event
	 */
	public void onBackSpace() {
		accentWaiting = false;
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
		case radioButtonTreeItem:
		case newRadioButtonTreeItem:
			((RadioButtonTreeItem) field).keydown(BACKSPACE, false, false,
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
		case radioButtonTreeItem:
		case newRadioButtonTreeItem:
			switch (type) {
			case left:
				((RadioButtonTreeItem) field).keydown(LEFT_ARROW, false, false,
				        false);
				break;
			case right:
				((RadioButtonTreeItem) field).keydown(RIGHT_ARROW, false,
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
		if (accentWaiting) {
			insertAccent(text);
			return;
		}

		switch (state) {
		case autoCompleteTextField:
			((AutoCompleteTextFieldW) field).insertString(text);
			if (text.startsWith("(")) {
				// moves inside the brackets
				onArrow(ArrowType.left);
			} else if (text.equals(KeyboardConstants.X_POWER_Y)) {
				((AutoCompleteTextFieldW) field).insertString("^");
			} else if (text.equals("nroot")) {
				((AutoCompleteTextFieldW) field).insertString("()");
				onArrow(ArrowType.left);
			}

			break;
		case gTextBox:
			insertAtEnd(text);
			break;
		case radioButtonTreeItem:
		case newRadioButtonTreeItem:
			if (text.equals(KeyboardConstants.X_POWER_Y)) {
				if (((RadioButtonTreeItem) field).getText().length() == 0) {
					return;
				}
				((RadioButtonTreeItem) field).keypress(CIRCUMFLEX, false,
				        false, false);
			} else if (text.startsWith(KeyboardConstants.EULER)) {
				((RadioButtonTreeItem) field)
				        .insertString(KeyboardConstants.EULER);
				// inserts: ^{}
				((RadioButtonTreeItem) field).keypress(CIRCUMFLEX, false,
						false, false);
			} else if (needsLbrace.contains(text)) {
				((RadioButtonTreeItem) field).insertString(text);
				// inserts: ()
				((RadioButtonTreeItem) field).keypress(LBRACE, false, false,
						false);
			} else if (text.equals("nroot")) {
				((RadioButtonTreeItem) field).insertString("nroo");
				((RadioButtonTreeItem) field).keypress(T_LOWER_CASE, false,
				        false, true);
			} else if (text.equals("log")) {
				((RadioButtonTreeItem) field).insertString("log_{10}");
				((RadioButtonTreeItem) field).keypress(LBRACE, false, false,
				        false);
			} else if (text.equals(KeyboardConstants.X_SQUARE)) {
				((RadioButtonTreeItem) field)
				        .insertString(KeyboardConstants.SQUARE);
			} else if (keyPressNeeded(text)) {
				((RadioButtonTreeItem) field).keypress(
				// text.codePointAt is the same as text.charAt for low ranges
				// but I think String.fromCharCode will wait for Unicode int
				        text.codePointAt(0),
				        false, false,
						false);
			} else if (text.equals("abs")) {
				((RadioButtonTreeItem) field).keypress(PIPE, false, false,
						false);
			} else {
				((RadioButtonTreeItem) field).insertString(text);
				((RadioButtonTreeItem) field).popupSuggestions();
			}
			break;
		}
	}

	/**
	 * @param text
	 *            to insert
	 * @return {@code true} if the RadioButtonTreeItem needs a keyPress event.
	 */
    private boolean keyPressNeeded(String text) {
	    return text.equals("/") || text.equals("_") || text.equals("$")
	            || text.equals(" ") || text.equals("|") || text.equals(",")
		        || text.equals("*") || text.startsWith("(")
		        || text.equals(KeyboardConstants.SQUARE_ROOT);
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
	 * @param text
	 */
    private void insertAccent(String text) {
		if (this.accent.equals(KeyboardConstants.ACCENT_ACUTE)
		        && accentAcute.containsKey(text)) {
			insertFrom(accentAcute, text);
		} else if (this.accent.equals(KeyboardConstants.ACCENT_CARON)
		        && accentCaron.containsKey(text)) {
			insertFrom(accentCaron, text);
		} else if (this.accent.equals(KeyboardConstants.ACCENT_GRAVE)
		        && accentGrave.containsKey(text)) {
			insertFrom(accentGrave, text);
		} else if (this.accent.equals(KeyboardConstants.ACCENT_CIRCUMFLEX)
		        && accentCircumflex.containsKey(text)) {
			insertFrom(accentCircumflex, text);
		} else {
			switch (state) {
			case autoCompleteTextField:
				((AutoCompleteTextFieldW) field).insertString(accent);
				((AutoCompleteTextFieldW) field).insertString(text);
				break;
			case gTextBox:
				insertAtEnd(accent);
				insertAtEnd(text);
				break;
			case radioButtonTreeItem:
			case newRadioButtonTreeItem:
				((RadioButtonTreeItem) field).insertString(accent);
				((RadioButtonTreeItem) field).keypress(text.charAt(0), false,
				        false, false);
				break;
			}
		}
	    accentWaiting = false;
    }

	/**
	 * inserts the correct unicode for a combined letter with accents
	 * 
	 * @param accents
	 *            which accent (caron, acute, grave or circumflex)
	 * @param text
	 */
	private void insertFrom(HashMap<String, String> accents, String text) {
		switch (state) {
		case autoCompleteTextField:
			((AutoCompleteTextFieldW) field).insertString(accents.get(text));
			break;
		case gTextBox:
			insertAtEnd(accents.get(text));
			break;
		case radioButtonTreeItem:
		case newRadioButtonTreeItem:
			((RadioButtonTreeItem) field).insertString(accents.get(text));
			break;
		}
	}
}
