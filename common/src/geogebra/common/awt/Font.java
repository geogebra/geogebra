package geogebra.common.awt;

public interface Font {
	
	public static final int PLAIN = 0;
	public static final int BOLD = 1;

	public abstract void setFontStyle(String fontStyle);

	public abstract String getFontStyle();

	public abstract String getFullFontString();

	public abstract void setFontVariant(String fontVariant);

	public abstract String getFontVariant();

	public abstract void setFontWeight(String fontWeight);

	public abstract String getFontWeight();

	public abstract void setFontSize(String fontSize);

	public abstract String getFontSize();

	public abstract void setLineHeight(String lineHeight);

	public abstract String getLineHeight();

	public abstract void setFontFamily(String fontFamily);

	public abstract String getFontFamily();

	public abstract Font deriveFont(String fontStyle2, final int newSize);

	public abstract int canDisplayUpTo(String textString);

	public abstract void setFontStyle(int fontStyle);

	public abstract void setFontSize(int fontSize);

	public abstract int getSize();

	public abstract boolean isItalic();

	public abstract boolean isBold();


}
