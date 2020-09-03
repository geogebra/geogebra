/* Colors.java
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

import java.util.HashMap;
import java.util.Map;

import com.himamis.retex.renderer.share.platform.FactoryProvider;
import com.himamis.retex.renderer.share.platform.graphics.Color;
import com.himamis.retex.renderer.share.platform.graphics.GraphicsFactory;

/**
 * An atom representing the foreground and background color of an other atom.
 */
public class Colors {

	private static final GraphicsFactory GRAPHICS_FACTORY = FactoryProvider
			.getInstance().getGraphicsFactory();

	public static final Color RED = GRAPHICS_FACTORY.createColor(255, 0, 0);
	public static final Color BLACK = GRAPHICS_FACTORY.createColor(0, 0, 0);
	public static final Color WHITE = GRAPHICS_FACTORY.createColor(255, 255,
			255);
	public static final Color BLUE = GRAPHICS_FACTORY.createColor(0, 0, 255);
	public static final Color GREEN = GRAPHICS_FACTORY.createColor(0, 255, 0);
	public static final Color CYAN = GRAPHICS_FACTORY.createColor(0, 255, 255);
	public static final Color MAGENTA = GRAPHICS_FACTORY.createColor(255, 0,
			255);
	public static final Color YELLOW = GRAPHICS_FACTORY.createColor(255, 255,
			0);

	public static final Color SELECTION = GRAPHICS_FACTORY.createColor(204, 204, 255);

	private static Map<String, Color> all = new HashMap<String, Color>() {
		{
			GraphicsFactory g = GRAPHICS_FACTORY;

			// jlm v1 colors below
			put("black", BLACK);
			put("white", WHITE);
			put("red", RED);
			put("green", GREEN);
			put("blue", BLUE);
			put("cyan", CYAN);
			put("magenta", MAGENTA);
			put("yellow", YELLOW);

			put("greenyellow", g.createColor(217, 255, 79));
			put("goldenrod", g.createColor(255, 229, 41));
			put("dandelion", g.createColor(255, 181, 41));
			put("apricot", g.createColor(255, 173, 122));
			put("peach", g.createColor(255, 128, 77));
			put("melon", g.createColor(255, 138, 128));
			put("yelloworange", g.createColor(255, 148, 0));
			put("orange", g.createColor(255, 99, 33));
			put("burntorange", g.createColor(255, 125, 0));
			put("bittersweet", g.createColor(194, 48, 0));
			put("redorange", g.createColor(255, 59, 33));
			put("mahogany", g.createColor(166, 25, 22));
			put("maroon", g.createColor(173, 23, 55));
			put("brickred", g.createColor(184, 20, 11));
			put("orangered", g.createColor(255, 0, 128));
			put("rubinered", g.createColor(255, 0, 222));
			put("wildstrawberry", g.createColor(255, 10, 156));
			put("salmon", g.createColor(255, 120, 158));
			put("carnationpink", g.createColor(255, 94, 255));
			put("violetred", g.createColor(255, 48, 255));
			put("rhodamine", g.createColor(255, 46, 255));
			put("mulberry", g.createColor(165, 25, 250));
			put("redviolet", g.createColor(124, 21, 235));
			put("fuchsia", g.createColor(157, 17, 168));
			put("lavender", g.createColor(255, 133, 255));
			put("thistle", g.createColor(224, 105, 255));
			put("orchid", g.createColor(173, 92, 255));
			put("darkorchid", g.createColor(153, 51, 204));
			put("purple", g.createColor(140, 36, 255));
			put("plum", g.createColor(128, 0, 255));
			put("violet", g.createColor(54, 31, 255));
			put("royalpurple", g.createColor(64, 26, 255));
			put("blueviolet", g.createColor(34, 22, 245));
			put("periwinkle", g.createColor(110, 115, 255));
			put("cadetblue", g.createColor(97, 110, 196));
			put("cornflowerblue", g.createColor(89, 222, 255));
			put("midnightblue", g.createColor(3, 126, 145));
			put("navyblue", g.createColor(15, 117, 255));
			put("royalblue", g.createColor(0, 128, 255));
			put("cerulean", g.createColor(15, 227, 255));
			put("processblue", g.createColor(10, 255, 255));
			put("skyblue", g.createColor(97, 255, 224));
			put("turquoise", g.createColor(38, 255, 204));
			put("tealblue", g.createColor(35, 250, 165));
			put("aquamarine", g.createColor(46, 255, 178));
			put("bluegreen", g.createColor(38, 255, 171));
			put("emerald", g.createColor(0, 255, 128));
			put("junglegreen", g.createColor(3, 255, 122));
			put("seagreen", g.createColor(79, 255, 128));
			put("forestgreen", g.createColor(20, 224, 27));
			put("pinegreen", g.createColor(15, 191, 78));
			put("limegreen", g.createColor(128, 255, 0));
			put("yellowgreen", g.createColor(143, 255, 66));
			put("springgreen", g.createColor(189, 255, 61));
			put("olivegreen", g.createColor(55, 153, 8));
			put("rawsienna", g.createColor(140, 39, 0));
			put("sepia", g.createColor(77, 13, 0));
			put("brown", g.createColor(102, 19, 0));
			put("tan", g.createColor(219, 148, 112));
			put("gray", g.createColor(128, 128, 128));
		}
	};

