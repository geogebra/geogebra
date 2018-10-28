/* ColorAtom.java
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

import com.himamis.retex.renderer.share.platform.Graphics;
import com.himamis.retex.renderer.share.platform.graphics.Color;

/**
 * An atom representing the foreground and background color of an other atom.
 */
public class ColorAtom extends Atom implements Row {

	private static Map<String, Color> Colors;

	// background color
	private final Color background;

	// foreground color
	private final Color color;

	// RowAtom for which the colorsettings apply
	protected final RowAtom elements;

	@Override
	public Atom duplicate() {
		return setFields(new ColorAtom(elements, background, color));
	}

	/**
	 * Creates a new ColorAtom that sets the given colors for the given atom.
	 * Null for a color means: no specific color set for this atom.
	 *
	 * @param atom
	 *            the atom for which the given colors have to be set
	 * @param bg
	 *            the background color
	 * @param c
	 *            the foreground color
	 */
	public ColorAtom(Atom atom, Color bg, Color c) {
		elements = new RowAtom(atom);
		background = bg;
		color = c;
	}

	/**
	 * Creates a ColorAtom that overrides the colors of the given ColorAtom if
	 * the given colors are not null. If they're null, the old values are used.
	 *
	 * @param bg
	 *            the background color
	 * @param c
	 *            the foreground color
	 * @param old
	 *            the ColorAtom for which the colorsettings should be overriden
	 *            with the given colors.
	 */
	public ColorAtom(Color bg, Color c, ColorAtom old) {
		elements = new RowAtom(old.elements);
		background = (bg == null ? old.background : bg);
		color = (c == null ? old.color : c);
	}

	@Override
	public Box createBox(TeXEnvironment env) {
		env.isColored = true;
		TeXEnvironment copy = env.copy();
		if (background != null)
			copy.setBackground(background);
		if (color != null)
			copy.setColor(color);
		return elements.createBox(copy);
	}

	@Override
	public int getLeftType() {
		return elements.getLeftType();
	}

	@Override
	public int getRightType() {
		return elements.getRightType();
	}

	@Override
	public void setPreviousAtom(Dummy prev) {
		elements.setPreviousAtom(prev);
	}

	protected Color getColor() {
		return color;
	}

	protected Color getBackground() {
		return background;
	}

	protected Color getFg() {
		return color;
	}

	protected Color getBg() {
		return background;
	}

	public static Color getColor(String s) {
		Graphics graphics = new Graphics();
		if (s != null) {
			s = s.trim();
			if (s.length() >= 1) {
				if (s.charAt(0) == '#') {
					return ColorUtil.decode(s);
				} else if (s.indexOf(',') != -1 || s.indexOf(';') != -1) {
					String[] toks = s.split(";,");
					if (toks.length == 3) {
						// RGB model
						try {
							String R = toks[0].trim();
							String G = toks[1].trim();
							String B = toks[2].trim();

							double r = Double.parseDouble(R);
							double g = Double.parseDouble(G);
							double b = Double.parseDouble(B);

							if (r == (int) r && g == (int) g && b == (int) b
									&& R.indexOf('.') == -1
									&& G.indexOf('.') == -1
									&& B.indexOf('.') == -1) {
								int ir = (int) Math.min(255, Math.max(0, r));
								int ig = (int) Math.min(255, Math.max(0, g));
								int ib = (int) Math.min(255, Math.max(0, b));
								return graphics.createColor(ir, ig, ib);
							}
							r = Math.min(1, Math.max(0, r));
							g = Math.min(1, Math.max(0, g));
							b = Math.min(1, Math.max(0, b));
							return graphics.createColor(r, g, b);
						} catch (NumberFormatException e) {
							return ColorUtil.BLACK;
						}
					} else if (toks.length == 4) {
						// CMYK model
						try {
							double c = Double
									.parseDouble(toks[0].trim());
							double m = Double
									.parseDouble(toks[1].trim());
							double y = Double
									.parseDouble(toks[2].trim());
							double k = Double
									.parseDouble(toks[3].trim());

							c = Math.min(1, Math.max(0, c));
							m = Math.min(1, Math.max(0, m));
							y = Math.min(1, Math.max(0, y));
							k = Math.min(1, Math.max(0, k));

							return convColor(c, m, y, k);
						} catch (NumberFormatException e) {
							return ColorUtil.BLACK;
						}
					}
				}

				Color c = getColors().get(s.toLowerCase());
				if (c != null) {
					return c;
				}
				if (s.indexOf('.') != -1) {
					try {
						double g = Math.min(1,
								Math.max(Double.parseDouble(s), 0));

						return graphics.createColor(g, g, g);
					} catch (NumberFormatException e) {
						//
					}
				}

				return ColorUtil.decode("#" + s);
			}
		}

		return ColorUtil.BLACK;
	}

