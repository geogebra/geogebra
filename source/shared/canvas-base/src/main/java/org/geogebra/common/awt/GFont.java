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

package org.geogebra.common.awt;

/**
 * Platform independent Font
 *
 */
public abstract class GFont {
	/** neither bold or italic font */
	public static final int PLAIN = 0;
	/** bold font */
	public static final int BOLD = 1;
	/** italic font */
	public static final int ITALIC = 2;
	/** underline font */
	public static final int UNDERLINE = 4;

	/**
	 * Returns the style of this <code>Font</code>. The style can be PLAIN,
	 * BOLD, ITALIC, or BOLD+ITALIC.
	 * 
	 * @return the style of this <code>Font</code>
	 * @see #isBold
	 * @see #isItalic
	 */
	public abstract int getStyle();

	/**
	 * Returns the point size of this <code>Font</code>, rounded to an integer.
	 * Most users are familiar with the idea of using <i>point size</i> to
	 * specify the size of glyphs in a font.
	 * 
	 * @return the point size of this <code>Font</code> in 1/72 of an inch
	 *         units.
	 */
	public abstract int getSize();

	/**
	 * Indicates whether this <code>Font</code> object's style is ITALIC.
	 * 
	 * @return <code>true</code> if this <code>Font</code> object's style is
	 *         ITALIC; <code>false</code> otherwise.
	 */
	public abstract boolean isItalic();

	/**
	 * Indicates whether this <code>Font</code> object's style is BOLD.
	 * 
	 * @return <code>true</code> if this <code>Font</code> object's style is
	 *         BOLD; <code>false</code> otherwise.
	 */
	public abstract boolean isBold();

	/**
	 * Indicates whether this <code>Font</code> can display a specified
	 * <code>String</code>. For strings with Unicode encoding, it is important
	 * to know if a particular font can display the string. This method returns
	 * an offset into the <code>String</code> <code>str</code> which is the
	 * first character this <code>Font</code> cannot display without using the
	 * missing glyph code. If the <code>Font</code> can display all characters,
	 * -1 is returned.
	 * 
	 * @param str
	 *            a <code>String</code> object
	 * @return an offset into <code>str</code> that points to the first
	 *         character in <code>str</code> that this <code>Font</code> cannot
	 *         display; or <code>-1</code> if this <code>Font</code> can display
	 *         all characters in <code>str</code>.
	 */
	public abstract int canDisplayUpTo(String str);

	/**
	 * @param style
	 *            font style ({@link GFont#PLAIN}, {@link GFont#BOLD}, {@link GFont#ITALIC}
	 *            or sum of the last two)
	 * @param fontSize
	 *            font size
	 * @return derive fonts
	 */
	public abstract GFont deriveFont(int style, int fontSize);

	/**
	 * @param style
	 *            font style (GFont.PLAIN, GFont.BOLD, GFont.ITALIC or sum of
	 *            last two)
	 * @param fontSize
	 *            font size
	 * @return derive fonts
	 */
	public abstract GFont deriveFont(int style, double fontSize);

	/**
	 * @param style
	 *            font style (GFont.PLAIN, GFont.BOLD, GFont.ITALIC or sum of
	 *            last two)
	 * @return derive fonts
	 */
	public abstract GFont deriveFont(int style);

	/**
	 * @return font name
	 */
	public abstract String getFontName();

}
