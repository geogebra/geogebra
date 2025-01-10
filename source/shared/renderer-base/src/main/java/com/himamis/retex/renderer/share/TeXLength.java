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

public class TeXLength {

	private static final TeXLength zero = new TeXLength();

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
		return l * unit.getFactor(env);
	}

	public TeXLength scale(final double factor) {
		return new TeXLength(unit, l * factor);
	}

	public static TeXLength getZero() {
		return zero;
	}

	@Override
	public String toString() {
		return Double.toString(getL()) + unit.toString();
	}

	private static int getIntPart(double x) {
		return (int) (x >= 0. ? Math.floor(x) : -Math.floor(-x));
	}

	private static int getDecPart(double x) {
		final double frac = Math.abs(x - getIntPart(x));
		int part = (int) Math.round(frac * Math.pow(10, TeXParser.MAX_DEC));
		while (part != 0 && (part % 10 == 0)) {
			part /= 10;
		}
		return part;
	}

	public Atom toAtom() {
		RowAtom ra = new RowAtom();
		final double l = getL();
		final int frac = TeXLength.getDecPart(l);
		final int inte = TeXLength.getIntPart(l);
		if (inte < 0) {
			ra.add(Symbols.MINUS);
			TeXParser.getAtomForNumber(-inte, ra, true);
		} else {
			TeXParser.getAtomForNumber(inte, ra, true);
		}
		if (frac != 0) {
			ra.add(Symbols.NORMALDOT);
			TeXParser.getAtomForNumber(frac, ra, true);
		}
		final String u = unit.toString();
		if (!u.isEmpty()) {
			ra.add(new RomanAtom(TeXParser.getAtomForLatinStr(u, false)));
		}
		return ra;
	}
}