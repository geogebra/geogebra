package org.geogebra.web.android.mathquill;

import java.util.HashSet;
import java.util.Set;

import org.geogebra.common.main.GWTKeycodes;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.Unicode;
import org.geogebra.web.web.util.keyboard.KeyboardConstants;
import org.geogebra.web.web.util.keyboardBase.TextFieldProcessing;

public class MathQuillInputProcessor implements TextFieldProcessing {

	private static final int BACKSPACE = 8;
	private static final int ENTER = '\r';
	private static final int LPARENTHESIS = '(';
	private static final int CIRCUMFLEX = '^';
	private static final int T_LOWER_CASE = 't';
	private static final int PIPE = '|';

	private Set<String> needsLeftParenthesis = new HashSet<String>();

	private MathQuillInput mathQuillInput;

	public MathQuillInputProcessor(MathQuillInput mathQuillInput) {
		initNeedsLeftParenthesis();
		this.mathQuillInput = mathQuillInput;
	}

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

	public void setFocus(boolean focus) {
		// no-op
	}

	public void onEnter() {
		mathQuillInput.enterPressed();
	}

	public void onBackSpace() {
		mathQuillInput.triggerKeyEvent("keydown", BACKSPACE, false, false,
				false);
	}

	public void onArrow(ArrowType type) {
		switch (type) {
		case left:
			mathQuillInput.triggerKeyEvent("keydown", GWTKeycodes.KEY_LEFT,
					false, false,
					false);
			break;

		default:
			mathQuillInput.triggerKeyEvent("keydown", GWTKeycodes.KEY_RIGHT,
					false, false,
					false);
			break;
		}

	}

	public void insertString(String text) {
		if (text.equals(KeyboardConstants.A_POWER_X)) {
			if (mathQuillInput.getText().length() == 0) {
				return;
			}
			mathQuillInput.keypress('^', false, false, false);
		} else if (text.startsWith(Unicode.EULER_STRING)) {
			// this should be like this, in order to avoid confusion
			// with a possible variable name called "e"
			mathQuillInput.insertString(Unicode.EULER_STRING);
			// inserts: ^{}
			mathQuillInput.keypress('^', false, false,
					false);
		} else if (needsLeftParenthesis.contains(text)) {
			mathQuillInput.insertString(text);
			// inserts: ()
			mathQuillInput.keypress('(', false, false,
					false);
		} else if (text.equals("nroot")) {
			mathQuillInput.insertString("nroo");
			mathQuillInput.keypress('t', false, false,
					true);
		} else if (text.equals("log")) {
			mathQuillInput.insertString("log_{10}");
			mathQuillInput.keypress('(', false, false,
					false);
		} else if (text.equals(KeyboardConstants.A_SQUARE)) {
			mathQuillInput.insertString(Unicode.Superscript_2 + "");
		} else if (keyPressNeeded(text)) {
			mathQuillInput.keypress(text.charAt(0),
					false, false, false);
		} else if (text.equals("abs")) {
			mathQuillInput.keypress('|', false, false,
					false);
		} else {
			mathQuillInput.insertString(text);
		}
	}

	private boolean keyPressNeeded(String text) {
		return text.equals("/") || text.equals("_") || text.equals("$")
				|| text.equals(" ") || text.equals("|") || text.equals(",")
				|| text.equals("*") || text.startsWith("(")
				|| text.equals(Unicode.SQUARE_ROOT + "");
	}

	public void scrollCursorIntoView() {
		// no-op
	}

	public boolean resetAfterEnter() {
		return false;
	}

}