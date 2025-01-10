/* TeXConstants.java
 * =========================================================================
 * This file is originally part of the JMathTeX Library - http://jmathtex.sourceforge.net
 *
 * Copyright (C) 2004-2007 Universiteit Gent
 * Copyright (C) 2009 DENIZET Calixte
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
 *
 * Linking this library statically or dynamically with other modules
 * is making a combined work based on this library. Thus, the terms
 * and conditions of the GNU General Public License cover the whole
 * combination.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce
 * an executable, regardless of the license terms of these independent
 * modules, and to copy and distribute the resulting executable under terms
 * of your choice, provided that you also meet, for each linked independent
 * module, the terms and conditions of the license of that module.
 * An independent module is a module which is not derived from or based
 * on this library. If you modify this library, you may extend this exception
 * to your version of the library, but you are not obliged to do so.
 * If you do not wish to do so, delete this exception statement from your
 * version.
 *
 */

/* Modified by Calixte Denizet */

package com.himamis.retex.renderer.share;

/**
 * The collection of constants that can be used in the methods of the classes of
 * this package.
 */
public class TeXConstants {

	// *******************
	// ALIGNMENT CONSTANTS
	// *******************

	public static enum Align {
		LEFT, RIGHT, CENTER, TOP, BOTTOM, NONE, INVALID
	}

	public static enum Muskip {
		THIN, MED, THICK, NEGTHIN, NEGMED, NEGTHICK, NONE
	}

	public static enum Type {
		ORDINARY, BIG_OPERATOR, BINARY_OPERATOR, RELATION, OPENING, CLOSING, PUNCTUATION, INNER, ACCENT, INTERTEXT, MULTICOLUMN, HLINE, NONE
	}

	// ******************************************
	// * Define elements which are a group opener
	// ******************************************
	public static enum Opener {
		NONE, LBRACE, LSQBRACKET, B_LSQBRACKET, B_LBRACKET, BEGIN_MATH
	}

	public static final int SCRIPT_NORMAL = 0;
	public static final int SCRIPT_NOLIMITS = 1;
	public static final int SCRIPT_LIMITS = 2;

	// *********************
	// SYMBOL TYPE CONSTANTS
	// *********************

	/**
	 * Symbol/Atom type: ordinary symbol, e.g. "slash"
	 */
	public static final int TYPE_ORDINARY = 0;

	/**
	 * Symbol/Atom type: big operator (= large operator), e.g. "sum"
	 */
	public static final int TYPE_BIG_OPERATOR = 1;

	/**
	 * Symbol/Atom type: binary operator, e.g. "plus"
	 */
	public static final int TYPE_BINARY_OPERATOR = 2;

	/**
	 * Symbol/Atom type: relation, e.g. "equals"
	 */
	public static final int TYPE_RELATION = 3;

	/**
	 * Symbol/Atom type: opening symbol, e.g. "lbrace"
	 */
	public static final int TYPE_OPENING = 4;

	/**
	 * Symbol/Atom type: closing symbol, e.g. "rbrace"
	 */
	public static final int TYPE_CLOSING = 5;

	/**
	 * Symbol/Atom type: punctuation symbol, e.g. "comma"
	 */
	public static final int TYPE_PUNCTUATION = 6;

	/**
	 * Atom type: inner atom (NOT FOR SYMBOLS!!!)
	 */
	public static final int TYPE_INNER = 7;

	/**
	 * Symbol type: accent, e.g. "hat"
	 */
	public static final int TYPE_ACCENT = 10;

	public static final int TYPE_INTERTEXT = 11;

	public static final int TYPE_MULTICOLUMN = 12;

	public static final int TYPE_HLINE = 13;

	// ***************************************
	// OVER AND UNDER DELIMITER TYPE CONSTANTS
	// ***************************************

	/**
	 * Delimiter type constant for putting delimiters over and under formula's:
	 * brace
	 */
	public static final int DELIM_BRACE = 0;

	/**
	 * Delimiter type constant for putting delimiters over and under formula's:
	 * square bracket
	 */
	public static final int DELIM_SQUARE_BRACKET = 1;

	/**
	 * Delimiter type constant for putting delimiters over and under formula's:
	 * parenthesis
	 */
	public static final int DELIM_BRACKET = 2;

	/**
	 * Delimiter type constant for putting delimiters over and under formula's:
	 * arrow with single line pointing to the left
	 */
	public static final int DELIM_LEFT_ARROW = 3;

	/**
	 * Delimiter type constant for putting delimiters over and under formula's:
	 * arrow with single line pointing to the right
	 */
	public static final int DELIM_RIGHT_ARROW = 4;

	/**
	 * Delimiter type constant for putting delimiters over and under formula's:
	 * arrow with single line pointing to the left and to the right
	 */
	public static final int DELIM_LEFT_RIGHT_ARROW = 5;

	/**
	 * Delimiter type constant for putting delimiters over and under formula's:
	 * arrow with two lines pointing to the left
	 */
	public static final int DELIM_DOUBLE_LEFT_ARROW = 6;

	/**
	 * Delimiter type constant for putting delimiters over and under formula's:
	 * arrow with two lines pointing to the right
	 */
	public static final int DELIM_DOUBLE_RIGHT_ARROW = 7;

	/**
	 * Delimiter type constant for putting delimiters over and under formula's:
	 * arrow with two lines pointing to the left and to the right
	 */
	public static final int DELIM_DOUBLE_LEFT_RIGHT_ARROW = 8;

	/**
	 * Delimiter type constant for putting delimiters over and under formula's:
	 * underline once
	 */
	public static final int DELIM_SINGLE_LINE = 9;

	/**
	 * Delimiter type constant for putting delimiters over and under formula's:
	 * underline twice
	 */
	public static final int DELIM_DOUBLE_LINE = 10;

	// *******************
	// TEX STYLE CONSTANTS
	// *******************

	/**
	 * TeX style: display style.
	 * <p>
	 * The large versions of big operators are used and limits are placed under
	 * and over these operators (default). Symbols are rendered in the largest
	 * size.
	 */
	public static final int STYLE_DISPLAY = 0;

	/**
	 * TeX style: text style.
	 * <p>
	 * The small versions of big operators are used and limits are attached to
	 * these operators as scripts (default). The same size as in the display
	 * style is used to render symbols.
	 */
	public static final int STYLE_TEXT = 2;

	/**
	 * TeX style: script style.
	 * <p>
	 * The same as the text style, but symbols are rendered in a smaller size.
	 */
	public static final int STYLE_SCRIPT = 4;

	/**
	 * TeX style: script_script style.
	 * <p>
	 * The same as the script style, but symbols are rendered in a smaller size.
	 */
	public static final int STYLE_SCRIPT_SCRIPT = 6;
}