	public static Color getFromName(final String name) {
		final Color c = all.get(name);
		if (c == null) {
			return all.get(name.toLowerCase());
		}
		return c;
	}

	public static void add(final String name, final Color color) {
		all.put(name, color);
	}

	public static int clamp(final int n) {
		return Math.min(255, Math.max(n, 0));
	}

	public static double clamp(final double n) {
		return Math.min(1., Math.max(n, 0.));
	}

	public static Color conv(final double c, final double m, final double y,
			final double k) {
		final double kk = 255. * (1. - k);
		final int R = (int) (kk * (1. - c) + 0.5);
		final int G = (int) (kk * (1. - m) + 0.5);
		final int B = (int) (kk * (1. - y) + 0.5);
		return FactoryProvider.getInstance().getGraphicsFactory()
				.createColor((R << 16) | (G << 8) | B);
	}

	public static Color convHSB(final double h, final double s,
			final double l) {
		final double h1 = normH(h);
		return FactoryProvider.getInstance().getGraphicsFactory()
				.createColor(HSBtoRGB(h1, s, l));
	}

	public static Color convHSL(final double h, final double s, final double l,
			final double a) {
		// https://www.w3.org/TR/css3-color/#hsl-color for algorithm
		final double ls = l * s;
		final double m2 = l + (l <= 0.5 ? ls : (s - ls));
		final double m1 = l * 2. - m2;
		final double h1 = normH(h);
		final float R = (float) HUEtoRGB(m1, m2, h1 + 1. / 3.);
		final float G = (float) HUEtoRGB(m1, m2, h1);
		final float B = (float) HUEtoRGB(m1, m2, h1 - 1. / 3.);

		return FactoryProvider.getInstance().getGraphicsFactory().createColor(R,
				G, B, (float) a);
	}

	public static Color convHSL(final double h, final double s,
			final double l) {
		return convHSL(h, s, l, 1f);
	}

	private static double HUEtoRGB(final double m1, final double m2, double h) {
		if (h < 0.) {
			h += 1.;
		} else if (h > 1.) {
			h -= 1.;
		}
		final double h6 = h * 6.;
		if (h6 < 1.) {
			return m1 + (m2 - m1) * h6;
		}
		if (h * 2. < 1.) {
			return m2;
		}
		if (h * 3. < 2.) {
			return m1 + (m2 - m1) * (4. - h6);
		}
		return m1;
	}

	private static double mod360(final double x) {
		return x - Math.floor(x / 360.) * 360.;
	}

	private static double normH(final double x) {
		return mod360(mod360(x) + 360.) / 360.;
	}

	private static double adjust(final double c, final double factor) {
		if (c == 0. || factor == 0.) {
			return 0.;
		}

		final double Gamma = 0.8;
		return Math.round(Math.pow(c * factor, Gamma));
	}

