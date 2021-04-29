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
package com.himamis.retex.renderer.web.font;

import com.himamis.retex.renderer.share.CharFont;
import com.himamis.retex.renderer.share.platform.font.Font;
import com.himamis.retex.renderer.share.platform.font.FontRenderContext;
import com.himamis.retex.renderer.share.platform.geom.Shape;
import com.himamis.retex.renderer.web.geom.Rectangle2DW;

import elemental2.dom.CanvasRenderingContext2D;

public class DefaultFont extends FontW implements FontWrapper {

	public DefaultFont(String name, int style, int size) {
		super(name, style, size);
	}

	@Override
	public void addFontLoadedCallback(FontLoadCallback callback) {
		callback.onFontLoaded(this);

	}

	@Override
	public Font deriveFont(int type) {
		return new DefaultFont(name, type, size);
	}

	@Override
	public void drawGlyph(String c, int x, int y, int size, CanvasRenderingContext2D ctx) {
		FontW derived = new DefaultFont(name, style, size);
		try {
			ctx.setFont(derived.getCssFontString());
		} catch (Exception e) {
			// invisible frame in FF throws this
		}
		ctx.fillText(c, x, y);
	}

	@Override
	public FontWrapper getFontWrapper() {
		return this;
	}

	@Override
	public boolean isLoaded() {
		return true;
	}

	@Override
	public int getScale() {
		return 1;
	}

	@Override
	public Shape getGlyphOutline(FontRenderContext frc, CharFont cf) {
		int size = ((FontW) frc.getFont()).getSize();
		// estimate of size
		int height = size;
		int width = size;
		return new Rectangle2DW(0, -height, width, height);
	}

	public boolean canDisplay(char ch) {
		return true;
	}

	public boolean canDisplay(int c) {
		return true;
	}

}
