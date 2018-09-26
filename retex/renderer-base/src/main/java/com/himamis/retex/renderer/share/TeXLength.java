/* TeXLength.java
 * =========================================================================
 * This file is part of the JLaTeXMath Library - http://forge.scilab.org/jlatexmath
 *
 * Copyright (C) 2018 DENIZET Calixte
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

public class TeXLength {

	public static enum Unit {
		EM, // 1 em = the width of the capital 'M' in the current font
		EX, // 1 ex = the height of the character 'x' in the current font
		PIXEL, //
		POINT, // postscript point
		PICA, // 1 pica = 12 point
		MU, // 1 mu = 1/18 em (em taken from the "mufont")
		CM, // 1 cm = 28.346456693 point
		MM, // 1 mm = 2.8346456693 point
		IN, // 1 in = 72 point
		SP, // 1 sp = 65536 point
		PT, // 1 pt = 1/72.27 in(or Standard Anglo-American point)
		DD, //
		CC, //
		X8, // 1 x8 = 1 default rule thickness
		NONE
	};

	private static final Map<String, TeXLength> map = new HashMap<String, TeXLength>() {
		{

			// value compatible with JLaTeXMath v1
			// put("fboxsep", new TeXLength(TeXLength.Unit.EM, 0.65));
			// to change it, do this in your code
			// TeXLength.put("fboxsep", new TeXLength(TeXLength.Unit.EM, 0.65));
			// changed for v2 to be more correct
			put("fboxsep", new TeXLength(Unit.PT, 3.));
			put("scriptspace", new TeXLength(Unit.PT, 0.5));
			put("nulldelimiterspace", new TeXLength(Unit.PT, 1.2));
			put("delimitershortfall", new TeXLength(Unit.PT, 5.));
			put("delimiterfactor", new TeXLength(Unit.NONE, 901.));
			// put("textwidth", new TeXLength(Unit.NONE,
			// Double.POSITIVE_INFINITY));
		}
	};

	private static final TeXLength zero = new TeXLength();
	private static final TeXLength none = new TeXLength(Unit.NONE, 0.);
	private final Unit unit;
	private final double l;

	public TeXLength() {
		this.unit = Unit.PIXEL;
		this.l = 0.;
	}

	public TeXLength(Unit unit, double l) {
		this.unit = unit;
		this.l = l;
	}

	public Unit getUnit() {
		return unit;
	}

	public double getL() {
		return l;
	}

	public double getValue(TeXEnvironment env) {
		return l * TeXLength.getFactor(unit, env);
	}

	public TeXLength scale(final double factor) {
		return new TeXLength(unit, l * factor);
	}

	public boolean isNone() {
		return unit == Unit.NONE;
	}

	public static TeXLength getZero() {
		return zero;
	}

	public static TeXLength getNone() {
		return none;
	}

	public static double getFactor(Unit unit, TeXEnvironment env) {
		switch (unit) {
		case EM:
			return env.getTeXFont().getEM(env.getStyle());
		case EX:
			return env.getTeXFont().getXHeight(env.getStyle(),
					env.getLastFontId());
		case PIXEL:
			return 1. / env.getSize();
		case POINT:
			return TeXFormula.PIXELS_PER_POINT / env.getSize();
		case PICA:
			return (12. * TeXFormula.PIXELS_PER_POINT) / env.getSize();
		case MU:
			final TeXFont tf = env.getTeXFont();
			return tf.getQuad(env.getStyle(), TeXFont.MUFONT) / 18.;
		case CM:
			return (28.346456693 * TeXFormula.PIXELS_PER_POINT) / env.getSize();
		case MM:
			return (2.8346456693 * TeXFormula.PIXELS_PER_POINT) / env.getSize();
		case IN:
			return (72. * TeXFormula.PIXELS_PER_POINT) / env.getSize();
		case SP:
			return (65536. * TeXFormula.PIXELS_PER_POINT) / env.getSize();
		case PT:
			return (0.9962640099 * TeXFormula.PIXELS_PER_POINT) / env.getSize();
		case DD:
			return (1.0660349422 * TeXFormula.PIXELS_PER_POINT) / env.getSize();
		case CC:
			return (12.7924193070 * TeXFormula.PIXELS_PER_POINT)
					/ env.getSize();
		case X8:
			return env.getTeXFont().getDefaultRuleThickness(env.getStyle());
		case NONE:
			return 1.;
		default:
			return 0.;
		}
	}

	public static double getLength(final String name,
			final TeXEnvironment env) {
		final TeXLength l = map.get(name);
		if (l != null) {
			return l.getL() * getFactor(l.getUnit(), env);
		}
		return 0.;
	}

	public static TeXLength getLength(final String name, final double factor) {
		final TeXLength l = map.get(name);
		if (l != null) {
			return l.scale(factor);
		}
		return null;
	}

	public static void setLength(final String name, final TeXLength l) {
		if (l != null) {
			map.put(name, l);
		}
	}

	public static boolean isLengthName(final String name) {
		return map.containsKey(name);
	}

	@Override
	public String toString() {
		return l + "_" + unit;
	}

	public static void put(String s, TeXLength len) {
		map.put(s, len);
	}
}
