package org.geogebra.web.html5.awt.font;

import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.awt.font.GTextLayout;
import org.geogebra.editor.share.util.Unicode;
import org.geogebra.ggbjdk.java.awt.geom.Rectangle;
import org.geogebra.web.html5.awt.GFontRenderContextW;
import org.geogebra.web.html5.awt.GFontW;

public class GTextLayoutW implements GTextLayout {

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
				if (Unicode.CHARACTERS_WITH_DESCENDERS_STRING.indexOf(str.charAt(i)) > -1) {
					containsLowerCase = true;
					break;
				}
			}
		}
		// containsLowerCase = str.indexOf('g') > -1 || str.indexOf('y') > -1 ||
		// str.indexOf('j') > -1 || str.indexOf('f') > -1;
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
