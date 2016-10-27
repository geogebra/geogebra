package org.geogebra.common.gui.inputfield;

import java.util.HashMap;

import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants;
import org.geogebra.common.util.Unicode;

public class AltKeys {

	public static HashMap<Character, String> LookupLower = null,
			lookupUpper = null;

	private static void init(boolean chromeApp) {
		LookupLower = new HashMap<Character, String>();
		lookupUpper = new HashMap<Character, String>();

		LookupLower.put('A', Unicode.alpha + "");
		lookupUpper.put('A', Unicode.Alpha + "");
		LookupLower.put('B', Unicode.beta + "");
		lookupUpper.put('B', Unicode.Beta + "");
		LookupLower.put('D', Unicode.delta + "");
		lookupUpper.put('D', Unicode.Delta + "");
		LookupLower.put('E', " " + Unicode.EULER_STRING + " ");
		lookupUpper.put('E', " " + Unicode.EULER_STRING + " ");
		LookupLower.put('F', Unicode.phi_symbol + "");
		lookupUpper.put('F', Unicode.Phi + "");
		LookupLower.put('G', Unicode.gamma + "");
		lookupUpper.put('G', Unicode.Gamma + "");
		LookupLower.put('I', " " + Unicode.IMAGINARY + " ");
		lookupUpper.put('I', " " + Unicode.IMAGINARY + " ");
		LookupLower.put('L', Unicode.lambda + "");
		lookupUpper.put('L', Unicode.Lambda + "");
		LookupLower.put('M', Unicode.mu + "");
		lookupUpper.put('M', Unicode.Mu + "");
		LookupLower.put('O', Unicode.DEGREE);
		lookupUpper.put('O', Unicode.DEGREE);
		LookupLower.put('P', " " + Unicode.pi + " ");
		lookupUpper.put('P', Unicode.Pi + "");
		LookupLower.put('R', Unicode.SQUARE_ROOT + "");
		lookupUpper.put('R', Unicode.SQUARE_ROOT + "");
		LookupLower.put('S', Unicode.sigma + "");
		lookupUpper.put('S', Unicode.Sigma + "");
		LookupLower.put('T', Unicode.theta + "");
		lookupUpper.put('T', Unicode.Theta + "");
		LookupLower.put('U', Unicode.INFINITY + "");
		lookupUpper.put('U', Unicode.INFINITY + "");
		LookupLower.put('W', Unicode.omega + "");
		lookupUpper.put('W', Unicode.Omega + "");

		LookupLower.put('0', Unicode.Superscript_0 + "");
		LookupLower.put('1', Unicode.Superscript_1 + "");
		LookupLower.put('2', Unicode.Superscript_2 + "");
		LookupLower.put('3', Unicode.Superscript_3 + "");
		LookupLower.put('4', Unicode.Superscript_4 + "");
		LookupLower.put('5', Unicode.Superscript_5 + "");
		LookupLower.put('6', Unicode.Superscript_6 + "");
		LookupLower.put('7', Unicode.Superscript_7 + "");
		LookupLower.put('8', Unicode.Superscript_8 + "");
		lookupUpper.put('8', ExpressionNodeConstants.strVECTORPRODUCT);
		LookupLower.put('9', Unicode.Superscript_9 + "");

		lookupUpper.put('*', ExpressionNodeConstants.strVECTORPRODUCT);
		LookupLower.put('*', ExpressionNodeConstants.strVECTORPRODUCT);

		lookupUpper.put('+', Unicode.PLUSMINUS);
		LookupLower.put('+', Unicode.PLUSMINUS);

		lookupUpper.put(Unicode.eGrave, "{"); // Italian keyboards
		LookupLower.put(Unicode.eGrave, "["); // Italian keyboards

		lookupUpper.put(Unicode.eAcute, "{"); // Italian keyboards
		LookupLower.put(Unicode.eAcute, "["); // Italian keyboards

		lookupUpper.put('=', Unicode.NOTEQUAL);
		LookupLower.put('=', Unicode.NOTEQUAL);

		lookupUpper.put('-', Unicode.Superscript_Minus + "");
		LookupLower.put('-', Unicode.Superscript_Minus + "");

		lookupUpper.put(',', Unicode.LESS_EQUAL + "");
		LookupLower.put(',', Unicode.LESS_EQUAL + "");

		lookupUpper.put('<', Unicode.LESS_EQUAL + "");
		LookupLower.put('<', Unicode.LESS_EQUAL + "");

		lookupUpper.put('.', Unicode.GREATER_EQUAL + "");
		LookupLower.put('.', Unicode.GREATER_EQUAL + "");

		lookupUpper.put('>', Unicode.GREATER_EQUAL + "");
		LookupLower.put('>', Unicode.GREATER_EQUAL + "");

		if (chromeApp) {

			// these keycodes also work in Safari 5.1.2 (Win 7), Firefox 12 (Win
			// 7) and Chrome 20 on Chromebook

			// on Chrome 18 (Win 7), Alt-Keypad* gives character 106
			lookupUpper.put((char) 106,
					ExpressionNodeConstants.strVECTORPRODUCT);
			LookupLower.put((char) 106,
					ExpressionNodeConstants.strVECTORPRODUCT);

			// on Chrome, Alt-Keypad+ gives character 107
			lookupUpper.put((char) 107, Unicode.PLUSMINUS);
			LookupLower.put((char) 107, Unicode.PLUSMINUS);

			// on Chrome, Alt-Keypad- gives character 109
			lookupUpper.put((char) 109, Unicode.Superscript_Minus + "");
			LookupLower.put((char) 109, Unicode.Superscript_Minus + "");

			// on Chrome, Alt-= gives character 187 (>>)
			lookupUpper.put((char) 187, Unicode.NOTEQUAL + "");
			LookupLower.put((char) 187, Unicode.NOTEQUAL + "");

			// on Chrome, Alt-, gives character 188 (1/4)
			lookupUpper.put((char) 188, Unicode.LESS_EQUAL + "");
			LookupLower.put((char) 188, Unicode.LESS_EQUAL + "");

			// on Chrome, Alt-- gives character 189 (1/2)
			lookupUpper.put((char) 189, Unicode.Superscript_Minus + "");
			LookupLower.put((char) 189, Unicode.Superscript_Minus + "");

			// on Chrome, Alt-. gives character 190 (3/4)
			lookupUpper.put((char) 190, Unicode.GREATER_EQUAL + "");
			LookupLower.put((char) 190, Unicode.GREATER_EQUAL + "");
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
			return AltKeys.LookupLower.get((char) (keyCode + 'A' - 'a'));
		}
		return AltKeys.LookupLower.get((char) keyCode);
	}

}
