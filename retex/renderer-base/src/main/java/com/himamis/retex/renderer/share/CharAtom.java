/* CharAtom.java
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

package com.himamis.retex.renderer.share;

/**
 * An atom representing exactly one alphanumeric character and the text style in
 * which it should be drawn.
 */
public class CharAtom extends CharSymbol {

	// alphanumeric character
	protected final char c;

	// text style (null means the default text style)
	protected int textStyle = TextStyle.NONE;

	/**
	 * Creates a CharAtom that will represent the given character in the given
	 * text style. Null for the text style means the default text style.
	 *
	 * @param c
	 *            the alphanumeric character
	 * @param textStyle
	 *            the text style in which the character should be drawn
	 */
	public CharAtom(char c, int textStyle, boolean mathMode) {
		this.c = c;
		this.textStyle = textStyle;
		this.mathMode = mathMode;
	}

	public CharAtom(char c, boolean mathMode) {
		this(c, TextStyle.NONE, mathMode);
	}

	public CharAtom(char c, int textStyle) {
		this(c, textStyle, false);
	}

	public CharAtom(char c) {
		this(c, TextStyle.NONE, false);
	}

	@Override
	public Box createBox(TeXEnvironment env) {
		if (textStyle == TextStyle.NONE) {
			textStyle = env.getTextStyle();
		}
		final boolean smallCap = env.getSmallCap();
		final Char ch = getChar(env.getTeXFont(), env.getStyle(), smallCap);
		CharBox box;
		if (smallCap && Character.isLowerCase(c)) {
			// We have a small capital
			box = new ScaledCharBox(ch, 0.8);
		} else {
			box = new CharBox(ch);
		}

		if (isMathMode() && mustAddItalicCorrection()) {
			box.addToWidth(ch.getItalic());
		}

		box.setAtom(this);
		return box;
	}

	public char getCharacter() {
		return c;
	}

	/*
	 * Get the Char-object representing this character ("c") in the right text
	 * style
	 */
	public Char getChar(TeXFont tf, int style, boolean smallCap) {
		char chr = c;
		if (smallCap && Character.isLowerCase(c)) {
			chr = Character.toUpperCase(c);
		}
		if (textStyle == TextStyle.NONE) {
			return tf.getChar(chr, TextStyle.MATHNORMAL, style);
		} else {
			return tf.getChar(chr, textStyle, style);
		}
	}

	@Override
	public Char getChar(TeXEnvironment env) {
		return getChar(env.getTeXFont(), env.getStyle(), env.getSmallCap());
	}

	@Override
	public CharFont getCharFont(TeXFont tf) {
		return getChar(tf, TeXConstants.STYLE_DISPLAY, false).getCharFont();
	}

	@Override
	public String toString() {
		return "CharAtom: \'" + c + "\'";
	}
}
