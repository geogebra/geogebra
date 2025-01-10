package org.geogebra.web.html5.awt;

import org.geogebra.common.awt.GFont;

public class GFontW extends GFont {
	private static final String NORMAL_STR = "normal";
	private static final String BOLD_STR = "bold";
	private static final String ITALIC_STR = "italic";
	public static final String GEOGEBRA_FONT_SERIF = "geogebra-serif, serif";
	public static final String GEOGEBRA_FONT_SANSERIF = "geogebra-sans-serif, sans-serif";
	private String fontStyle = NORMAL_STR;
	private String fontVariant = NORMAL_STR;
	private String fontWeight = NORMAL_STR;
	private int fontSize = 12;
	private String fontFamily = GEOGEBRA_FONT_SANSERIF;

	/**
	 * @param otherfont
	 *            font to copy
	 */
	public GFontW(GFontW otherfont) {
		fontStyle = otherfont.getFontStyle();
		fontVariant = otherfont.getFontVariant();
		fontWeight = otherfont.getFontWeight();
		fontSize = otherfont.getFontSize();
		fontFamily = otherfont.getFontFamily();
	}

	/**
	 * @param fontStyle
	 *            style
	 */
	public GFontW(String fontStyle) {
		this.fontStyle = fontStyle;
	}

	/**
	 * @param name
	 *            eg SansSerif, Serif
	 * @param style
	 *            style
	 * @param size
	 *            size
	 */
	public GFontW(String name, int style, int size) {
		if ("Serif".equals(name)) {
			fontFamily = GEOGEBRA_FONT_SERIF;
		} else if ("SansSerif".equals(name)) {
			fontFamily = GEOGEBRA_FONT_SANSERIF;
		} else {
			fontFamily = name;
		}
		fontSize = size;
		setFontStyle(style);

	}

	public String getFontStyle() {
		return fontStyle;
	}

	/**
	 * @return complete CSS font description
	 */
	public String getFullFontString() {
		return fontStyle + " " + fontVariant + " " + fontWeight + " "
				+ fontSize + "px " + fontFamily;
	}

	public void setFontVariant(String fontVariant) {
		this.fontVariant = fontVariant;
	}

	public String getFontVariant() {
		return fontVariant;
	}

	public String getFontWeight() {
		return fontWeight;
	}

	public int getFontSize() {
		return fontSize;
	}

	public String getFontFamily() {
		return fontFamily;
	}

	@Override
	public int canDisplayUpTo(String textString) {
		// Suppose that everything will work well as it is difficult to
		// determine
		// if a character is displayable or not
		return -1;
	}

	private void setFontStyle(int fontStyle) {
		switch (fontStyle) {
		case GFont.BOLD:
			this.fontWeight = BOLD_STR;
			this.fontStyle = NORMAL_STR;
			break;
		case GFont.ITALIC:
			this.fontWeight = NORMAL_STR;
			this.fontStyle = ITALIC_STR;
			break;
		case GFont.BOLD + GFont.ITALIC:
			this.fontWeight = BOLD_STR;
			this.fontStyle = ITALIC_STR;
			break;
		default:
			this.fontStyle = NORMAL_STR;
			this.fontWeight = NORMAL_STR;
		}
	}

	@Override
	public int getSize() {
		return fontSize;
	}

	@Override
	public boolean isItalic() {
		return fontStyle.equals(ITALIC_STR);
	}

	@Override
	public boolean isBold() {
		return fontWeight.equals(BOLD_STR);
	}

	@Override
	public int getStyle() {
		return (isBold() ? GFont.BOLD : 0) + (isItalic() ? GFont.ITALIC : 0);
	}

	@Override
	public GFont deriveFont(int plain2, int newFontSize) {
		GFontW ret = new GFontW(fontStyle);
		ret.fontFamily = fontFamily;
		ret.setFontStyle(plain2);
		ret.fontSize = newFontSize;
		return ret;
	}

	@Override
	public GFont deriveFont(int plain2, double fontSize1) {
		return deriveFont(plain2, (int) fontSize1);
	}

	@Override
	public GFont deriveFont(int i) {
		GFontW ret = new GFontW(fontStyle);
		ret.setFontStyle(i);
		ret.fontSize = fontSize;
		ret.fontFamily = fontFamily;
		return ret;
	}

	@Override
	public String getFontName() {
		return fontFamily;
	}

	@Override
	public boolean equals(Object font) {
		if (font instanceof GFontW) {
			GFontW fontW = (GFontW) font;
			return fontFamily.equals(fontW.fontFamily)
					&& fontSize == fontW.fontSize
					&& fontStyle.equals(fontW.fontStyle)
					&& fontVariant.equals(fontW.fontVariant)
					&& fontWeight.equals(fontW.fontWeight);

		}

		return false;
	}

	@Override
	public int hashCode() {
		// any arbitrary
		// constant will do
		return fontSize | fontStyle.hashCode();
	}

}
