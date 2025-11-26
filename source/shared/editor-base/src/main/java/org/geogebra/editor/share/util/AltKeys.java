/*
GeoGebra - Dynamic Mathematics for Schools
Copyright (c) GeoGebra GmbH, Altenbergerstr 69, 4040 Linz, Austria
https://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by
the Free Software Foundation.
*/

package org.geogebra.editor.share.util;

import java.util.HashMap;

public class AltKeys {

	private static HashMap<Character, String> lookupLower = null;
	private static HashMap<Character, String> lookupUpper = null;

	private static void init(boolean chromeApp) {
		lookupLower = new HashMap<>();
		lookupUpper = new HashMap<>();

		lookupLower.put('A', String.valueOf(Unicode.alpha));
		lookupUpper.put('A', String.valueOf(Unicode.Alpha));
		lookupLower.put('B', String.valueOf(Unicode.beta));
		lookupUpper.put('B', String.valueOf(Unicode.Beta));
		lookupLower.put('D', String.valueOf(Unicode.delta));
		lookupUpper.put('D', String.valueOf(Unicode.Delta));
		lookupLower.put('E', " " + Unicode.EULER_STRING + " ");
		lookupUpper.put('E', " " + Unicode.EULER_STRING + " ");
		lookupLower.put('F', String.valueOf(Unicode.phi_symbol));
		lookupUpper.put('F', String.valueOf(Unicode.Phi));
		lookupLower.put('G', String.valueOf(Unicode.gamma));
		lookupUpper.put('G', String.valueOf(Unicode.Gamma));
		lookupLower.put('I', " " + Unicode.IMAGINARY + " ");
		lookupUpper.put('I', " " + Unicode.IMAGINARY + " ");
		lookupLower.put('L', String.valueOf(Unicode.lambda));
		lookupUpper.put('L', String.valueOf(Unicode.Lambda));
		lookupLower.put('M', String.valueOf(Unicode.mu));
		lookupUpper.put('M', String.valueOf(Unicode.Mu));
		lookupLower.put('N', String.valueOf(Unicode.nu));
		lookupUpper.put('N', String.valueOf(Unicode.Nu));
		lookupLower.put('O', Unicode.DEGREE_STRING);
		lookupUpper.put('O', Unicode.DEGREE_STRING);
		lookupLower.put('P', " " + Unicode.pi + " ");
		lookupUpper.put('P', String.valueOf(Unicode.Pi));
		lookupLower.put('R', String.valueOf(Unicode.SQUARE_ROOT));
		lookupUpper.put('R', String.valueOf(Unicode.SQUARE_ROOT));
		lookupLower.put('S', String.valueOf(Unicode.sigma));
		lookupUpper.put('S', String.valueOf(Unicode.Sigma));
		lookupLower.put('T', String.valueOf(Unicode.theta));
		lookupUpper.put('T', String.valueOf(Unicode.Theta));
		lookupLower.put('U', String.valueOf(Unicode.INFINITY));
		lookupUpper.put('U', String.valueOf(Unicode.INFINITY));
		lookupLower.put('W', String.valueOf(Unicode.omega));
		lookupUpper.put('W', String.valueOf(Unicode.Omega));

		lookupLower.put('0', String.valueOf(Unicode.SUPERSCRIPT_0));
		lookupUpper.put('0', "}");
		lookupLower.put('1', String.valueOf(Unicode.SUPERSCRIPT_1));
		lookupLower.put('2', String.valueOf(Unicode.SUPERSCRIPT_2));
		lookupUpper.put('2', String.valueOf(Unicode.CURRENCY_EURO));
		lookupLower.put('3', String.valueOf(Unicode.SUPERSCRIPT_3));
		lookupUpper.put('3', String.valueOf(Unicode.LEFT_GUILLEMET));
		lookupLower.put('4', String.valueOf(Unicode.SUPERSCRIPT_4));
		lookupUpper.put('4', String.valueOf(Unicode.RIGHT_GUILLEMET));
		lookupLower.put('5', String.valueOf(Unicode.SUPERSCRIPT_5));
		lookupUpper.put('5', String.valueOf(Unicode.CURRENCY_POUND));
		lookupLower.put('6', String.valueOf(Unicode.SUPERSCRIPT_6));
		lookupLower.put('7', String.valueOf(Unicode.SUPERSCRIPT_7));
		lookupUpper.put('7', "\\");
		lookupLower.put('8', String.valueOf(Unicode.SUPERSCRIPT_8));
		lookupUpper.put('8', String.valueOf(Unicode.VECTOR_PRODUCT));
		lookupLower.put('9', String.valueOf(Unicode.SUPERSCRIPT_9));
		lookupUpper.put('9', "{");

		lookupUpper.put('*', String.valueOf(Unicode.VECTOR_PRODUCT));
		lookupLower.put('*', String.valueOf(Unicode.VECTOR_PRODUCT));

		lookupUpper.put('+', String.valueOf(Unicode.PLUSMINUS));
		lookupLower.put('+', String.valueOf(Unicode.XOR));

		lookupUpper.put(Unicode.e_GRAVE, "{"); // Italian keyboards
		lookupLower.put(Unicode.e_GRAVE, "["); // Italian keyboards

		lookupUpper.put(Unicode.e_ACUTE, "{"); // Italian keyboards
		lookupLower.put(Unicode.e_ACUTE, "["); // Italian keyboards

		// alt-/ for backslash (not on all keyboards eg Dutch)
		lookupLower.put('/', "\\");
		lookupUpper.put('=', String.valueOf(Unicode.XOR));
		lookupLower.put('=', String.valueOf(Unicode.XOR));

		lookupUpper.put('-', String.valueOf(Unicode.SUPERSCRIPT_MINUS));
		lookupLower.put('-', String.valueOf(Unicode.SUPERSCRIPT_MINUS));

		lookupUpper.put(',', String.valueOf(Unicode.LESS_EQUAL));
		lookupLower.put(',', String.valueOf(Unicode.LESS_EQUAL));

		lookupUpper.put('<', String.valueOf(Unicode.LESS_EQUAL));
		lookupLower.put('<', String.valueOf(Unicode.LESS_EQUAL));

		lookupUpper.put('.', String.valueOf(Unicode.GREATER_EQUAL));
		lookupLower.put('.', String.valueOf(Unicode.GREATER_EQUAL));

		lookupUpper.put('>', String.valueOf(Unicode.GREATER_EQUAL));
		lookupLower.put('>', String.valueOf(Unicode.GREATER_EQUAL));

		if (chromeApp) {

			// these keycodes also work in Safari 5.1.2 (Win 7), Firefox 12 (Win
			// 7) and Chrome 20 on Chromebook

			// on Chrome 18 (Win 7), Alt-Keypad* gives character 106
			lookupUpper.put((char) 106, String.valueOf(Unicode.VECTOR_PRODUCT));
			lookupLower.put((char) 106, String.valueOf(Unicode.VECTOR_PRODUCT));

			// on Chrome, Alt-Keypad+ gives character 107
			lookupUpper.put((char) 107, String.valueOf(Unicode.XOR));
			lookupLower.put((char) 107, String.valueOf(Unicode.XOR));

			// on Chrome, Alt-Keypad- gives character 109
			lookupUpper.put((char) 109, String.valueOf(Unicode.SUPERSCRIPT_MINUS));
			lookupLower.put((char) 109, String.valueOf(Unicode.SUPERSCRIPT_MINUS));

			// on Chrome, Alt-= gives character 187 (>>)
			lookupUpper.put((char) 187, String.valueOf(Unicode.NOTEQUAL));
			lookupLower.put((char) 187, String.valueOf(Unicode.NOTEQUAL));

			// on Chrome, Alt-, gives character 188 (1/4)
			lookupUpper.put((char) 188, String.valueOf(Unicode.LESS_EQUAL));
			lookupLower.put((char) 188, String.valueOf(Unicode.LESS_EQUAL));

			// on Chrome, Alt-- gives character 189 (1/2)
			lookupUpper.put((char) 189, String.valueOf(Unicode.SUPERSCRIPT_MINUS));
			lookupLower.put((char) 189, String.valueOf(Unicode.SUPERSCRIPT_MINUS));

			// on Chrome, Alt-. gives character 190 (3/4)
			lookupUpper.put((char) 190, String.valueOf(Unicode.GREATER_EQUAL));
			lookupLower.put((char) 190, String.valueOf(Unicode.GREATER_EQUAL));

			// on Chrome (mac), Alt u. gives character 85 (3/4)
			lookupUpper.put((char) 85, String.valueOf(Unicode.INFINITY));
			lookupLower.put((char) 85, String.valueOf(Unicode.INFINITY));

			// on Chrome (mac), Alt n. gives character 78 (3/4)
			lookupUpper.put((char) 78, String.valueOf(Unicode.Nu));
			lookupLower.put((char) 78, String.valueOf(Unicode.nu));
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

	/**
	 * checks if the typed character maps to a special character by checking whether
	 * it exists as a key in the hashmaps
	 * @param keyCode - key code without modifiers
	 * @param isShiftDown - determines if shift is down
	 * @param webApp - whether we run this in a browser
	 * @return returns true if the char is a key in the hashmaps
	 */
	public static Boolean isGeoGebraShortcut(int keyCode, boolean isShiftDown,
			boolean webApp) {
		if (lookupUpper == null) {
			init(webApp);
		}
		if (isShiftDown) {
			return AltKeys.lookupUpper.containsKey((char) keyCode);
		}
		if (keyCode >= 'a' && keyCode <= 'z') {
			return AltKeys.lookupLower.containsKey((char) (keyCode + 'A' - 'a'));
		}

		return AltKeys.lookupLower.containsKey((char) keyCode) || keyCode == 229;
	}
}
