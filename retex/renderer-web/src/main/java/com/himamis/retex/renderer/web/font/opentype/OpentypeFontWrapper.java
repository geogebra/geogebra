/**
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

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.JavaScriptObject;
import com.himamis.retex.renderer.web.font.FontWrapper;

public class OpentypeFontWrapper implements FontWrapper {

	private JavaScriptObject impl;

	public OpentypeFontWrapper(JavaScriptObject impl) {
		this.impl = impl;
	}

	@Override
	public void drawGlyph(String c, int x, int y, int size, Context2d ctx) {
		drawGlyphNative(c, x, y, size, ctx);
	}

	public native void drawGlyphNative(String c, double x, double y,
			double size, Context2d ctx) /*-{
		var font = this.@com.himamis.retex.renderer.web.font.opentype.OpentypeFontWrapper::impl;
		// font not loaded yet
		if (!font) {
			return;
		}
		var glyph = this.@com.himamis.retex.renderer.web.font.opentype.OpentypeFontWrapper::getGlyph(Ljava/lang/String;)(c);
		if (glyph) {
			glyph.size = size;
			glyph.unitsPerEm = font[0];
			@com.himamis.retex.renderer.web.font.opentype.OpentypeFontWrapper::drawPath(Lcom/google/gwt/core/client/JavaScriptObject;IILcom/google/gwt/canvas/client/Canvas;)(glyph, x, y, ctx);
		}
	}-*/;

	@Override
	public native JavaScriptObject getGlyphOutline(String c, int size) /*-{
		var font = this.@com.himamis.retex.renderer.web.font.opentype.OpentypeFontWrapper::impl;
		// font not loaded yet
		if (!font) {
			return;
		}
		var glyph = this.@com.himamis.retex.renderer.web.font.opentype.OpentypeFontWrapper::getGlyph(Ljava/lang/String;)(c);
		if (!glyph) {
			return;
		}
		glyph.size = size;
		glyph.unitsPerEm = font[0];
		return glyph;
	}-*/;

	private JavaScriptObject getGlyph(String c) {
		return getGlyph(impl, c.codePointAt(0));
	}

	private static native JavaScriptObject getGlyph(JavaScriptObject font,
			int code) /*-{
		for (i = 1; i < font.length; i += 1) {
			var glyph = font[i];
			if (glyph[0] === code) {
				return font[i];
			}

			for (j = 0; j < glyph[0].length; j += 1) {
				if (glyph[0][j] === code) {
					return font[i];
				}
			}
		}
		return null;
	}-*/;

	public static native void drawPath(JavaScriptObject path, int x, int y,
			Canvas ctx) /*-{
		if (!path) {
			return;
		}

		var xScale = path.size / path.unitsPerEm;
		var yScale = path.size / path.unitsPerEm;

		ctx.beginPath();

		var j = 2;
		for (i = 0; i < path[1].length; i += 1) {
			var cmd = path[1][i][0];
			if (cmd === 'M') {
				ctx.moveTo(x + path[j] * xScale, y - path[j + 1] * yScale);
				j += 2;
			} else if (cmd === 'L') {
				ctx.lineTo(x + path[j] * xScale, y - path[j + 1] * yScale);
				j += 2;
			} else if (cmd === 'Q') {
				ctx.quadraticCurveTo(x + path[j + 2] * xScale, y - path[j + 3]
						* yScale, x + path[j] * xScale, y - path[j + 1]
						* yScale);
				j += 4;
			} else if (cmd === 'C') {
				ctx.bezierCurveTo(x + path[j + 2] * xScale, y - path[j + 3]
						* yScale, x + path[j + 4] * xScale, y - path[j + 5]
						* yScale, x + path[j] * xScale, y - path[j + 1]
						* yScale);
				j += 6;
			}
		}

		ctx.closePath();
		ctx.fill();
	}-*/;

}
