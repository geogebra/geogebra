package org.geogebra.keyboard.web;

import org.geogebra.common.util.lang.Unicode;

/**
 * Contains Strings which are used for the {@link KeyBoardButtonBase buttons} of
 * the {@link KBBase}
 */
public class KeyboardConstants {

	/** a to the power of 2 */
	public static final String A_SQUARE = "a" + Unicode.Superscript_2;
	/** a to the power of x */
	public static final String A_POWER_X = "a^x";
	/** hashtag and not*/
	public static final String SWITCH_TO_SPECIAL_SYMBOLS = "" + Unicode.HASHTAG + Unicode.AMPERSAND + Unicode.NOT;
	/** left floor x right floor */
	public static final String FLOOR = "" + Unicode.LFLOOR + "x" + Unicode.RFLOOR;
	/** left ceil x right ceil */
	public static final String CEIL = "" + Unicode.LCEIL + "x" + Unicode.RCEIL;
	

}
