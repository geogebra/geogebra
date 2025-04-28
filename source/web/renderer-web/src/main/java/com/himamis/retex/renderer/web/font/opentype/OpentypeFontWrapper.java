/*
 * This file is part of the ReTeX library - https://github.com/himamis/ReTeX
 * <p>
 * Copyright (C) 2015 Balazs Bencze
 * <p>
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * <p>
 * A copy of the GNU General Public License can be found in the file
 * LICENSE.txt provided with the source distribution of this program (see
 * the META-INF directory in the source jar). This license can also be
 * found on the GNU website at http://www.gnu.org/licenses/gpl.html.
 * <p>
 * If you did not receive a copy of the GNU General Public License along
 * with this program, contact the lead developer, or write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 * <p>
 * Linking this library statically or dynamically with other modules
 * is making a combined work based on this library. Thus, the terms
 * and conditions of the GNU General Public License cover the whole
 * combination.
 * <p>
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
 */
package com.himamis.retex.renderer.web.font.opentype;

import com.himamis.retex.renderer.web.font.FontWrapper;
import com.himamis.retex.renderer.web.graphics.FontGlyph;

import elemental2.core.JsArray;
import elemental2.dom.CanvasRenderingContext2D;
import jsinterop.base.Js;

public class OpentypeFontWrapper implements FontWrapper {

	private JsArray<Object> impl;

	public OpentypeFontWrapper(JsArray<Object> impl) {
		this.impl = impl;
	}

	@Override
	public void drawGlyph(String c, int x, int y, int size, CanvasRenderingContext2D ctx) {
		drawGlyphNative(c, x, y, size, ctx);
	}

	public void drawGlyphNative(String c, double x, double y,
			double size, CanvasRenderingContext2D ctx) {
		// font not loaded yet
		if (impl == null) {
			return;
		}
		FontGlyph glyph = getGlyph(c);
		if (glyph != null) {
			glyph.size = size;
			glyph.unitsPerEm = (double) impl.getAt(0);
			drawPath(glyph, x, y, ctx);
		}
	}

	public FontGlyph getGlyphOutline(String c, int size) {
		// font not loaded yet
		if (impl == null) {
			return null;
		}
		FontGlyph glyph = getGlyph(c);
		if (glyph == null) {
			return null;
		}
		glyph.size = size;
		glyph.unitsPerEm = (double) impl.getAt(0);
		return glyph;
	};

	private FontGlyph getGlyph(String c) {
		return getGlyph(impl, c.codePointAt(0));
	}

	private static FontGlyph getGlyph(JsArray<Object> font,
			int code) {
		for (int i = 1; i < font.length; i += 1) {
			FontGlyph glyph = Js.uncheckedCast(font.getAt(i));
			Object at = glyph.getAt(0);
			if (Js.asInt(at) == code) {
				// no path => pointer to the next glyph
				return "undefined".equals(Js.typeof(glyph.getAt(1)))
						? Js.uncheckedCast(font.getAt(i + 1)) : glyph;
			}
		}
		return null;
	}

	public static void drawPath(FontGlyph path, double x, double y,
			CanvasRenderingContext2D ctx) {
		if (Js.isFalsy(path)) {
			return;
		}

		double xScale = path.size / path.unitsPerEm;
		double yScale = path.size / path.unitsPerEm;

		ctx.beginPath();

		int j = 2;
		String types = (String) path.getAt(1);
		JsArray<Double> dPath = Js.uncheckedCast(path);
		for (int i = 0; i < types.length(); i += 1) {
			char cmd = types.charAt(i);
			if (cmd == 'M') {
				ctx.moveTo(x + dPath.getAt(j) * xScale, y - dPath.getAt(j + 1) * yScale);
				j += 2;
			} else if (cmd == 'L') {
				ctx.lineTo(x + dPath.getAt(j) * xScale, y - dPath.getAt(j + 1) * yScale);
				j += 2;
			} else if (cmd == 'Q') {
				ctx.quadraticCurveTo(x + dPath.getAt(j + 2) * xScale, y - dPath.getAt(j + 3)
						* yScale, x + dPath.getAt(j) * xScale, y - dPath.getAt(j + 1)
						* yScale);
				j += 4;
			} else if (cmd == 'C') {
				ctx.bezierCurveTo(x + dPath.getAt(j + 2) * xScale, y - dPath.getAt(j + 3)
						* yScale, x + dPath.getAt(j + 4) * xScale, y - dPath.getAt(j + 5)
						* yScale, x + dPath.getAt(j) * xScale, y - dPath.getAt(j + 1)
						* yScale);
				j += 6;
			}
		}

		ctx.closePath();
		ctx.fill();
	}

}
