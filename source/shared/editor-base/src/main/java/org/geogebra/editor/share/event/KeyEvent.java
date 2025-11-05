/*
GeoGebra - Dynamic Mathematics for Schools
Copyright (c) GeoGebra GmbH, Altenbergerstr 69, 4040 Linz, Austria
https://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by
the Free Software Foundation.
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
