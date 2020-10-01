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
package com.himamis.retex.renderer.web;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.JavaScriptObject;
import com.himamis.retex.renderer.share.Colors;
import com.himamis.retex.renderer.share.TeXConstants;
import com.himamis.retex.renderer.share.TeXFormula;
import com.himamis.retex.renderer.share.TeXIcon;
import com.himamis.retex.renderer.share.platform.graphics.Color;
import com.himamis.retex.renderer.share.platform.graphics.HasForegroundColor;
import com.himamis.retex.renderer.share.platform.graphics.Insets;
import com.himamis.retex.renderer.web.graphics.Graphics2DW;
import com.himamis.retex.renderer.web.graphics.JLMContext2d;

import elemental2.dom.DomGlobal;
import jsinterop.base.JsPropertyMap;

public class JlmLib {

	private StringBuilder initString;

	public JlmLib() {
		initString = new StringBuilder();
	}

	public void initWith(String string) {
		initString.append(string);
	}

	public JsPropertyMap<Object> drawLatex(final Context2d ctx, final String latex,
			final double size, final int type, final int x, final int y,
			final int topInset, final int leftInset, final int bottomInset,
			final int rightInset, final String fgColorString,
			final String bgColorString, final JavaScriptObject callback) {

		// init jlm with the given string
		if (initString.length() > 0) {
			new TeXFormula(initString.toString());
			initString.setLength(0);
		}
		// create icon and graphics objects
		TeXIcon icon = createIcon(latex, size, type,
				new Insets(topInset, leftInset, bottomInset, rightInset));
		return draw(icon, ctx, x, y, fgColorString, bgColorString, callback);
	}

	public static double getPixelRatio() {
		return DomGlobal.window.devicePixelRatio;
	}

	public static JsPropertyMap<Object> draw(TeXIcon icon, Context2d ctx,
			final int x, final int y, final String fgColorString,
			final String bgColorString, final JavaScriptObject callback) {
		return draw(icon, ctx, x, y, Colors.decode(fgColorString),
				Colors.decode(bgColorString), callback, getPixelRatio());
	}

	public static JsPropertyMap<Object> draw(TeXIcon icon, Context2d ctx,
			final int x, final int y, final Color fgColor,
			final Color bgColor, final JavaScriptObject callback,
			double ratio) {
		Graphics2DW g2 = new Graphics2DW(ctx);

		((JLMContext2d) ctx).setDevicePixelRatio(ratio);
		ctx.scale(ratio, ratio);
		// fill the background color
		if (bgColor != null) {
			g2.setColor(bgColor);
			g2.fillRect(x, y, icon.getIconWidth(), icon.getIconHeight());
		}

		// set the callback
		g2.setDrawingFinishedCallback(async -> callJavascriptCallback(callback, async));

		// paint the icon

		icon.paintIcon(new HasForegroundColor() {
			@Override
			public Color getForegroundColor() {
				return fgColor;
			}
		}, g2, x, y);
		g2.maybeNotifyDrawingFinishedCallback(false);

		// return {width, height}
		return createReturnValue(icon, ratio);
	}

	private static native void callJavascriptCallback(JavaScriptObject cb,
			boolean async) /*-{
		if (cb != null) {
			cb(async);
		}
	}-*/;

	public static TeXIcon createIcon(final String latex, final double size,
			final int type, Insets insets) {
		TeXFormula formula = new TeXFormula(latex);
		TeXIcon icon = formula.new TeXIconBuilder()
				.setStyle(TeXConstants.STYLE_DISPLAY).setType(type)
				.setSize(size).build();
		icon.setInsets(insets);
		return icon;
	}

	private static JsPropertyMap<Object> createReturnValue(TeXIcon icon,
			double ratio) {
		JsPropertyMap<Object> object = JsPropertyMap.of();
		object.set("width", icon.getIconWidth());
		object.set("height", icon.getIconHeight());
		object.set("baseline", icon.getBaseLine());
		object.set("pixelRatio", ratio);
		return object;
	}
}
