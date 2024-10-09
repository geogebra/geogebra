package org.geogebra.keyboard.web;

import org.geogebra.keyboard.base.model.impl.factory.Characters;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * Contains Strings which are used for the {@link BaseKeyboardButton buttons} of
 * the {@link TabbedKeyboard}
 */
public class KeyboardConstants {
	/** a to the power of 2 */
	public static final String A_SQUARE = "a" + Unicode.SUPERSCRIPT_2;
	/** a to the power of x */
	public static final String A_POWER_X = "a^x";
	/** hashtag and not*/
	public static final String SWITCH_TO_SPECIAL_SYMBOLS = "#&" + Unicode.NOT;
	/** greek letters */
	public static final String SWITCH_TO_GREEK_CHARACTERS = ""
			+ Characters.ALPHA + Characters.BETA + Characters.GAMMA;
	/** left floor x right floor */
	public static final String FLOOR = "" + Unicode.LFLOOR + "x" + Unicode.RFLOOR;
	/** left ceil x right ceil */
	public static final String CEIL = "" + Unicode.LCEIL + "x" + Unicode.RCEIL;
}