	private static void initColors() {
		Colors = new HashMap<String, Color>();

		Colors.put("black", ColorUtil.BLACK);
		Colors.put("white", ColorUtil.WHITE);
		Colors.put("red", ColorUtil.RED);
		Colors.put("green", ColorUtil.GREEN);
		Colors.put("blue", ColorUtil.BLUE);
		Colors.put("cyan", ColorUtil.CYAN);
		Colors.put("magenta", ColorUtil.MAGENTA);
		Colors.put("yellow", ColorUtil.YELLOW);

		Graphics g = new Graphics();

		Colors.put("greenyellow", g.createColor(217, 255, 79));
		Colors.put("goldenrod", g.createColor(255, 229, 41));
		Colors.put("dandelion", g.createColor(255, 181, 41));
		Colors.put("apricot", g.createColor(255, 173, 122));
		Colors.put("peach", g.createColor(255, 128, 77));
		Colors.put("melon", g.createColor(255, 138, 128));
		Colors.put("yelloworange", g.createColor(255, 148, 0));
		Colors.put("orange", g.createColor(255, 99, 33));
		Colors.put("burntorange", g.createColor(255, 125, 0));
		Colors.put("bittersweet", g.createColor(194, 48, 0));
		Colors.put("redorange", g.createColor(255, 59, 33));
		Colors.put("mahogany", g.createColor(166, 25, 22));
		Colors.put("maroon", g.createColor(173, 23, 55));
		Colors.put("brickred", g.createColor(184, 20, 11));
		Colors.put("orangered", g.createColor(255, 0, 128));
		Colors.put("rubinered", g.createColor(255, 0, 222));
		Colors.put("wildstrawberry", g.createColor(255, 10, 156));
		Colors.put("salmon", g.createColor(255, 120, 158));
		Colors.put("carnationpink", g.createColor(255, 94, 255));
		Colors.put("magenta", g.createColor(255, 0, 255));
		Colors.put("violetred", g.createColor(255, 48, 255));
		Colors.put("rhodamine", g.createColor(255, 46, 255));
		Colors.put("mulberry", g.createColor(165, 25, 250));
		Colors.put("redviolet", g.createColor(124, 21, 235));
		Colors.put("fuchsia", g.createColor(157, 17, 168));
		Colors.put("lavender", g.createColor(255, 133, 255));
		Colors.put("thistle", g.createColor(224, 105, 255));
		Colors.put("orchid", g.createColor(173, 92, 255));
		Colors.put("darkorchid", g.createColor(153, 51, 204));
		Colors.put("purple", g.createColor(140, 36, 255));
		Colors.put("plum", g.createColor(128, 0, 255));
		Colors.put("violet", g.createColor(54, 31, 255));
		Colors.put("royalpurple", g.createColor(64, 26, 255));
		Colors.put("blueviolet", g.createColor(34, 22, 245));
		Colors.put("periwinkle", g.createColor(110, 115, 255));
		Colors.put("cadetblue", g.createColor(97, 110, 196));
		Colors.put("cornflowerblue", g.createColor(89, 222, 255));
		Colors.put("midnightblue", g.createColor(3, 126, 145));
		Colors.put("navyblue", g.createColor(15, 117, 255));
		Colors.put("royalblue", g.createColor(0, 128, 255));
		Colors.put("cerulean", g.createColor(15, 227, 255));
		Colors.put("processblue", g.createColor(10, 255, 255));
		Colors.put("skyblue", g.createColor(97, 255, 224));
		Colors.put("turquoise", g.createColor(38, 255, 204));
		Colors.put("tealblue", g.createColor(35, 250, 165));
		Colors.put("aquamarine", g.createColor(46, 255, 178));
		Colors.put("bluegreen", g.createColor(38, 255, 171));
		Colors.put("emerald", g.createColor(0, 255, 128));
		Colors.put("junglegreen", g.createColor(3, 255, 122));
		Colors.put("seagreen", g.createColor(79, 255, 128));
		Colors.put("forestgreen", g.createColor(20, 224, 27));
		Colors.put("pinegreen", g.createColor(15, 191, 78));
		Colors.put("limegreen", g.createColor(128, 255, 0));
		Colors.put("yellowgreen", g.createColor(143, 255, 66));
		Colors.put("springgreen", g.createColor(189, 255, 61));
		Colors.put("olivegreen", g.createColor(55, 153, 8));
		Colors.put("rawsienna", g.createColor(140, 39, 0));
		Colors.put("sepia", g.createColor(77, 13, 0));
		Colors.put("brown", g.createColor(102, 19, 0));
		Colors.put("tan", g.createColor(219, 148, 112));
		Colors.put("gray", g.createColor(128, 128, 128));
	}

	private static Color convColor(final double c, final double m,
			final double y, final double k) {
		final double kk = 1d - k;
		return new Graphics().createColor(kk * (1d - c), kk * (1d - m),
				kk * (1d - y));
	}

	/**
	 * @return colors hashmap
	 */
	public static Map<String, Color> getColors() {
		if (Colors == null) {
			initColors();
		}

		return Colors;
	}

}