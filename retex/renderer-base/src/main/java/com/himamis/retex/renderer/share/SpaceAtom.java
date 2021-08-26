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

/**
 * An atom representing whitespace. The dimension values can be set using
 * different unit types.
 */
public class SpaceAtom extends Atom {

	// whether a hard space should be represented
	private boolean blankSpace;

	// thinmuskip, medmuskip, thickmuskip
	private TeXConstants.Muskip blankType = TeXConstants.Muskip.NONE;

	// dimensions
	private double width;
	private double height;
	private double depth;

	// units for the dimensions
	private Unit unit;

	public SpaceAtom() {
		blankSpace = true;
	}

	public SpaceAtom(TeXConstants.Muskip type) {
		blankSpace = true;
		blankType = type;
	}

	public SpaceAtom(Unit unit, double width, double height,
			double depth) {
		this.unit = unit;
		this.width = width;
		this.height = height;
		this.depth = depth;
	}

	public SpaceAtom(Unit unit, double width) {
		this.unit = unit;
		this.width = width;
		this.height = 0.;
		this.depth = 0.;
	}

	public SpaceAtom(TeXLength l) {
		this.unit = l.getUnit();
		this.width = l.getL();
		this.height = 0.;
		this.depth = 0.;
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
				return new StrutBox(env.getSpace(), 0., 0., 0.).setAtom(this);
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

	private final double conv(final double x, final Unit unit,
			final TeXEnvironment env) {
		return x == 0. ? 0. : x * unit.getFactor(env);
	}

	public double getHeight() {
		return height;
	}

	public Unit getUnit() {
		return unit;
	}

}
