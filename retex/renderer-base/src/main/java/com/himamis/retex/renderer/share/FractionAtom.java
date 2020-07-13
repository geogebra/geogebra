/* FractionAtom.java
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

/**
 * An atom representing a fraction.
 */
public class FractionAtom extends Atom {

	// alignment settings for the numerator and denominator
	protected TeXConstants.Align numAlign = TeXConstants.Align.CENTER;
	protected TeXConstants.Align denomAlign = TeXConstants.Align.CENTER;

	// the atoms representing the numerator and denominator
	protected Atom numerator;
	protected Atom denominator;

	// thickness of the fraction line
	// unit used for the thickness of the fraction line
	protected TeXLength thickness;

	/**
	 * The thickness of the fraction line is determined by the given value "t"
	 * in the given unit.
	 *
	 * @param num
	 *            the numerator
	 * @param den
	 *            the denominator
	 * @param t
	 *            the thickness of the fraction line (in the given unit)
	 * @param numAlign
	 *            alignment of the numerator
	 * @param denomAlign
	 *            alignment of the denominator
	 */
	public FractionAtom(Atom num, Atom den, TeXLength t,
			TeXConstants.Align numAlign, TeXConstants.Align denomAlign) {
		numerator = num;
		denominator = den;
		thickness = t;
		this.numAlign = numAlign;
		this.denomAlign = denomAlign;
		type = TeXConstants.TYPE_INNER;
	}

	/**
	 * Uses the default thickness for the fraction line
	 *
	 * @param num
	 *            the numerator
	 * @param den
	 *            the denominator
	 */
	public FractionAtom(Atom num, Atom den) {
		this(num, den, null, TeXConstants.Align.CENTER,
				TeXConstants.Align.CENTER);
	}

	public boolean isRuleHidden() {
		// null represents default non-zero thickness
		return thickness != null && thickness.getL() == 0;
	}

	/**
	 * Uses the default thickness for the fraction line
	 *
	 * @param num
	 *            the numerator
	 * @param den
	 *            the denominator
	 * @param rule
	 *            whether the fraction line should be drawn
	 */
	public FractionAtom(Atom num, Atom den, boolean rule) {
		this(num, den, rule ? null : TeXLength.getZero(),
				TeXConstants.Align.CENTER, TeXConstants.Align.CENTER);
	}

	public FractionAtom(Atom num, Atom den, TeXLength l) {
		this(num, den, l, TeXConstants.Align.CENTER, TeXConstants.Align.CENTER);
	}

	/**
	 * Uses the default thickness for the fraction line.
	 *
	 * @param num
	 *            the numerator
	 * @param den
	 *            the denominator
	 * @param rule
	 *            whether the fraction line should be drawn
	 * @param numAlign
	 *            alignment of the numerator
	 * @param denomAlign
	 *            alignment of the denominator
	 */
	public FractionAtom(Atom num, Atom den, boolean rule,
			TeXConstants.Align numAlign, TeXConstants.Align denomAlign) {
		this(num, den, rule ? null : TeXLength.getZero(), numAlign, denomAlign);
	}

	@Override
	public Box createBox(TeXEnvironment env) {
		TeXFont tf = env.getTeXFont();
		int style = env.getStyle();
		// set thickness to default if default value should be used
		final double drt = tf.getDefaultRuleThickness(style);
		double thn;
		if (thickness != null) {
			// convert the thickness to pixels
			thn = thickness.getValue(env);
		} else {
			thn = drt;
		}

		// create equal width boxes (in appropriate styles)
		Box num = numerator == null ? StrutBox.getEmpty()
				: numerator.createBox(env.numStyle());
		Box denom = denominator == null ? StrutBox.getEmpty()
				: denominator.createBox(env.denomStyle());

		if (num.getWidth() < denom.getWidth()) {
			num = new HorizontalBox(num, denom.getWidth(), numAlign);
		} else {
			denom = new HorizontalBox(denom, num.getWidth(), denomAlign);
		}

		// calculate default shift amounts
		double shiftUp;
		double shiftDown;
		if (style < TeXConstants.STYLE_TEXT) {
			shiftUp = tf.getNum1(style);
			shiftDown = tf.getDenom1(style);
		} else {
			shiftDown = tf.getDenom2(style);
			if (thn > 0) {
				shiftUp = tf.getNum2(style);
			} else {
				shiftUp = tf.getNum3(style);
			}
		}

		// upper part of vertical box = numerator
		VerticalBox vBox = new VerticalBox();
		vBox.add(num);

		// calculate clearance clr, adjust shift amounts and create vertical box
		double clr;
		double delta;
		double axis = tf.getAxisHeight(style);

		if (thn > 0) { // with fraction rule
			// clearance clr
			clr = style < TeXConstants.STYLE_TEXT ? 3. * thn : thn;

			// adjust shift amounts
			delta = thn / 2.;
			double kern1 = shiftUp - num.getDepth() - (axis + delta);
			double kern2 = axis - delta - (denom.getHeight() - shiftDown);
			double delta1 = clr - kern1;
			double delta2 = clr - kern2;
			if (delta1 > 0) {
				shiftUp += delta1;
				kern1 += delta1;
			}
			if (delta2 > 0) {
				shiftDown += delta2;
				kern2 += delta2;
			}

			// fill vertical box
			vBox.add(new StrutBox(0., kern1, 0., 0.));
			vBox.add(new HorizontalRule(thn, num.getWidth(), 0.));
			vBox.add(new StrutBox(0., kern2, 0., 0.));
		} else { // without fraction rule
			// clearance clr
			clr = (style < TeXConstants.STYLE_TEXT ? 7. : 3.) * drt;

			// adjust shift amounts
			double kern = shiftUp - num.getDepth()
					- (denom.getHeight() - shiftDown);
			delta = (clr - kern) / 2.;
			if (delta > 0) {
				shiftUp += delta;
				shiftDown += delta;
				kern += 2. * delta;
			}

			// fill vertical box
			vBox.add(new StrutBox(0., kern, 0., 0.));
		}

		// finish vertical box
		vBox.add(denom);
		vBox.setHeight(shiftUp + num.getHeight());
		vBox.setDepth(shiftDown + denom.getDepth());

		final double f = env.lengthSettings().getLength("nulldelimiterspace", env);

		return new HorizontalBox(vBox, vBox.getWidth() + 2 * f,
				TeXConstants.Align.CENTER);
	}

	public Atom getNumerator() {
		return numerator;
	}

	public Atom getDenominator() {
		return denominator;
	}

}
