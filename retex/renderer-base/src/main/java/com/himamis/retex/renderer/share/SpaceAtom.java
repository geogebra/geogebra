/* SpaceAtom.java
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

/* Modified by Calixte Denizet */

package com.himamis.retex.renderer.share;

import java.util.HashMap;
import java.util.Map;

import com.himamis.retex.renderer.share.TeXConstants.Muskip;
import com.himamis.retex.renderer.share.TeXLength.Unit;

/**
 * An atom representing whitespace. The dimension values can be set using different unit types.
 */
public class SpaceAtom extends Atom {

	private static Map<String, TeXLength.Unit> units = new HashMap<String, TeXLength.Unit>();
	static {
		units.put("em", TeXLength.Unit.EM);
		units.put("ex", TeXLength.Unit.EX);
		units.put("px", TeXLength.Unit.PIXEL);
		units.put("pix", TeXLength.Unit.PIXEL);
		units.put("pixel", TeXLength.Unit.PIXEL);
		units.put("pt", TeXLength.Unit.PT);
		units.put("bp", TeXLength.Unit.POINT);
		units.put("pica", TeXLength.Unit.PICA);
		units.put("pc", TeXLength.Unit.PICA);
		units.put("mu", TeXLength.Unit.MU);
		units.put("cm", TeXLength.Unit.CM);
		units.put("mm", TeXLength.Unit.MM);
		units.put("in", TeXLength.Unit.IN);
		units.put("sp", TeXLength.Unit.SP);
		units.put("dd", TeXLength.Unit.DD);
		units.put("cc", TeXLength.Unit.CC);
	}

	private static interface UnitConversion { // NOPMD
		public double getPixelConversion(TeXEnvironment env);
	}

	private static UnitConversion[] unitConversions = new UnitConversion[] {

	new UnitConversion() {// EM
				@Override
				public double getPixelConversion(TeXEnvironment env) {
					return env.getTeXFont().getEM(env.getStyle());
				}
			},

			new UnitConversion() {// EX
				@Override
				public double getPixelConversion(TeXEnvironment env) {
					return env.getTeXFont().getXHeight(env.getStyle(), env.getLastFontId());
				}
			},

			new UnitConversion() {// PIXEL
				@Override
				public double getPixelConversion(TeXEnvironment env) {
					return 1 / env.getSize();
				}
			},

			new UnitConversion() {// BP (or PostScript point)
				@Override
				public double getPixelConversion(TeXEnvironment env) {
					return TeXFormula.PIXELS_PER_POINT / env.getSize();
				}
			},

			new UnitConversion() {// PICA
				@Override
				public double getPixelConversion(TeXEnvironment env) {
					return (12 * TeXFormula.PIXELS_PER_POINT) / env.getSize();
				}
			},

			new UnitConversion() {// MU
				@Override
				public double getPixelConversion(TeXEnvironment env) {
					TeXFont tf = env.getTeXFont();
					return tf.getQuad(env.getStyle(), tf.getMuFontId()) / 18;
				}
			},

			new UnitConversion() {// CM
				@Override
				public double getPixelConversion(TeXEnvironment env) {
					return (28.346456693f * TeXFormula.PIXELS_PER_POINT) / env.getSize();
				}
			},

			new UnitConversion() {// MM
				@Override
				public double getPixelConversion(TeXEnvironment env) {
					return (2.8346456693f * TeXFormula.PIXELS_PER_POINT) / env.getSize();
				}
			},

			new UnitConversion() {// IN
				@Override
				public double getPixelConversion(TeXEnvironment env) {
					return (72 * TeXFormula.PIXELS_PER_POINT) / env.getSize();
				}
			},

			new UnitConversion() {// SP
				@Override
				public double getPixelConversion(TeXEnvironment env) {
					return (65536 * TeXFormula.PIXELS_PER_POINT) / env.getSize();
				}
			},

			new UnitConversion() {// PT (or Standard Anglo-American point)
				@Override
				public double getPixelConversion(TeXEnvironment env) {
					return (.9962640099f * TeXFormula.PIXELS_PER_POINT) / env.getSize();
				}
			},

			new UnitConversion() {// DD
				@Override
				public double getPixelConversion(TeXEnvironment env) {
					return (1.0660349422f * TeXFormula.PIXELS_PER_POINT) / env.getSize();
				}
			},

			new UnitConversion() {// CC
				@Override
				public double getPixelConversion(TeXEnvironment env) {
					return (12.7924193070f * TeXFormula.PIXELS_PER_POINT) / env.getSize();
				}
			}, new UnitConversion() {// X8
				public double getPixelConversion(TeXEnvironment env) {
				                 return env.getTeXFont().getDefaultRuleThickness(env.getStyle());
				             }
			} };

