/* BigOperatorAtom.java
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

/* Modified by Calixte Denizet */

package com.himamis.retex.renderer.share;

import com.himamis.retex.renderer.share.serialize.HasTrueBase;

/**
 * An atom representing a "big operator" (or an atom that acts as one) together
 * with its limits.
 */
public class BigOperatorAtom extends Atom implements HasTrueBase {

	// limits
	private final Atom under;
	private final Atom over;

	// atom representing a big operator
	private Atom base;

	/**
	 * Creates a new BigOperatorAtom from the given atoms. The default rules the
	 * positioning of the limits will be applied.
	 *
	 * @param base
	 *            atom representing the big operator
	 * @param under
	 *            atom representing the under limit
	 * @param over
	 *            atom representing the over limit
	 */
	public BigOperatorAtom(final Atom base, final Atom under, final Atom over) {
		this.base = base;
		this.under = under;
		this.over = over;
		this.type = TeXConstants.TYPE_BIG_OPERATOR;
	}

	public void setBase(final Atom base) {
		this.base = base;
	}

	@Override
	public Box createBox(TeXEnvironment env) {
		if (over == null && under == null) {
			return base.createBox(env);
		}

		TeXFont tf = env.getTeXFont();
		int style = env.getStyle();
		Box y;
		Atom trueBase = base.getBase();

		if ((base.type_limits == TeXConstants.SCRIPT_NOLIMITS)
				|| (base.type_limits == TeXConstants.SCRIPT_NORMAL
						&& style >= TeXConstants.STYLE_TEXT)) {
			return new ScriptsAtom(base, under, over).createBox(env);
		} else {
			// delta = italic
			double delta = trueBase.getItalic(env);

			// limits
			Box x = null;
			Box z = null;
			if (over != null) {
				x = over.createBox(env.supStyle());
			} else {
				trueBase.setAddItalicCorrection(false);
			}

			y = trueBase.createBox(env);

			if (under != null) {
				z = under.createBox(env.subStyle());
			}

			// make boxes equally wide
			double maxWidth = Math.max(
					Math.max(x == null ? 0. : x.getWidth(), y.getWidth()),
					z == null ? 0. : z.getWidth());
			x = changeWidth(x, maxWidth);
			y = changeWidth(y, maxWidth);
			z = changeWidth(z, maxWidth);

			// build vertical box
			VerticalBox vBox = new VerticalBox();

			double bigop5 = tf.getBigOpSpacing5(style);
			double kern = 0.;

			// over
			if (over != null) {
				vBox.add(new StrutBox(0., bigop5, 0., 0.));
				x.setShift(delta / 2.);
				vBox.add(x);
				kern = Math.max(tf.getBigOpSpacing1(style),
						tf.getBigOpSpacing3(style) - x.getDepth());
				vBox.add(new StrutBox(0., kern, 0., 0.));
			}

			// base
			vBox.add(y);

			// under
			if (under != null) {
				double k = Math.max(tf.getBigOpSpacing2(style),
						tf.getBigOpSpacing4(style) - z.getHeight());
				vBox.add(new StrutBox(0., k, 0., 0.));
				z.setShift(-delta / 2.);
				vBox.add(z);
				vBox.add(new StrutBox(0., bigop5, 0., 0.));
			}

			// set height and depth vertical box and return it
			double h = y.getHeight();
			final double total = vBox.getHeight() + vBox.getDepth();
			if (x != null) {
				h += bigop5 + kern + x.getHeight() + x.getDepth();
			}
			vBox.setHeight(h);
			vBox.setDepth(total - h);

			return vBox;
		}
	}

	/*
	 * Centers the given box in a new box that has the given width
	 */
	private static Box changeWidth(Box b, double maxWidth) {
		if (b != null && Math.abs(maxWidth - b.getWidth()) > TeXFormula.PREC) {
			return new HorizontalBox(b, maxWidth, TeXConstants.Align.CENTER);
		}
		return b;
	}

	public Atom getBottom() {
		return under;
	}

	public Atom getTop() {
		return over;
	}

	@Override
	public Atom getTrueBase() {
		return base;
	}
}
