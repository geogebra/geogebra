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
