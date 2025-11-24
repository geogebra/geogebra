package org.geogebra.web.awt;

import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.awt.font.GTextLayout;
import org.geogebra.ggbjdk.java.awt.geom.Rectangle;

public class GTextLayoutW implements GTextLayout {

	// various characters which hang down below the line
	// gjy with/without accents
	// characters with cedillas
	// some Greek, Russian, Malayalam, Arabic
	public static final String CHARACTERS_WITH_DESCENDERS_STRING = "\u00B5\u1EF3\u0177\u0135"
			+ "\u0157\u0163\u0137\u015F\u0137\u013C\u00E7\u0146\u1EF9\u011F\u011D\u0123"
			+ "\u00FDgjy\u03BE\u03B2\u03C8\u03B3\u03B7\u03C2\u0444\u0449\u0446\u0D71\u0D6C"
			+ "\u0D6B\u0D33\u0D67\u0630\u0648\u0635\u0628\u0631\u064D\u0633\u062E\u064A\u064D";

	GFont font;
	String str;
	GFontRenderContextW frc;
	boolean containsLowerCase = false;
	int advance = -1;

	/**
	 * Creates a layout for given text.
	 *
	 * @param str
	 *            string
	 * @param font
	 *            font
	 * @param frc
	 *            font context
	 */
	public GTextLayoutW(String str, GFont font, GFontRenderContextW frc) {
		this.font = font;
		this.str = str;
		this.frc = frc;

		if (str.length() > 0) {
			for (int i = 0; i < str.length(); i++) {
				if (CHARACTERS_WITH_DESCENDERS_STRING.indexOf(str.charAt(i)) > -1) {
					containsLowerCase = true;
					break;
				}
			}
		}
	}

	@Override
	public double getAdvance() {
		if (advance < 0 && frc != null) {
			advance = frc.measureText(str, ((GFontW) font).getFullFontString());
		}
		return advance;
	}

	@Override
	public GRectangle2D getBounds() {
		return new Rectangle((int) getAdvance(), (int) getAscent());
	}

	@Override
	public double getAscent() {
		if (containsLowerCase) {
			return font.getSize() * 0.75f;
		}
		return font.getSize() * 0.80f;
	}

	@Override
	public double getDescent() {
		if (containsLowerCase) {
			return font.getSize() * 0.25f;
		}
		return font.getSize() * 0.20f;
	}

	@Override
	public void draw(GGraphics2D g2, int x, int y) {
		GFont tempFont = g2.getFont();
		g2.setFont(font);
		g2.drawString(str, x, y);
		g2.setFont(tempFont);
	}

}
