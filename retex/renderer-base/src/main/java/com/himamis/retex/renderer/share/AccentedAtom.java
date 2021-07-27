/* AccentedAtom.java
 * =========================================================================
 * This file is originally part of the JMathTeX Library - http://jmathtex.sourceforge.net
 *
 * Copyright (C) 2004-2007 Universiteit Gent
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

/**
 * An atom representing another atom with an accent symbol above it.
 */
public class AccentedAtom extends Atom {

	// accent symbol
	private final SymbolAtom accent;

	// base atom
	protected final Atom base;

	// extra skew
	protected double skew = Double.NaN;

	public AccentedAtom(Atom base, SymbolAtom accent) {
		this.base = base;
		this.accent = accent;
	}

	/**
	 * Creates an AccentedAtom from a base atom and an accent symbol defined by
	 * its name
	 *
	 * @param base
	 *            base atom
	 * @param accentName
	 *            name of the accent symbol to be put over the base atom
	 */
	public AccentedAtom(Atom base, String accentName) {
		this(base, SymbolAtom.get(accentName));
	}

	public void setSkew(final double skew) {
		this.skew = skew;
	}

	@Override
	public Box createBox(TeXEnvironment env) {
		final TeXFont tf = env.getTeXFont();
		final int style = env.getStyle();

		// set base in cramped style and remove italic correction
		Box b = base.createBox(env.crampStyle());
		double italic = 0.;
		if (!mustAddItalicCorrection()) {
			italic = base.getItalic(env);
		}

		double u = b.getWidth() + italic;
		double s;
		final Atom trueBase = getBase();
		if (trueBase instanceof CharSymbol) {
			s = tf.getSkew(((CharSymbol) trueBase).getCharFont(tf), style);
		} else {
			s = 0.;
		}

		if (!Double.isNaN(skew)) {
			s = skew * Unit.MU.getFactor(env) - s;
		}

		// TODO: maybe we've a bug here
		// we take xheight for the accent and not for its extension... wait and
		// see
		final double delta = Math.min(b.getHeight(), accent.getXHeight(env));

		// create vertical box
		VerticalBox vBox = new VerticalBox();

		// accent
		Box y = accent.getNextLarger(env, u);

		// if diff > 0, center accent, otherwise center base
		y.setShift(s + (u - y.getWidth()) / 2.);
		y.setWidth(0.);
		vBox.add(y);

		// kern
		vBox.add(new StrutBox(0., -delta, 0., 0.));
		// base
		vBox.add(b);

		// set height and depth vertical box
		final double total = vBox.getHeight() + vBox.getDepth();
		final double d = b.getDepth();
		vBox.setDepth(d);
		vBox.setHeight(total - d);

		return vBox.setAtom(this);
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
	public Atom getBase() {
		return base.getBase();
	}

	@Override
	public boolean mustAddItalicCorrection() {
		return base.mustAddItalicCorrection();
	}

	@Override
	public boolean setAddItalicCorrection(boolean b) {
		return base.setAddItalicCorrection(b);
	}

	/**
	 * @return accent
	 */
	public SymbolAtom getAccent() {
		return this.accent;
	}

}
