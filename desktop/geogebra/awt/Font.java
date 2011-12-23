package geogebra.awt;

public class Font extends geogebra.common.awt.Font {
	
	private java.awt.Font impl = new java.awt.Font("Default", geogebra.common.awt.Font.PLAIN, 12);

	public Font(java.awt.Font font){
		impl = font;
	}
	public java.awt.Font getAwtFont() {
		return impl;
	}

	public static java.awt.Font getAwtFont(geogebra.common.awt.Font font) {
		if(!(font instanceof Font))
			return null;
		return ((Font)font).impl;
	}
	@Override
	public int getStyle() {
		return impl.getStyle();
	}
	@Override
	public int getSize() {
		return impl.getSize();
	}
	@Override
	public boolean isItalic() {
		return impl.isItalic();
	}
	@Override
	public boolean isBold() {
		return impl.isBold();
	}
	@Override
	public int canDisplayUpTo(String textString) {
		return impl.canDisplayUpTo(textString);
	}
	public Font deriveFont(int style, int fontSize){
		return new Font(impl.deriveFont(style, fontSize));
	}
	@Override
	public geogebra.common.awt.Font deriveFont(int i) {
		return new Font(impl.deriveFont(i));
	}
}