	// whether a hard space should be represented
	private boolean blankSpace;

	// thinmuskip, medmuskip, thickmuskip
	private TeXConstants.Muskip blankType = TeXConstants.Muskip.NONE;

	// dimensions
	private double width;
	private double height;
	private double depth;

	// units for the dimensions
	private TeXLength.Unit unit;

	@Override
	final public Atom duplicate() {
		SpaceAtom ret = new SpaceAtom();
		
		ret.blankSpace = blankSpace;
		ret.blankType = blankType;
		ret.width = width;
		ret.height = height;
		ret.depth = depth;
		ret.unit = unit;
		
		return setFields(ret);
	}

	public SpaceAtom() {
		blankSpace = true;
	}

	public SpaceAtom(Muskip type) {
		blankSpace = true;
		blankType = type;
	}

	public SpaceAtom(Unit unit, double width, double height, double depth) {
		this.unit = unit;
		this.width = width;
		this.height = height;
		this.depth = depth;
	}

	public static Unit getUnit(String unit) {
		Unit u = units.get(unit);
		return u == null ? TeXLength.Unit.PIXEL : u;
	}

	public static Object[] getLength(String lgth) {
		if (lgth == null) {
			return new Object[] { TeXLength.Unit.PIXEL, 0f };
		}

		int i = 0;
		for (; i < lgth.length() && !Character.isLetter(lgth.charAt(i)); i++) {
			;
		}
		double f = 0;
		try {
			f = Double.parseDouble(lgth.substring(0, i));
		} catch (NumberFormatException e) {
			return new Object[] { Double.NaN };
		}

		Unit unit;
		if (i != lgth.length()) {
			unit = getUnit(lgth.substring(i).toLowerCase());
		} else {
			unit = TeXLength.Unit.PIXEL;
		}

		return new Object[] { unit, f };
	}

	public static boolean isNegative(TeXConstants.Muskip skip) {
		return skip == TeXConstants.Muskip.NEGTHIN
				|| skip == TeXConstants.Muskip.NEGMED
				|| skip == TeXConstants.Muskip.NEGTHICK;
	}

	@Override
	public Box createBox(TeXEnvironment env) {
		if (blankSpace) {
			if (blankType == TeXConstants.Muskip.NONE) {
				return new StrutBox(env.getSpace(), 0., 0., 0.);
			} else {
				Box b;
				if (blankType == TeXConstants.Muskip.THIN
						|| blankType == TeXConstants.Muskip.NEGTHIN) {
					b = Glue.get(TeXConstants.TYPE_INNER,
							TeXConstants.TYPE_BIG_OPERATOR, env);
				} else if (blankType == TeXConstants.Muskip.MED
						|| blankType == TeXConstants.Muskip.NEGMED) {
					b = Glue.get(TeXConstants.TYPE_BINARY_OPERATOR,
							TeXConstants.TYPE_BIG_OPERATOR, env);
				} else {
					b = Glue.get(TeXConstants.TYPE_RELATION,
							TeXConstants.TYPE_BIG_OPERATOR, env);
				}
				if (b == null) {
					b = StrutBox.getEmpty();
				}
				if (SpaceAtom.isNegative(blankType)) {
					b.negWidth();
				}
				return b;
			}
		} else {
			return new StrutBox(conv(width, unit, env), conv(height, unit, env),
					conv(depth, unit, env), 0.);
		}
	}

	public static double getFactor(Unit unit, TeXEnvironment env) {
		return unitConversions[unit.ordinal()].getPixelConversion(env);
	}

	private final double conv(final double x, final TeXLength.Unit unit,
			final TeXEnvironment env) {
		return x == 0. ? 0. : x * TeXLength.getFactor(unit, env);
	}
}
