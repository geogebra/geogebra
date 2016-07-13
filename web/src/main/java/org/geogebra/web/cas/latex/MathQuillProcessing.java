package org.geogebra.web.cas.latex;

import java.util.HashSet;

import org.geogebra.common.main.GWTKeycodes;
import org.geogebra.common.main.KeyboardLocale;
import org.geogebra.common.util.Unicode;
import org.geogebra.web.keyboard.KeyboardConstants;
import org.geogebra.web.keyboard.KeyboardListener;
import org.geogebra.web.web.gui.view.algebra.EquationEditorListener;

public class MathQuillProcessing implements KeyboardListener {
	private EquationEditorListener field;
	private HashSet<String> needsLeftParenthesis = new HashSet<String>();

	public MathQuillProcessing(EquationEditorListener field) {
		initNeedsLeftParenthesis();
		this.field = field;
	}

	@Override
	public void setFocus(boolean focus) {
		if (focus) {
			field.setFocus(true, false);
			}

	}

	@Override
	public void onBackSpace() {
		field.keydown(BACKSPACE, false, false,
					false);
		}

	@Override
	public void onEnter() {

		field.keyup(ENTER, false, false, false);

	}

	@Override
	public void scrollCursorIntoView() {

		field.scrollCursorIntoView();

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

		// my on-screen keyboard has these! Hungarian
		// TODO: fix this by calling updateForNewLanguage!
		needsLeftParenthesis.add("tg");
		needsLeftParenthesis.add("sh");
		needsLeftParenthesis.add("ch");
		needsLeftParenthesis.add("th");
	}

	public void updateForNewLanguage(KeyboardLocale loc) {
		needsLeftParenthesis.clear();
		initNeedsLeftParenthesis();

		needsLeftParenthesis.add(loc.getFunction("sin"));
		needsLeftParenthesis.add(loc.getFunction("cos"));
		needsLeftParenthesis.add(loc.getFunction("tan"));
		needsLeftParenthesis.add(loc.getFunction("sinh"));
		needsLeftParenthesis.add(loc.getFunction("cosh"));
		needsLeftParenthesis.add(loc.getFunction("tanh"));
	}

	@Override
	public void insertString(String text) {

		if (text.equals(KeyboardConstants.A_POWER_X)) {
			if (field.getText().length() == 0) {
				return;
			}
			field.keypress('^', false, false, false, false);
		} else if (text.startsWith(Unicode.EULER_STRING)) {
			// this should be like this, in order to avoid confusion
			// with a possible variable name called "e"
			field.insertString(Unicode.EULER_STRING);
			// inserts: ^{}
			field.keypress('^', false, false, false, false);
		} else if (needsLeftParenthesis.contains(text)) {
			field.insertString(text);
			// inserts: () in Math mode, ( in Quotations
			// ((EquationEditorListener) field).keypress('(', false,
			// false,
			// false);
			// for Quotations, we need an additional ')' and backspace
			// but the timing of these events might makes these things
			// more indeterministic! so instead, preparing custom code
			// just for this use case...

			// some parameter should be added to mean also '(', ')' AND
			// a left key effect (without keydown event triggering)
			// instead of overriding ALT, CTRL, SHIFT, it is more clean
			// to add another parameter "more" so we can add custom code
			// then
			field.keypress('(', false, false, false, true);
			// the last true parameter means that this '(',
			// when executed, shall also make a ')' and a left key

			// if there is only one event happening, then we will
			// probably not have issues for indeterministic behaviour
			// that's why this is probably better than entering
			// "(" + ")" by keypress events...
		} else if (text.equals("nroot")) {
			field.insertString("nroo");
			field.keypress('t', false, false, true, false);
		} else if (text.equals("log")) {
			field.insertString("log_{10}");
			field.keypress('(', false, false, false, true);
		} else if (text.equals(KeyboardConstants.A_SQUARE)) {
			field.insertString(Unicode.Superscript_2 + "");
		} else if (keyPressNeeded(text)) {
			field.keypress(text.charAt(0), false, false, false,
					text.startsWith("(") || text.startsWith("|"));
		} else if (text.equals("abs")) {
			field.keypress('|', false, false, false, true);
		} else if (text.equals("quotes")) {
			field.keypress('"', false, false, false, true);
		} else {
			// if (text.length() == 1) {
			// ((EquationEditorListener) field).keypress(text.charAt(0),
			// false, false, false);
			// } else {
			field.insertString(text);
			// }
			// in case of keypress, we shall wait until the keypress
			// event
			// is really effective and only check for show suggestions
			// then...
			// but this is non-trivial unless we deal with it in the
			// keypress
			// event, not sure it's worth the work when we can also use
			// insertString in this case as well...
			field.showOrHideSuggestions();
		}

	}

	@Override
	public void onArrow(ArrowType type) {

		switch (type) {
		case left:
			field.keydown(GWTKeycodes.KEY_LEFT, false, false, false);
			break;
		case right:
			field.keydown(GWTKeycodes.KEY_RIGHT, false, false, false);
			break;
		}

	}

	@Override
	public boolean resetAfterEnter() {
		if (field instanceof InputTreeItem) {
			return false;
		}
		return field.resetAfterEnter();
		}

	public void setKeyBoardModeText(boolean text) {
		// TODO Auto-generated method stub

	}

	/**
	 * @param text
	 *            to insert
	 * @return {@code true} if the RadioButtonTreeItem needs a keyPress event.
	 */
	protected static boolean keyPressNeeded(String text) {
		return text.equals("/") || text.equals("_") || text.equals("$")
				|| text.equals(" ") || text.equals("|") || text.equals(",")
				|| text.equals("*") || text.startsWith("(") || text.equals(")")
				|| text.equals("[") || text.equals("]") || text.equals("{")
				|| text.equals("}") || text.equals(Unicode.SQUARE_ROOT + "")
				// allowing both syntaxes for * and / here
				|| text.equals(Unicode.MULTIPLY + "")
				|| text.equals(Unicode.DIVIDE);
	}
}
