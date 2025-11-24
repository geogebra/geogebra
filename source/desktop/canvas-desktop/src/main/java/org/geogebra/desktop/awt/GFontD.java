package org.geogebra.desktop.awt;

import java.awt.Font;

import org.geogebra.common.awt.GFont;

public class GFontD extends GFont {

	private Font impl = new Font("Default", GFont.PLAIN, 12);

	public GFontD(Font font) {
		impl = font;
	}

	public Font getAwtFont() {
		return impl;
	}

	/**
	 * @param font cross-platform font
	 * @return native font
	 */
	public static Font getAwtFont(GFont font) {
		if (!(font instanceof GFontD)) {
			return null;
		}
		return ((GFontD) font).impl;
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

	@Override
	public GFontD deriveFont(int style, int fontSize) {
		return new GFontD(impl.deriveFont(style, fontSize));
	}

	@Override
	public GFontD deriveFont(int style, double fontSize) {
		return new GFontD(impl.deriveFont(style, (float) fontSize));
	}

	@Override
	public GFont deriveFont(int i) {
		return new GFontD(impl.deriveFont(i));
	}

	@Override
	public String getFontName() {
		return impl.getFontName();
	}

	@Override
	public boolean equals(Object font) {
		if (font instanceof GFontD) {
			return impl.equals(((GFontD) font).impl);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return impl.hashCode();
	}
}
