package com.himamis.retex.editor.share.util;

import java.util.HashMap;

public class AltKeys {

	private static HashMap<Character, String> lookupLower = null;
	private static HashMap<Character, String> lookupUpper = null;

	private static void init(boolean chromeApp) {
		lookupLower = new HashMap<>();
		lookupUpper = new HashMap<>();

		lookupLower.put('A', Unicode.alpha + "");
		lookupUpper.put('A', Unicode.Alpha + "");
		lookupLower.put('B', Unicode.beta + "");
		lookupUpper.put('B', Unicode.Beta + "");
		lookupLower.put('D', Unicode.delta + "");
		lookupUpper.put('D', Unicode.Delta + "");
		lookupLower.put('E', " " + Unicode.EULER_STRING + " ");
		lookupUpper.put('E', " " + Unicode.EULER_STRING + " ");
		lookupLower.put('F', Unicode.phi_symbol + "");
		lookupUpper.put('F', Unicode.Phi + "");
		lookupLower.put('G', Unicode.gamma + "");
		lookupUpper.put('G', Unicode.Gamma + "");
		lookupLower.put('I', " " + Unicode.IMAGINARY + " ");
		lookupUpper.put('I', " " + Unicode.IMAGINARY + " ");
		lookupLower.put('L', Unicode.lambda + "");
		lookupUpper.put('L', Unicode.Lambda + "");
		lookupLower.put('M', Unicode.mu + "");
		lookupUpper.put('M', Unicode.Mu + "");
		lookupLower.put('N', Unicode.nu + "");
		lookupUpper.put('N', Unicode.Nu + "");
		lookupLower.put('O', Unicode.DEGREE_STRING);
		lookupUpper.put('O', Unicode.DEGREE_STRING);
		lookupLower.put('P', " " + Unicode.pi + " ");
		lookupUpper.put('P', Unicode.Pi + "");
		lookupLower.put('R', Unicode.SQUARE_ROOT + "");
		lookupUpper.put('R', Unicode.SQUARE_ROOT + "");
		lookupLower.put('S', Unicode.sigma + "");
		lookupUpper.put('S', Unicode.Sigma + "");
		lookupLower.put('T', Unicode.theta + "");
		lookupUpper.put('T', Unicode.Theta + "");
		lookupLower.put('U', Unicode.INFINITY + "");
		lookupUpper.put('U', Unicode.INFINITY + "");
		lookupLower.put('W', Unicode.omega + "");
		lookupUpper.put('W', Unicode.Omega + "");

		lookupLower.put('0', Unicode.SUPERSCRIPT_0 + "");
		lookupLower.put('1', Unicode.SUPERSCRIPT_1 + "");
		lookupLower.put('2', Unicode.SUPERSCRIPT_2 + "");
		lookupLower.put('3', Unicode.SUPERSCRIPT_3 + "");
		lookupLower.put('4', Unicode.SUPERSCRIPT_4 + "");
		lookupLower.put('5', Unicode.SUPERSCRIPT_5 + "");
		lookupLower.put('6', Unicode.SUPERSCRIPT_6 + "");
		lookupLower.put('7', Unicode.SUPERSCRIPT_7 + "");
		lookupLower.put('8', Unicode.SUPERSCRIPT_8 + "");
		lookupUpper.put('8', Unicode.VECTOR_PRODUCT + "");
		lookupLower.put('9', Unicode.SUPERSCRIPT_9 + "");

		lookupUpper.put('*', Unicode.VECTOR_PRODUCT + "");
		lookupLower.put('*', Unicode.VECTOR_PRODUCT + "");

		lookupUpper.put('+', Unicode.PLUSMINUS + "");
		lookupLower.put('+', Unicode.XOR + "");

		lookupUpper.put(Unicode.e_GRAVE, "{"); // Italian keyboards
		lookupLower.put(Unicode.e_GRAVE, "["); // Italian keyboards

		lookupUpper.put(Unicode.e_ACUTE, "{"); // Italian keyboards
		lookupLower.put(Unicode.e_ACUTE, "["); // Italian keyboards

		// alt-/ for backslash (not on all keyboards eg Dutch)
		lookupLower.put('/', "\\");

		lookupUpper.put('=', Unicode.XOR + "");
		lookupLower.put('=', Unicode.XOR + "");

		lookupUpper.put('-', Unicode.SUPERSCRIPT_MINUS + "");
		lookupLower.put('-', Unicode.SUPERSCRIPT_MINUS + "");

		lookupUpper.put(',', Unicode.LESS_EQUAL + "");
		lookupLower.put(',', Unicode.LESS_EQUAL + "");

		lookupUpper.put('<', Unicode.LESS_EQUAL + "");
		lookupLower.put('<', Unicode.LESS_EQUAL + "");

		lookupUpper.put('.', Unicode.GREATER_EQUAL + "");
		lookupLower.put('.', Unicode.GREATER_EQUAL + "");

		lookupUpper.put('>', Unicode.GREATER_EQUAL + "");
		lookupLower.put('>', Unicode.GREATER_EQUAL + "");

		if (chromeApp) {

			// these keycodes also work in Safari 5.1.2 (Win 7), Firefox 12 (Win
			// 7) and Chrome 20 on Chromebook

			// on Chrome 18 (Win 7), Alt-Keypad* gives character 106
			lookupUpper.put((char) 106, Unicode.VECTOR_PRODUCT + "");
			lookupLower.put((char) 106, Unicode.VECTOR_PRODUCT + "");

			// on Chrome, Alt-Keypad+ gives character 107
			lookupUpper.put((char) 107, Unicode.XOR + "");
			lookupLower.put((char) 107, Unicode.XOR + "");

			// on Chrome, Alt-Keypad- gives character 109
			lookupUpper.put((char) 109, Unicode.SUPERSCRIPT_MINUS + "");
			lookupLower.put((char) 109, Unicode.SUPERSCRIPT_MINUS + "");

			// on Chrome, Alt-= gives character 187 (>>)
			lookupUpper.put((char) 187, Unicode.NOTEQUAL + "");
			lookupLower.put((char) 187, Unicode.NOTEQUAL + "");

			// on Chrome, Alt-, gives character 188 (1/4)
			lookupUpper.put((char) 188, Unicode.LESS_EQUAL + "");
			lookupLower.put((char) 188, Unicode.LESS_EQUAL + "");

			// on Chrome, Alt-- gives character 189 (1/2)
			lookupUpper.put((char) 189, Unicode.SUPERSCRIPT_MINUS + "");
			lookupLower.put((char) 189, Unicode.SUPERSCRIPT_MINUS + "");

			// on Chrome, Alt-. gives character 190 (3/4)
			lookupUpper.put((char) 190, Unicode.GREATER_EQUAL + "");
			lookupLower.put((char) 190, Unicode.GREATER_EQUAL + "");
		}

	}

	/**
	 * check for eg alt-a for alpha check for eg alt-shift-a for upper case
	 * alpha
	 * 
	 * @param keyCode
	 *            Key code without modifiers.
	 * @param isShiftDown
	 *            Determines if shift is down,
	 * @param webApp
	 *            whether we run this in a browser
	 * @return The "alpha-string" ie the symbols.
	 */
	public static String getAltSymbols(int keyCode, boolean isShiftDown,
			boolean webApp) {
		if (lookupUpper == null) {
			init(webApp);
		}
		if (isShiftDown) {
			return AltKeys.lookupUpper.get((char) keyCode);
		}
		if (keyCode >= 'a' && keyCode <= 'z') {
			return AltKeys.lookupLower.get((char) (keyCode + 'A' - 'a'));
		}
		return AltKeys.lookupLower.get((char) keyCode);
	}

}
