/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.editor.share.util;

/**
 * Enumeration of all available keys. Provides mapping to integer codes used by JVM and JS/GWT.
 */
public enum KeyCodes {
	A(JavaKeyCodes.VK_A),

	B(JavaKeyCodes.VK_B),

	C(JavaKeyCodes.VK_C),

	D(JavaKeyCodes.VK_D),

	E(JavaKeyCodes.VK_E),

	F(JavaKeyCodes.VK_F),

	G(JavaKeyCodes.VK_G),

	H(JavaKeyCodes.VK_H),

	I(JavaKeyCodes.VK_I),

	J(JavaKeyCodes.VK_J),

	K(JavaKeyCodes.VK_K),

	L(JavaKeyCodes.VK_L),

	M(JavaKeyCodes.VK_M),

	N(JavaKeyCodes.VK_N),

	O(JavaKeyCodes.VK_O),

	P(JavaKeyCodes.VK_P),

	Q(JavaKeyCodes.VK_Q),

	R(JavaKeyCodes.VK_R),

	S(JavaKeyCodes.VK_S),

	T(JavaKeyCodes.VK_T),

	U(JavaKeyCodes.VK_U),

	V(JavaKeyCodes.VK_V),

	W(JavaKeyCodes.VK_W),

	X(JavaKeyCodes.VK_X),

	Y(JavaKeyCodes.VK_Y),

	Z(JavaKeyCodes.VK_Z),

	F1(JavaKeyCodes.VK_F1),

	F2(JavaKeyCodes.VK_F2),

	F3(JavaKeyCodes.VK_F3),

	F4(JavaKeyCodes.VK_F4),

	F5(JavaKeyCodes.VK_F5),

	F6(JavaKeyCodes.VK_F6),

	F7(JavaKeyCodes.VK_F7),

	F8(JavaKeyCodes.VK_F8),

	F9(JavaKeyCodes.VK_F9),

	F10(JavaKeyCodes.VK_F10),

	F11(JavaKeyCodes.VK_F11),

	F12(JavaKeyCodes.VK_F12),

	K0(JavaKeyCodes.VK_0),

	K1(JavaKeyCodes.VK_1),

	K2(JavaKeyCodes.VK_2),

	K3(JavaKeyCodes.VK_3),

	K4(JavaKeyCodes.VK_4),

	K5(JavaKeyCodes.VK_5),

	K6(JavaKeyCodes.VK_6),

	K7(JavaKeyCodes.VK_7),

	K8(JavaKeyCodes.VK_8),

	K9(JavaKeyCodes.VK_9),

	NUMPAD0(JavaKeyCodes.VK_NUMPAD0),

	NUMPAD1(JavaKeyCodes.VK_NUMPAD1),

	NUMPAD2(JavaKeyCodes.VK_NUMPAD2),

	NUMPAD3(JavaKeyCodes.VK_NUMPAD3),

	NUMPAD4(JavaKeyCodes.VK_NUMPAD4),

	NUMPAD5(JavaKeyCodes.VK_NUMPAD5),

	NUMPAD6(JavaKeyCodes.VK_NUMPAD6),

	NUMPAD7(JavaKeyCodes.VK_NUMPAD7),

	NUMPAD8(JavaKeyCodes.VK_NUMPAD8),

	NUMPAD9(JavaKeyCodes.VK_NUMPAD9),

	MINUS(JavaKeyCodes.VK_MINUS, GWTKeycodes.KEY_MINUS),

	SUBTRACT(JavaKeyCodes.VK_SUBTRACT), // GWTKeycodes.KEY_NUM_MINUS

	PLUS(JavaKeyCodes.VK_PLUS, GWTKeycodes.KEY_NUM_PLUS),

	ADD(JavaKeyCodes.VK_ADD), // GWTKeycodes.KEY_NUM_PLUS

	EQUALS(JavaKeyCodes.VK_EQUALS, GWTKeycodes.KEY_EQUALS),

	BACK_QUOTE(JavaKeyCodes.VK_BACK_QUOTE, GWTKeycodes.KEY_BACK_QUOTE),

	LEFT_SQUARE_BRACKET(JavaKeyCodes.VK_OPEN_BRACKET, GWTKeycodes.KEY_LEFT_SQUARE_BRACKET),

