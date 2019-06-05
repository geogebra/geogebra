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
package com.himamis.retex.renderer.share.platform.graphics;

import com.himamis.retex.renderer.share.platform.FactoryProvider;

public abstract class GraphicsFactory {
	// old
	/*
	 * static public int CURSOR_RED = 96; static public int CURSOR_GREEN = 96;
	 * static public int CURSOR_BLUE = 255;
	 */
	// teal default
	/*
	 * static public int CURSOR_RED = 0; static public int CURSOR_GREEN = 168;
	 * static public int CURSOR_BLUE = 168;
	 */
	// teal dark
	/*
	 * static public int CURSOR_RED = 0; static public int CURSOR_GREEN = 141;
	 * static public int CURSOR_BLUE = 141;
	 */
	// purple default
	/*
	 * static public int CURSOR_RED = 101; static public int CURSOR_GREEN = 87;
	 * static public int CURSOR_BLUE = 210;
	 */
	// purple dark
	static public final int CURSOR_RED = 76;
	static public final int CURSOR_GREEN = 66;
	static public final int CURSOR_BLUE = 161;

	public abstract BasicStroke createBasicStroke(double width, int cap,
			int join, double miterLimit);

	public abstract Color createColor(int red, int green, int blue);

	public abstract Image createImage(int width, int height, int type);

	public abstract Transform createTransform();

	public Color createColor(int rgb) {
		int red = (rgb >> 16) & 0xFF;
		int green = (rgb >> 8) & 0xFF;
		int blue = (rgb >> 0) & 0xFF;
		return createColor(red, green, blue);

	}

	public Color createColor(double r, double g, double b) {
		return createColor((int) (r * 255), (int) (g * 255), (int) (b * 255));
	}

	public Color createColor(int r, int g, int b, int alpha) {
		// XXX alpha ignored, TODO
		return createColor(r, g, b);
	}

	public Color createColor(double r, double g, double b, double a) {
		return createColor((int) r * 255, (int) g * 255, (int) b * 255,
				(int) a * 255);
	}

	public Color createColor(int rgba, boolean hasAlpha) {
		if (!hasAlpha) {
			return createColor(rgba);
		}
		int alpha = (rgba >> 24) & 0xFF;
		int red = (rgba >> 16) & 0xFF;
		int green = (rgba >> 8) & 0xFF;
		int blue = (rgba >> 0) & 0xFF;
		return createColor(red, green, blue, alpha);

	}

	public Image createImage(String path) {
		// implemented in desktop only
		return null;
	}

	public Stroke createBasicStroke(double width, float[] dashes) {
		// not implemented in iOS/Android
		FactoryProvider.debugS("dashed lines not implemented");
		return createBasicStroke(width, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER, 0);
	}

}
