/* CancelAtom.java
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

import com.himamis.retex.renderer.share.platform.graphics.Color;

/**
 * An atom representing a vertical row of other atoms.
 */
public class CancelAtom extends Atom {

	public static enum Type {
		SLASH, BACKSLASH, X
	}

	private static Color cancelColor = null;

	private final Atom base;
	private final Type ctype;
	private final Color color;

	public CancelAtom(final Atom base, final Type ctype, final Color color) {
		this.base = base;
		this.ctype = ctype;
		this.color = color;
	}

	public CancelAtom(final Atom base, final Type ctype) {
		this.base = base;
		this.ctype = ctype;
		this.color = cancelColor;
	}

	@Override
	public Box createBox(TeXEnvironment env) {
		final Box b = base.createBox(env);
		final double drt = env.getTeXFont()
				.getDefaultRuleThickness(env.getStyle());
		final double extra = new TeXLength(Unit.EX, 0.5)
				.getValue(env);
		return new CancelBox(b, ctype, drt, extra, color);
	}

	public static void handleColor(final TeXParser tp, final String code) {
		final SingleAtomConsumer sac = new SingleAtomConsumer();
		tp.addConsumer(sac);
		tp.addString(code, true);
		tp.parse();
		tp.pop();
		final Atom c = sac.get();
		if (c instanceof ColorAtom) {
			final ColorAtom ca = (ColorAtom) c;
			Color col = ca.getFg();
			if (col == null) {
				col = ca.getBg();
			}
			cancelColor = col;
		} else {
			cancelColor = null;
		}
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
	public double getItalic(TeXEnvironment env) {
		return base.getItalic(env);
	}

	@Override
	public double getXHeight(TeXEnvironment env) {
		return base.getXHeight(env);
	}

	@Override
	public boolean isMathMode() {
		return base.isMathMode();
	}

	@Override
	public void setMathMode(final boolean mathMode) {
		base.setMathMode(mathMode);
	}

	@Override
	public boolean mustAddItalicCorrection() {
		return base.mustAddItalicCorrection();
	}

	@Override
	public boolean setAddItalicCorrection(boolean b) {
		return base.setAddItalicCorrection(b);
	}

	@Override
	public Atom getBase() {
		return base.getBase();
	}

}
