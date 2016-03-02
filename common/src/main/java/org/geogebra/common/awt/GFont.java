package org.geogebra.common.awt;

public abstract class GFont {

	public static final int PLAIN = 0;
	public static final int BOLD = 1;
	public static final int ITALIC = 2;

	public abstract int getStyle();

	public abstract int getSize();

	public abstract boolean isItalic();

	public abstract boolean isBold();

	public abstract int canDisplayUpTo(String textString);

	/**
	 * @param style
	 *            font style (GFont.PLAIN, GFont.BOLD, GFont.ITALIC or sum of
	 *            last two)
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
	public abstract GFont deriveFont(int style, float fontSize);

	/**
	 * @param style
	 *            font style (GFont.PLAIN, GFont.BOLD, GFont.ITALIC or sum of
	 *            last two)
	 * @return derive fonts
	 */
	public abstract GFont deriveFont(int style);

	public abstract String getFontName();

}
