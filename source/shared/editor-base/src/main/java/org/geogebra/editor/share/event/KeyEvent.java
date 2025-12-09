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

package org.geogebra.editor.share.event;

public class KeyEvent {

	/**
	 * The Shift key extended modifier constant.
	 */
	public static final int SHIFT_MASK = 1;

	/**
	 * The Control key extended modifier constant.
	 */
	public static final int CTRL_MASK = 2;
	/**
	 * The Alt key extended modifier constant.
	 */
	public static final int ALT_MASK = 1 << 3;

	/**
	 * The Meta key extended modifier constant.
	 */
	public static final int META_DOWN_MASK = 1 << 8;

	// these 4 used in Android
	public static final int ACTION_DOWN = 0;
	public static final int ACTION_UP = 1;
	public static final int ACTION_MULTIPLE = 2;
	public static final int ACTION_UNKNOWN = -1;

	private final int keyCode;
	private final int keyModifiers;
	private final char unicodeKeyChar;
	private final KeyboardType sourceKeyboard;

	private final int action;

	public enum KeyboardType {
		/** External keyboard */
		EXTERNAL,
		/** GeoGebra's math keyboard */
		INTERNAL,
		/** Keyboard type cannot be determined */
		UNKNOWN
	}

	public KeyEvent(int keyCode, KeyboardType keyboardType) {
		this(keyCode, 0, keyboardType);
	}

	public KeyEvent(int keyCode, int keyModifiers, KeyboardType keyboardType) {
		this(keyCode, keyModifiers, '\0', keyboardType);
	}

	/**
	 * @param keyCode
	 *            key code
	 * @param keyModifiers
	 *            modifiers (ALT_MASK | SHIFT_MASK)
	 * @param unicodeKeyChar
	 *            Unicode key
	 * @param sourceKeyboard source keyboard type
	 */
	public KeyEvent(int keyCode, int keyModifiers, char unicodeKeyChar,
			KeyboardType sourceKeyboard) {
		this.keyCode = keyCode;
		this.keyModifiers = keyModifiers;
		this.unicodeKeyChar = unicodeKeyChar;
		this.sourceKeyboard = sourceKeyboard;
		this.action = ACTION_DOWN;
	}

	/**
	 * @param keyCode
	 *            key code
	 * @param keyModifiers
	 *            modifiers (ALT_MASK | SHIFT_MASK)
	 * @param unicodeKeyChar
	 *            Unicode key
	 * @param sourceKeyboard source keyboard type
	 * @param action for Android only, one of the ACTION_* constants
	 */
	public KeyEvent(int keyCode, int keyModifiers, char unicodeKeyChar,
			KeyboardType sourceKeyboard, int action) {
		this.keyCode = keyCode;
		this.keyModifiers = keyModifiers;
		this.unicodeKeyChar = unicodeKeyChar;
		this.sourceKeyboard = sourceKeyboard;
		this.action = action;
	}

	public int getKeyCode() {
		return keyCode;
	}

	public int getKeyModifiers() {
		return keyModifiers;
	}
	
	public char getUnicodeKeyChar() {
		return unicodeKeyChar;
	}

	/**
	 * @return keyboard type
	 */
	public KeyboardType getSourceKeyboard() {
		return sourceKeyboard;
	}

	/**
	 * Android only
	 * 
	 * @return ACTION_UP / ACTION_DOWN / ACTION_MULTIPLE / ACTION_UNKNOWN
	 */
	public int getAction() {
		return action;
	}

}
