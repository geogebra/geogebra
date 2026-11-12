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
 * Key codes for the browser. Some overlap with {@link JavaKeyCodes}, some do not.
 */
public interface GWTKeycodes {
	/**
	 * Backspace key code.
	 */
	int KEY_BACKSPACE = 8;
	/**
	 * Enter key code.
	 */
	int KEY_ENTER = 13;
	/**
	 * Escape key code.
	 */
	int KEY_ESCAPE = 27;
	/**
	 * Key code for insert key (Also numeric keyboard insert).
	 */
	int KEY_INSERT = 45; // also NUM_INSERT
	/**
	 * Delete key code (also numeric keypad delete).
	 */
	int KEY_DELETE = 46;
	/**
	 * Key code for left windows key or meta.
	 */
	int KEY_WIN_KEY_LEFT_META = 91;
	/**
	 * Key code for right windows key.
	 */
	int KEY_WIN_KEY_RIGHT = 92;
	/**
	 * Key code for context menu key.
	 */
	int KEY_CONTEXT_MENU = 93;
	/**
	 * Key code for plus on numeric keyboard
	 */
	int KEY_NUM_PLUS = 107;

	int KEY_SEMICOLON = 186;
	int KEY_EQUALS = 187;
	int KEY_COMMA = 188;
	int KEY_MINUS = 189;
	int KEY_PERIOD = 190;
	int KEY_SLASH = 191;
	int KEY_APOSTROPHE = 192;
	int KEY_LEFT_SQUARE_BRACKET = 219;
	int KEY_BACKSLASH = 220;
	int KEY_RIGHT_SQUARE_BRACKET = 221;
	int KEY_HASH = 222;
	int KEY_BACK_QUOTE = 223;
}