	public static Color convWave(final double waveLen) {
		double R, G, B;

		if (waveLen >= 380. && waveLen <= 439.) {
			R = -(waveLen - 440.) / 60.;
			G = 0.;
			B = 1.;
		} else if (waveLen >= 440. && waveLen <= 489.) {
			R = 0.;
			G = (waveLen - 440.) / 50.;
			B = 1.;
		} else if (waveLen >= 490. && waveLen <= 509.) {
			R = 0.;
			G = 1.;
			B = -(waveLen - 510.) / 20.;
		} else if (waveLen >= 510. && waveLen <= 579.) {
			R = (waveLen - 510.) / 70.;
			G = 1.;
			B = 0.;
		} else if (waveLen >= 580. && waveLen <= 644.) {
			R = 1.;
			G = -(waveLen - 645.) / 65.;
			B = 0.;
		} else if (waveLen >= 645. && waveLen <= 780.) {
			R = 1.;
			G = 0.;
			B = 0.;
		} else {
			R = 0.;
			G = 0.;
			B = 0.;
		}

		final double twave = Math.floor(waveLen);
		double factor;
		if (twave >= 380. && twave <= 419.) {
			factor = 0.3 + 0.7 * (waveLen - 380.) / 40.;
		} else if (twave >= 420. && twave <= 700.) {
			factor = 1.;
		} else if (twave >= 701. && twave <= 780.) {
			factor = 0.3 + 0.7 * (780. - waveLen) / 80.;
		} else {
			factor = 0.;
		}

		R = adjust(R, factor);
		G = adjust(G, factor);
		B = adjust(B, factor);

		return FactoryProvider.getInstance().getGraphicsFactory()
				.createColor((float) R, (float) G, (float) B);
	}

	public static Color decode(String string) {
		try {
			int val = Integer.decode(string);
			return GRAPHICS_FACTORY.createColor(
					(val >> 16) & 0xFF,
					(val >> 8) & 0xFF,
					val & 0xFF
			);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/**
	 * @param hue
	 *            (0-1)
	 * @param saturation
	 *            (0-1)
	 * @param brightness
	 *            (0-1)
	 * @return new color as ARGB
	 */
	public static int HSBtoRGB(double hue, double saturation,
			double brightness) {
		int r = 0, g = 0, b = 0;
		if (saturation == 0) {
			r = g = b = (int) (brightness * 255.0f + 0.5f);
		} else {
			double h = (hue - Math.floor(hue)) * 6.0f;
			double f = h - java.lang.Math.floor(h);
			double p = brightness * (1.0f - saturation);
			double q = brightness * (1.0f - saturation * f);
			double t = brightness * (1.0f - (saturation * (1.0f - f)));
			switch ((int) h) {
			default:
			case 0:
				r = (int) (brightness * 255.0f + 0.5f);
				g = (int) (t * 255.0f + 0.5f);
				b = (int) (p * 255.0f + 0.5f);
				break;
			case 1:
				r = (int) (q * 255.0f + 0.5f);
				g = (int) (brightness * 255.0f + 0.5f);
				b = (int) (p * 255.0f + 0.5f);
				break;
			case 2:
				r = (int) (p * 255.0f + 0.5f);
				g = (int) (brightness * 255.0f + 0.5f);
				b = (int) (t * 255.0f + 0.5f);
				break;
			case 3:
				r = (int) (p * 255.0f + 0.5f);
				g = (int) (q * 255.0f + 0.5f);
				b = (int) (brightness * 255.0f + 0.5f);
				break;
			case 4:
				r = (int) (t * 255.0f + 0.5f);
				g = (int) (p * 255.0f + 0.5f);
				b = (int) (brightness * 255.0f + 0.5f);
				break;
			case 5:
				r = (int) (brightness * 255.0f + 0.5f);
				g = (int) (p * 255.0f + 0.5f);
				b = (int) (q * 255.0f + 0.5f);
				break;
			}
		}
		return 0xff000000 | (r << 16) | (g << 8) | (b << 0);
	}
}
