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
package com.himamis.retex.renderer.web.font.opentype;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.himamis.retex.renderer.share.CharFont;
import com.himamis.retex.renderer.share.platform.FactoryProvider;
import com.himamis.retex.renderer.share.platform.font.Font;
import com.himamis.retex.renderer.share.platform.font.FontRenderContext;
import com.himamis.retex.renderer.share.platform.geom.Rectangle2D;
import com.himamis.retex.renderer.share.platform.geom.Shape;
import com.himamis.retex.renderer.web.font.FontW;
import com.himamis.retex.renderer.web.font.FontWrapper;
import com.himamis.retex.renderer.web.geom.ShapeW;

public class OpentypeFont extends FontW implements OpentypeFontStatusListener {

	private Opentype opentype = Opentype.INSTANCE;
	private List<FontLoadCallback> fontLoadCallbacks;

	public OpentypeFont(String name, int style, int size) {
		super(name, style, size);
		fontLoadCallbacks = new ArrayList<FontLoadCallback>();
	}

	@Override
	public Font deriveFont(int type) {
		return new OpentypeFont(name, type, size);
	}

	@Override
	public boolean isLoaded() {
		return opentype.fontIsLoaded(name);
	}

	@Override
	public void addFontLoadedCallback(FontLoadCallback callback) {
		if (opentype.fontIsLoaded(name)) {
			callback.onFontLoaded(this);
		} else {
			if (fontLoadCallbacks == null) {
				callback.onFontError(this);
			} else {
				fontLoadCallbacks.add(callback);
				opentype.addListener(this);
			}
		}
	}

	@Override
	public FontWrapper getFontWrapper() {
		return opentype.getFont(name);
	}

	@Override
	public void onFontLoaded(OpentypeFontWrapper font, String familyName) {
		if (!familyName.equals(name)) {
			// not interested
			return;
		}
		FactoryProvider.debugS("Font " + name + " loaded");
		opentype.removeListener(this);
		for (FontLoadCallback fontLoadCallback : fontLoadCallbacks) {
			fontLoadCallback.onFontLoaded(this);
		}
		fontLoadCallbacks.clear();
		fontLoadCallbacks = null;
	}

	@Override
	public void onFontError(Object error, String familyName) {
		if (!familyName.equals(name)) {
			// not interested
			return;
		}
		FactoryProvider.debugS("Font " + name + " error");
		FactoryProvider.debugS(error.toString());
		opentype.removeListener(this);
		for (FontLoadCallback fontLoadCallback : fontLoadCallbacks) {
			fontLoadCallback.onFontError(this);
		}
		fontLoadCallbacks.clear();
		fontLoadCallbacks = null;
	}

	@Override
	public int getScale() {
		return 1;
	}

	@Override
	public Shape getGlyphOutline(FontRenderContext frc, CharFont cf) {
		FontW font = this;
		FontWrapper wrap = font.getFontWrapper();

		if (wrap == null) {
			// fail gracefully when font not loaded
			// will be tried again when font loaded on callback
			return null;
		}

		JavaScriptObject outline = wrap.getGlyphOutline(cf + "",
				font.getSize());

		double height = cf.fontInfo.getHeight(cf.c);
		double width = cf.fontInfo.getWidth(cf.c);

		Rectangle2D rect = FactoryProvider.getInstance().getGeomFactory()
				.createRectangle2D(0, 0, width, height);
		return new ShapeW(outline, rect);
	}

	@Override
	public boolean canDisplay(char ch) {
		return true;
	}

	@Override
	public boolean canDisplay(int c) {
		return true;
	}

}
