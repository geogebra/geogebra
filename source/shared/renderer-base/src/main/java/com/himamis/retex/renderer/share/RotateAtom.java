/* RotateAtom.java
 * =========================================================================
 * This file is part of the JLaTeXMath Library - http://forge.scilab.org/jlatexmath
 *
 * Copyright (C) 2009-2018 DENIZET Calixte
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

import java.util.Map;

import com.himamis.retex.renderer.share.serialize.HasTrueBase;

/**
 * An atom representing a rotated Atom.
 */
public class RotateAtom extends Atom implements HasTrueBase {

	private Atom base;
	private double angle;
	private int option = -1;
	private Unit xunit;
	private Unit yunit;
	private double x, y;

	public RotateAtom() {
		//
	}

	public RotateAtom(Atom base, double angle, Map<String, String> map) {
		this.base = base;
		this.angle = angle;
		if (map.containsKey("origin")) {
			this.option = RotateBox.getOrigin(map.get("origin"));
		} else {
			TeXParser tp = null;
			if (map.containsKey("x")) {
				tp = new TeXParser();
				tp.setParseString(map.get("x"));
				final TeXLength lenX = tp.getLength();
				this.xunit = lenX.getUnit();
				this.x = lenX.getL();
			} else {
				this.xunit = Unit.POINT;
				this.x = 0.;
			}
			if (map.containsKey("y")) {
				if (tp == null) {
					tp = new TeXParser();
				}
				tp.setParseString(map.get("y"));
				final TeXLength lenY = tp.getLength();
				this.xunit = lenY.getUnit();
				this.x = lenY.getL();
			} else {
				this.yunit = Unit.POINT;
				this.y = 0.;
			}
		}
	}

	@Override
	public Box createBox(TeXEnvironment env) {
		if (option != -1) {
			return new RotateBox(base.createBox(env), angle, option);
		}
		return new RotateBox(base.createBox(env), angle,
				x * xunit.getFactor(env),
				y * yunit.getFactor(env));
	}

	@Override
	public int getLeftType() {
		return base.getLeftType();
	}

	@Override
	public int getRightType() {
		return base.getRightType();
	}

	@Override
	public int getLimits() {
		return base.getLimits();
	}

	@Override
	public Atom getTrueBase() {
		return base;
	}
}
