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

package org.geogebra.keyboard.web;

import org.geogebra.editor.share.util.Unicode;
import org.geogebra.keyboard.base.model.impl.factory.Characters;

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
