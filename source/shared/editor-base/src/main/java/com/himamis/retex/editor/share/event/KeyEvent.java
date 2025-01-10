/**
 * This file is part of the ReTeX library - https://github.com/himamis/ReTeX
 *
 * Copyright (C) 2015 Balazs Bencze
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * A copy of the GNU General Public License can be found in the file
 * LICENSE.txt provided with the source distribution of this program (see
 * the META-INF directory in the source jar). This license can also be
 * found on the GNU website at http://www.gnu.org/licenses/gpl.html.
 *
 * If you did not receive a copy of the GNU General Public License along
 * with this program, contact the lead developer, or write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package com.himamis.retex.editor.share.event;

public class KeyEvent {

	/**
	 * The Shift key extended modifier constant.
	 * 
	 * @since 1.4
	 */
	public static final int SHIFT_MASK = 1;

	/**
	 * The Control key extended modifier constant.
	 * 
	 * @since 1.4
	 */
	public static final int CTRL_MASK = 2;
	/**
	 * The Alt key extended modifier constant.
	 * 
	 * @since 1.4
	 */
	public static final int ALT_MASK = 1 << 3;

	/**
	 * The Meta key extended modifier constant.
	 * 
	 * @since 1.4
	 */
	public static final int META_DOWN_MASK = 1 << 8;

	// these 4 used in Android
	public static final int ACTION_DOWN = 0;
	public static final int ACTION_UP = 1;
	public static final int ACTION_MULTIPLE = 2;
	public static final int ACTION_UNKNOWN = -1;

	private int keyCode;
	private int keyModifiers;
	private char unicodeKeyChar;

	private int action;

	public KeyEvent(int keyCode) {
		this(keyCode, 0);
	}

	public KeyEvent(int keyCode, int keyModifiers) {
		this(keyCode, keyModifiers, '\0');
	}

	/**
	 * @param keyCode
	 *            key code
	 * @param keyModifiers
	 *            modifiers (ALT_MASK | SHIFT_MASK)
	 * @param unicodeKeyChar
	 *            unicode key
	 */
	public KeyEvent(int keyCode, int keyModifiers, char unicodeKeyChar) {
		this.keyCode = keyCode;
		this.keyModifiers = keyModifiers;
		this.unicodeKeyChar = unicodeKeyChar;
	}

	/**
	 * @param keyCode
	 *            key code
	 * @param keyModifiers
	 *            modifiers (ALT_MASK | SHIFT_MASK)
	 * @param unicodeKeyChar
	 *            unicode key
	 * @param action
	 *            for Android, one of the ACTION_* constants
	 */
	public KeyEvent(int keyCode, int keyModifiers, char unicodeKeyChar,
			int action) {
		this(keyCode, keyModifiers, unicodeKeyChar);
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
	 * Android only
	 * 
	 * @return UP / DOWN / MULTIPLE / ?
	 */
	public int getAction() {
		return action;
	}
}
