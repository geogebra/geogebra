package geogebra.common.awt;

public abstract class GFont {

	public static final int PLAIN = 0;
	public static final int BOLD = 1;
	public static final int ITALIC = 2;

	public abstract int getStyle();

	public abstract int getSize();

	public abstract boolean isItalic();

	public abstract boolean isBold();

	public abstract int canDisplayUpTo(String textString);

	public abstract GFont deriveFont(int plain2, int fontSize);

	public abstract GFont deriveFont(int plain2, float fontSize);

	public abstract GFont deriveFont(int i);

	public abstract String getFontName();

	/*
	 * public abstract void setFontStyle(String fontStyle);
	 * 
	 * public abstract String getFontStyle();
	 * 
	 * public abstract String getFullFontString();
	 * 
	 * public abstract void setFontVariant(String fontVariant);
	 * 
	 * public abstract String getFontVariant();
	 * 
	 * public abstract void setFontWeight(String fontWeight);
	 * 
	 * public abstract String getFontWeight();
	 * 
	 * public abstract void setFontSize(String fontSize);
	 * 
	 * public abstract String getFontSize();
	 * 
	 * public abstract void setLineHeight(String lineHeight);
	 * 
	 * public abstract String getLineHeight();
	 * 
	 * public abstract void setFontFamily(String fontFamily);
	 * 
	 * public abstract String getFontFamily();
	 * 
	 * public abstract Font deriveFont(String fontStyle2, final int newSize);
	 * 
	 * public abstract int canDisplayUpTo(String textString);
	 * 
	 * public abstract void setFontStyle(int fontStyle);
	 * 
	 * public abstract void setFontSize(int fontSize);
	 * 
	 * public abstract int getSize();
	 * 
	 * public abstract boolean isItalic();
	 * 
	 * public abstract boolean isBold();
	 * 
	 * public abstract int getStyle();
	 * 
	 * public abstract Font deriveFont(int i);
	 * 
	 * public abstract Font deriveFont(int style, int newSize);
	 */

}
