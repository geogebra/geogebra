/**
 * This file is part of the ReTeX library - https://github.com/himamis/ReTeX
 *
 * Copyright (C) 2015 Balazs Bencze
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
package com.himamis.retex.renderer.desktop.font;

import java.util.HashMap;
import java.util.Map;

import com.himamis.retex.renderer.share.CharFont;
import com.himamis.retex.renderer.share.platform.font.Font;
import com.himamis.retex.renderer.share.platform.font.FontLoader;
import com.himamis.retex.renderer.share.platform.font.FontRenderContext;
import com.himamis.retex.renderer.share.platform.font.GlyphVector;
import com.himamis.retex.renderer.share.platform.font.TextAttribute;
import com.himamis.retex.renderer.share.platform.geom.Shape;

public class FontD implements Font {

	public java.awt.Font impl;

	public FontD(java.awt.Font impl) {
		this.impl = impl;
	}

	public FontD(String name, int style, int size) {
		impl = new java.awt.Font(name, style, size);
	}

	@Override
	public Font deriveFont(int type) {
		return new FontD(impl.deriveFont(type));
	}

	@Override
	public Font deriveFont(Map<TextAttribute, Object> map) {
		return new FontD(impl.deriveFont(convertMap(map)));
	}

	public java.awt.Font getFont() {
		return impl;
	}

	private static Map<java.awt.font.TextAttribute, Object> helper = new HashMap<java.awt.font.TextAttribute, Object>();

	private static Map<java.awt.font.TextAttribute, Object> convertMap(
			Map<TextAttribute, Object> map) {
		helper.clear();
		for (TextAttribute key : map.keySet()) {
			helper.put(((TextAttributeD) key).getTextAttribute(), map.get(key));
		}
		return helper;
	}

	@Override
	public boolean isEqual(Font f) {
		return impl.equals(((FontD) f).impl);
	}

	@Override
	public int getScale() {
		return FontLoader.FONT_SCALE_FACTOR;
	}

	public GlyphVector createGlyphVector(FontRenderContext frc, String s) {
		return new GlyphVectorD(impl
				.createGlyphVector((java.awt.font.FontRenderContext) frc, s));
	}

	@Override
	public Shape getGlyphOutline(FontRenderContext frc, CharFont cf) {
		return createGlyphVector(frc, cf.c + "").getGlyphOutline(0);
	}

	public int getSize() {
		return impl.getSize();
	}

	public String getName() {
		return impl.getName();
	}

	@Override
	public boolean canDisplay(char ch) {
		return impl.canDisplay(ch);
	}

	@Override
	public boolean canDisplay(int c) {
		return impl.canDisplay(c);
	}

}
