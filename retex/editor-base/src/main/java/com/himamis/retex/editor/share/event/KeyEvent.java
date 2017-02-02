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

	public static final int VK_ENTER = '\n';
	public static final int VK_BACK_SPACE = '\b';
	public static final int VK_TAB = '\t';
	public static final int VK_SHIFT = 0x10;
	public static final int VK_CONTROL = 0x11;
	public static final int VK_ALT = 0x12;
	public static final int VK_CAPS_LOCK = 0x14;
	public static final int VK_ESCAPE = 0x1B;
	public static final int VK_SPACE = 0x20;
	public static final int VK_PAGE_UP = 0x21;
	public static final int VK_PAGE_DOWN = 0x22;
	public static final int VK_END = 0x23;
	public static final int VK_HOME = 0x24;

	/**
	 * Constant for the non-numpad <b>left</b> arrow key.
	 */
	public static final int VK_LEFT = 0x25;

	/**
	 * Constant for the non-numpad <b>up</b> arrow key.
	 */
	public static final int VK_UP = 0x26;

	/**
	 * Constant for the non-numpad <b>right</b> arrow key.
	 */
	public static final int VK_RIGHT = 0x27;

	/**
	 * Constant for the non-numpad <b>down</b> arrow key.
	 */
	public static final int VK_DOWN = 0x28;

	/**
	 * Constant for the comma key, ","
	 */
	public static final int VK_COMMA = 0x2C;



	/**
	 * Constant for the semicolon key, ";"
	 */
	public static final int VK_SEMICOLON = 0x3B;

	/**
	 * Constant for the equals key, "="
	 */
	public static final int VK_EQUALS = 0x3D;

	/** VK_A thru VK_Z are the same as ASCII 'A' thru 'Z' (0x41 - 0x5A) */
	public static final int VK_A = 0x41;
	public static final int VK_C = 0x43;
	public static final int VK_V = 0x56;
	public static final int VK_X = 0x58;
	public static final int VK_Y = 0x59;
	public static final int VK_Z = 0x5A;

	/**
	 * Constant for the open bracket key, "["
	 */
	public static final int VK_OPEN_BRACKET = 0x5B;
	public static final int VK_OPEN_PAREN = 0x39;

	/**
	 * Constant for the back slash key, "\"
	 */
	public static final int VK_BACK_SLASH = 0x5C;

	/**
	 * Constant for the close bracket key, "]"
	 */
	public static final int VK_CLOSE_BRACKET = 0x5D;


	public static final int VK_DELETE = 0x7F; /* ASCII DEL */


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

	public KeyEvent(int keyCode, int keyModifiers, char unicodeKeyChar) {
		this.keyCode = keyCode;
		this.keyModifiers = keyModifiers;
		this.unicodeKeyChar = unicodeKeyChar;
	}

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
