package geogebra.web.awt;

import geogebra.common.main.App;

public class GFontW extends geogebra.common.awt.GFont{
	public static final int PLAIN = 0;
	public static final int BOLD = 1;
	private String fontStyle = "normal";
	private String fontVariant = "normal";
	private String fontWeight = "normal";
	private String fontSize = "12";
	private String lineHeight = "12";
	private String fontFamily = "sans-serif";

	public GFontW(GFontW otherfont) {
		fontStyle = otherfont.getFontStyle();
		fontVariant = otherfont.getFontVariant();
		fontWeight = otherfont.getFontWeight();
		fontSize = otherfont.getFontSize();
		lineHeight = otherfont.getLineHeight();
		fontFamily = otherfont.getFontFamily();
	}

	public GFontW(String fontStyle) {
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

	public GFontW deriveFont(String fontStyle2, final int newSize) {
		App.debug("implementation needed or OK"); // TODO Auto-generated
		GFontW f = new GFontW(fontStyle2);
		f.setFontSize(newSize);
		return f;
	}

	@Override
    public int canDisplayUpTo(String textString) {
		// Suppose that everything will work well as it is difficult to determine
		// if a character is displayable or not
		return -1;
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
    public geogebra.common.awt.GFont deriveFont(int plain2, int fontSize) {
	    GFontW ret = new GFontW(fontStyle);
	    ret.setFontStyle(plain2);
	    ret.setFontSize(fontSize);
	    return ret;
    }

	@Override
    public geogebra.common.awt.GFont deriveFont(int i) {
	    App.debug("implementation needed or OK");
	    GFontW ret = new GFontW(fontStyle);
	    ret.setFontStyle(i);
	    ret.setFontSize(fontSize);
	    return ret;
    }

	@Override
    public String getFontName() {
	    App.debug("implementation needed or OK");
	    return fontFamily;
    }

}
