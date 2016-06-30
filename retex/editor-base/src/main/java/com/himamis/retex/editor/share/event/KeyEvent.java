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
	 * The Meta key extended modifier constant.
	 * 
	 * @since 1.4
	 */
	public static final int META_DOWN_MASK = 1 << 8;

	/**
	 * The Alt key extended modifier constant.
	 * 
	 * @since 1.4
	 */
	public static final int ALT_DOWN_MASK = 1 << 9;

	public static final int VK_ENTER = '\n';
	public static final int VK_BACK_SPACE = '\b';
	public static final int VK_TAB = '\t';
	public static final int VK_CANCEL = 0x03;
	public static final int VK_CLEAR = 0x0C;
	public static final int VK_SHIFT = 0x10;
	public static final int VK_CONTROL = 0x11;
	public static final int VK_ALT = 0x12;
	public static final int VK_PAUSE = 0x13;
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
	 * Constant for the minus key, "-"
	 * 
	 * @since 1.2
	 */
	public static final int VK_MINUS = 0x2D;

	/**
	 * Constant for the period key, "."
	 */
	public static final int VK_PERIOD = 0x2E;

	/**
	 * Constant for the forward slash key, "/"
	 */
	public static final int VK_SLASH = 0x2F;

	/** VK_0 thru VK_9 are the same as ASCII '0' thru '9' (0x30 - 0x39) */
	public static final int VK_0 = 0x30;
	public static final int VK_1 = 0x31;
	public static final int VK_2 = 0x32;
	public static final int VK_3 = 0x33;
	public static final int VK_4 = 0x34;
	public static final int VK_5 = 0x35;
	public static final int VK_6 = 0x36;
	public static final int VK_7 = 0x37;
	public static final int VK_8 = 0x38;
	public static final int VK_9 = 0x39;

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
	public static final int VK_B = 0x42;
	public static final int VK_C = 0x43;
	public static final int VK_D = 0x44;
	public static final int VK_E = 0x45;
	public static final int VK_F = 0x46;
	public static final int VK_G = 0x47;
	public static final int VK_H = 0x48;
	public static final int VK_I = 0x49;
	public static final int VK_J = 0x4A;
	public static final int VK_K = 0x4B;
	public static final int VK_L = 0x4C;
	public static final int VK_M = 0x4D;
	public static final int VK_N = 0x4E;
	public static final int VK_O = 0x4F;
	public static final int VK_P = 0x50;
	public static final int VK_Q = 0x51;
	public static final int VK_R = 0x52;
	public static final int VK_S = 0x53;
	public static final int VK_T = 0x54;
	public static final int VK_U = 0x55;
	public static final int VK_V = 0x56;
	public static final int VK_W = 0x57;
	public static final int VK_X = 0x58;
	public static final int VK_Y = 0x59;
	public static final int VK_Z = 0x5A;

	/**
	 * Constant for the open bracket key, "["
	 */
	public static final int VK_OPEN_BRACKET = 0x5B;

	/**
	 * Constant for the back slash key, "\"
	 */
	public static final int VK_BACK_SLASH = 0x5C;

	/**
	 * Constant for the close bracket key, "]"
	 */
	public static final int VK_CLOSE_BRACKET = 0x5D;

	public static final int VK_MULTIPLY = 0x6A;
	public static final int VK_ADD = 0x6B;

	/**
	 * This constant is obsolete, and is included only for backwards
	 * compatibility.
	 * 
	 * @see #VK_SEPARATOR
	 */
	public static final int VK_SEPARATER = 0x6C;

	/**
	 * Constant for the Numpad Separator key.
	 * 
	 * @since 1.4
	 */
	public static final int VK_SEPARATOR = VK_SEPARATER;

	public static final int VK_SUBTRACT = 0x6D;
	public static final int VK_DECIMAL = 0x6E;
	public static final int VK_DIVIDE = 0x6F;
	public static final int VK_DELETE = 0x7F; /* ASCII DEL */

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

	public KeyEvent(int keyCode, int keyModifiers, char unicodeKeyChar) {
		this(keyCode, keyModifiers, unicodeKeyChar, ACTION_UNKNOWN);
	}

	public KeyEvent(int keyCode, int keyModifiers, char unicodeKeyChar, int action) {
		this.keyCode = keyCode;
		this.keyModifiers = keyModifiers;
		this.unicodeKeyChar = unicodeKeyChar;
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

	public int getAction() {
		return action;
	}
}
