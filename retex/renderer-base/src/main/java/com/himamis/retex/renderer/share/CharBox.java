/* CharBox.java
 * =========================================================================
 * This file is originally part of the JMathTeX Library - http://jmathtex.sourceforge.net
 *
 * Copyright (C) 2004-2007 Universiteit Gent
 * Copyright (C) 2009 DENIZET Calixte
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * A copy of the GNU General Public License can be found in the file
 * LICENSE.txt provided with the source distribution of this program (see
 * the META-INF directory in the source jar). This license can also be
 * found on the GNU website at http://www.gnu.org/licenses/gpl.html.
 *
 * If you did not receive a copy of the GNU General Public License along
 * with this program, contact the lead developer, or write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 * Linking this library statically or dynamically with other modules
 * is making a combined work based on this library. Thus, the terms
 * and conditions of the GNU General Public License cover the whole
 * combination.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce
 * an executable, regardless of the license terms of these independent
 * modules, and to copy and distribute the resulting executable under terms
 * of your choice, provided that you also meet, for each linked independent
 * module, the terms and conditions of the license of that module.
 * An independent module is a module which is not derived from or based
 * on this library. If you modify this library, you may extend this exception
 * to your version of the library, but you are not obliged to do so.
 * If you do not wish to do so, delete this exception statement from your
 * version.
 *
 */

package com.himamis.retex.renderer.share;

import com.himamis.retex.renderer.share.platform.FactoryProvider;
import com.himamis.retex.renderer.share.platform.Graphics;
import com.himamis.retex.renderer.share.platform.font.Font;
import com.himamis.retex.renderer.share.platform.font.FontRenderContext;
import com.himamis.retex.renderer.share.platform.geom.Area;
import com.himamis.retex.renderer.share.platform.geom.Shape;
import com.himamis.retex.renderer.share.platform.graphics.Graphics2DInterface;

/**
 * A box representing a single character.
 */
public class CharBox extends Box {

	private static final FontRenderContext FRC;
	static {
		FRC = new Graphics().createImage(1, 1).createGraphics2D()
				.getFontRenderContext();
	}

	protected CharFont cf;
	protected double size;

	private final char[] arr = new char[1];

	protected CharBox() {
	}

	/**
	 * Create a new CharBox that will represent the character defined by the
	 * given Char-object.
	 *
	 * @param c
	 *            a Char-object containing the character's font information.
	 */
	public CharBox(Char c) {
		cf = c.getCharFont();
		size = c.getMetrics().getSize();
		width = c.getWidth();
		height = c.getHeight();
		depth = c.getDepth();
	}

	@Override
	public void addToWidth(final double x) {
		width += x;
	}

	@Override
	public void draw(Graphics2DInterface g2, double x, double y) {
		drawDebug(g2, x, y);
		g2.saveTransformation();
		g2.translate(x, y);
		Font font = cf.fontInfo.getFont();

		// https://github.com/opencollab/jlatexmath/issues/32
		int fontScale = font.getScale();

		if (fontScale != 1) {

			if (Math.abs(size - fontScale) > TeXFormula.PREC) {
				g2.scale(size / fontScale, size / fontScale);
			}

		} else {

			if (size != 1) {
				g2.scale(size, size);
			}

		}
		Font oldFont = g2.getFont();
		if (!oldFont.isEqual(font)) {
			g2.setFont(font);
		}

		arr[0] = cf.c;

		g2.drawChars(arr, 0, 1, 0, 0);

		if (!oldFont.isEqual(font)) {
			g2.setFont(oldFont);
		}
		g2.restoreTransformation();
	}

	@Override
	public Area getArea() {
		// final Font font = Configuration.get().getFont(cf.fontId);
		FontInfo info = cf.fontInfo;
		Font font = info.getFont();

		// can be null (if font not loaded - HTML5)
		final Shape s = font.getGlyphOutline(FRC, cf);

		final Area a = geom.createArea(s);
		final double x = size / FactoryProvider.getInstance().getFontFactory()
				.getFontScaleFactor();
		if (x != 1) {
			a.scale(x);
		}
		return a;
	}

	@Override
	public FontInfo getLastFont() {
		return cf.fontInfo;
	}

	@Override
	public String toString() {
		return super.toString() + "; char=" + cf.c;
	}

	@Override
	public void inspect(BoxConsumer handler, BoxPosition position) {
		super.inspect(handler, position.withScale(size));
	}
}
