package org.geogebra.common.awt;

import com.himamis.retex.renderer.share.TeXFont;

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

	/**
	 * @param serif
	 *            whether this is serif font
	 * @return style as required by JLaTeXMath
	 */
	public int getLaTeXStyle(boolean serif) {
		int style = 0;
		if (isBold()) {
			style = style | TeXFont.BOLD;
		}
		if (isItalic()) {
			style = style | TeXFont.ITALIC;
		}
		if (!serif) {
			style = style | TeXFont.SANSSERIF;
		}

		return style;
	}

}
