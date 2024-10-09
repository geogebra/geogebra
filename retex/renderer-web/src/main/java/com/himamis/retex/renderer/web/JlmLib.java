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

import com.himamis.retex.renderer.share.Colors;
import com.himamis.retex.renderer.share.TeXConstants;
import com.himamis.retex.renderer.share.TeXFormula;
import com.himamis.retex.renderer.share.TeXIcon;
import com.himamis.retex.renderer.share.platform.FactoryProvider;
import com.himamis.retex.renderer.share.platform.graphics.Color;
import com.himamis.retex.renderer.share.platform.graphics.Insets;
import com.himamis.retex.renderer.web.graphics.Graphics2DW;
import com.himamis.retex.renderer.web.graphics.JLMContextHelper;

import elemental2.dom.CanvasRenderingContext2D;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLCanvasElement;

public class JlmLib {

	private StringBuilder initString;

	public JlmLib() {
		initString = new StringBuilder();
	}

	public void initWith(String string) {
		initString.append(string);
	}

	public FormulaRenderingResult drawLatex(final CanvasRenderingContext2D ctx,
			final TeXFormula formula,
			final double size, final int type, final int x, final int y,
			final Insets insets, final String fgColorString,
			final String bgColorString, final DrawingFinishedCallback callback,
			HTMLCanvasElement canvasElement) {

		// init jlm with the given string
		if (initString.length() > 0) {
			new TeXFormula(initString.toString());
			initString.setLength(0);
		}
		// create icon and graphics objects
		TeXIcon icon = createIcon(formula, size, type, insets);
		if (canvasElement != null) {
			int iconWidth = icon.getIconWidth();
			canvasElement.width = (int) Math.ceil(iconWidth * getPixelRatio());
			int iconHeight = icon.getIconHeight();
			canvasElement.height = (int) Math.ceil(iconHeight * getPixelRatio());
			canvasElement.style.verticalAlign = (100 * icon.getBaseLine() - 100) + "%";
			canvasElement.style.setProperty("height", iconHeight + "px");
			canvasElement.style.setProperty("line-height", iconHeight + "px");
			canvasElement.style.setProperty("width", iconWidth + "px");
		}
		return draw(icon, ctx, x, y, fgColorString, bgColorString, callback);
	}

	public static double getPixelRatio() {
		return DomGlobal.window.devicePixelRatio;
	}


	public static FormulaRenderingResult draw(TeXIcon icon, CanvasRenderingContext2D ctx,
			final int x, final int y, final String fgColorString,
			final String bgColorString, final DrawingFinishedCallback callback) {
		return draw(icon, ctx, x, y, decode(fgColorString),
				decode(bgColorString), callback, getPixelRatio());
	}

	public static Color decode(String color) {
		return color == null ? null : Colors.decode(color);
	}

	public static FormulaRenderingResult draw(TeXIcon icon, CanvasRenderingContext2D ctx,
			final double x, final double y, final Color fgColor,
			final Color bgColor, final DrawingFinishedCallback callback,
			double ratio) {
		Graphics2DW g2 = new Graphics2DW(ctx);

		JLMContextHelper.as(ctx).setDevicePixelRatio(ratio);
		if (ratio != 1.0) {
			ctx.scale(ratio, ratio);
		}
		// fill the background color
		if (bgColor != null) {
			g2.setColor(bgColor);
			g2.fillRect(x, y, icon.getIconWidth(), icon.getIconHeight());
		}

		// set the callback
		g2.setDrawingFinishedCallback(callback);

		// paint the icon

		icon.paintIcon(() -> fgColor, g2, x, y);
		g2.maybeNotifyDrawingFinishedCallback(false);

		// return {width, height}
		return createReturnValue(icon, ratio);
	}

	public static TeXIcon createIcon(final TeXFormula formula, final double size,
			final int type, Insets insets) {
		TeXIcon icon = formula.new TeXIconBuilder()
				.setStyle(TeXConstants.STYLE_DISPLAY).setType(type)
				.setSize(size).build();
		icon.setInsets(insets);
		return icon;
	}

	private static FormulaRenderingResult createReturnValue(TeXIcon icon,
			double ratio) {
		FormulaRenderingResult object = new FormulaRenderingResult();
		object.width = icon.getIconWidth();
		object.height = icon.getIconHeight();
		object.baseline = icon.getBaseLine();
		object.pixelRatio = ratio;
		return object;
	}

	/**
	 * @param ascii AsciiMath formula
	 * @return renderable formula
	 */
	protected TeXFormula fromAsciiMath(String ascii) {
		FactoryProvider.debugS("ASCII math input not supported, falling back to LaTeX");
		return new TeXFormula(ascii);
	}
}