	RIGHT_SQUARE_BRACKET(JavaKeyCodes.VK_CLOSE_BRACKET, GWTKeycodes.KEY_RIGHT_SQUARE_BRACKET),

	ALT(JavaKeyCodes.VK_ALT),

	BACKSPACE(JavaKeyCodes.VK_BACK_SPACE),

	CTRL(JavaKeyCodes.VK_CONTROL),

	DELETE(JavaKeyCodes.VK_DELETE, GWTKeycodes.KEY_DELETE),

	DOWN(JavaKeyCodes.VK_DOWN),

	END(JavaKeyCodes.VK_END),

	ENTER(JavaKeyCodes.VK_ENTER, GWTKeycodes.KEY_ENTER),

	ESCAPE(JavaKeyCodes.VK_ESCAPE),

	HOME(JavaKeyCodes.VK_HOME),

	LEFT(JavaKeyCodes.VK_LEFT),

	PAGEDOWN(JavaKeyCodes.VK_PAGE_DOWN),

	PAGEUP(JavaKeyCodes.VK_PAGE_UP),

	RIGHT(JavaKeyCodes.VK_RIGHT),

	SHIFT(JavaKeyCodes.VK_SHIFT),

	INSERT(JavaKeyCodes.VK_INSERT, GWTKeycodes.KEY_INSERT),

	TAB(JavaKeyCodes.VK_TAB),

	UP(JavaKeyCodes.VK_UP),

	SPACE(JavaKeyCodes.VK_SPACE),

	CONTEXT_MENU(JavaKeyCodes.VK_CONTEXT_MENU, GWTKeycodes.KEY_CONTEXT_MENU),

	WINDOWS(JavaKeyCodes.VK_WINDOWS, GWTKeycodes.KEY_WIN_KEY_LEFT_META),

	BACKSLASH(JavaKeyCodes.VK_BACK_SLASH, GWTKeycodes.KEY_BACKSLASH),

	SLASH(JavaKeyCodes.VK_SLASH, GWTKeycodes.KEY_SLASH),

	NUMPADSLASH(JavaKeyCodes.VK_DIVIDE), // GWTKeycodes.KEY_NUM_DIVISION

	COMMA(JavaKeyCodes.VK_COMMA, GWTKeycodes.KEY_COMMA),

	SEMICOLON(JavaKeyCodes.VK_SEMICOLON, GWTKeycodes.KEY_SEMICOLON),

	APOSTROPHE(JavaKeyCodes.VK_QUOTE, GWTKeycodes.KEY_APOSTROPHE),

	HASH(JavaKeyCodes.VK_NUMBER_SIGN, GWTKeycodes.KEY_HASH),

	NUMPADASTERISK(JavaKeyCodes.VK_MULTIPLY),

	NUMLOCK(JavaKeyCodes.VK_NUM_LOCK),

	/**
	 * Mac Meta/Apple, same keycode as Windows key in GWT
	 */
	META(JavaKeyCodes.VK_META, GWTKeycodes.KEY_WIN_KEY_LEFT_META),

	/**
	 * middle key of NumPad (with NumLock off)
	 */
	CLEAR(JavaKeyCodes.VK_CLEAR),

	PERIOD(JavaKeyCodes.VK_PERIOD, GWTKeycodes.KEY_PERIOD),

	DECIMALPOINT(JavaKeyCodes.VK_DECIMAL),

	DECIMALCOMMA(JavaKeyCodes.VK_SEPARATOR),

	UNKNOWN(JavaKeyCodes.VK_UNDEFINED);

	private final int javaKey;
	private final int GWTKey;

	KeyCodes(int javaKey, int GWTKey) {
		this.javaKey = javaKey;
		this.GWTKey = GWTKey;
	}

	KeyCodes(int javaKey) {
		this.javaKey = javaKey;
		this.GWTKey = javaKey;
	}

	/**
	 * @param javaKeyCode
	 *            java key code
	 * @return KeyCodes wrapper
	 */
	public static KeyCodes translateJavacode(int javaKeyCode) {
		for (KeyCodes l : KeyCodes.values()) {
			if (l.javaKey == javaKeyCode) {
				return l;
			}
		}
		return UNKNOWN;
	}

	public int getJavaKeyCode() {
		return javaKey;
	}

	public int getGWTKeyCode() {
		return GWTKey;
	}
}
