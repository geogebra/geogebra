package geogebra.web.awt;

import geogebra.common.main.AbstractApplication;

public class Font extends geogebra.common.awt.Font{
	public static final int PLAIN = 0;
	public static final int BOLD = 1;
	private String fontStyle = "normal";
	private String fontVariant = "normal";
	private String fontWeight = "normal";
	private String fontSize = "12";
	private String lineHeight = "12";
	private String fontFamily = "sans-serif";
	
	public Font(String fontStyle) {
		this.setFontStyle(fontStyle);			
		// TODO Auto-generated constructor stub
	}

	public void setFontStyle(String fontStyle) {
		this.fontStyle = fontStyle;
	}

	public String getFontStyle() {
		return fontStyle;
	}

	public String getFullFontString() {
		return fontStyle+" "+fontVariant+" "+fontWeight+" "+fontSize+"px/"+lineHeight+"px "+fontFamily;
	}

	public void setFontVariant(String fontVariant) {
		this.fontVariant = fontVariant;
	}

	public String getFontVariant() {
		return fontVariant;
	}

	public void setFontWeight(String fontWeight) {
		this.fontWeight = fontWeight;
	}

	public String getFontWeight() {
		return fontWeight;
	}

	public void setFontSize(String fontSize) {
		this.fontSize = fontSize;
	}

	public String getFontSize() {
		return fontSize;
	}

	public void setLineHeight(String lineHeight) {
		this.lineHeight = lineHeight;
	}

	public String getLineHeight() {
		return lineHeight;
	}

	public void setFontFamily(String fontFamily) {
		this.fontFamily = fontFamily;
	}

	public String getFontFamily() {
		return fontFamily;
	}

	public Font deriveFont(String fontStyle2, final int newSize) {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated
		Font f = new Font(fontStyle2);
		f.setFontSize(newSize);
		return f;
	}

	@Override
    public int canDisplayUpTo(String textString) {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated
		return textString.length();
	}

	public void setFontStyle(int fontStyle) {
		switch (fontStyle) {
		case 0:
			setFontStyle("normal");
			break;
		case 1:
			setFontWeight("bold");
			break;
		case 2:
			setFontStyle("italic");
			break;
		case 3:
			setFontWeight("bold");
			setFontStyle("italic");
			break;
		default:
			setFontStyle("normal");
			setFontWeight("normal");
		}
	}

	public void setFontSize(int fontSize) {
		this.fontSize = ""+fontSize; 
	}
	
	@Override
    public int getSize() {
		return Integer.parseInt(fontSize);
	}
	
	@Override
    public boolean isItalic() {
		return fontStyle.equals("italic");
	}
	
	@Override
    public boolean isBold() {
		return fontWeight.equals("bold");
	}

	@Override
    public int getStyle() {
	    return (isBold()?1:0)+(isItalic()?2:0);
    }

	@Override
    public geogebra.common.awt.Font deriveFont(int plain2, int fontSize) {
	    Font ret = new Font(fontStyle);
	    ret.setFontStyle(plain2);
	    ret.setFontSize(fontSize);
	    return ret;
    }

	@Override
    public geogebra.common.awt.Font deriveFont(int i) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    return null;
    }

	@Override
    public String getFontName() {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    return fontFamily;
    }

}
