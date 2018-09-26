/* XFactory.java
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

import com.himamis.retex.renderer.share.platform.geom.Area;

/**
 * Responsible for creating a box containing a delimiter symbol that exists in
 * different sizes.
 */
public class XFactory {

	/*
	 * leftarrow & rightarrow have a width of 1.0 but without left/right bearing
	 * (0.057) and the round part (w=0.033) we've a width of 0.853. The body of
	 * the arrow is a line where the top is at 0.270 and the bottom at 0.230 so
	 * the height of the body is 0.04 (the default rule thickness)
	 */

	public static Box createArrow(boolean left, TeXEnvironment env,
			double width) {
		final TeXFont tf = env.getTeXFont();
		final int style = env.getStyle();
		final CharBox arr = (CharBox) (left ? Symbols.LEFTARROW.createBox(env)
				: Symbols.RIGHTARROW.createBox(env));
		final double h = arr.getHeight();
		final double d = arr.getDepth();
		final double arrw = arr.getWidth();
		final double bodyW = 0.886 /* 0.853 + 0.033 */ * arrw;
		if (width <= TeXFormula.PREC + bodyW) {
			arr.setDepth(d / 2.);
			return arr;
		}

		final double drt = tf.getDefaultRuleThickness(style);
		final double awidth = width - 0.910 * arrw;
		final HorizontalRule body = new HorizontalRule(drt, awidth, 0.);
		final Area abody = body.getArea();
		final Area aarr = arr.getArea();

		aarr.translate(-0.057 * arrw, 0d);

		if (left) {
			abody.translate(0.853 * arrw, -0.230 * arrw);
			aarr.add(abody);
		} else {
			abody.translate(0d, -0.230 * arrw);
			aarr.translate(awidth - 0.033 * arrw, 0d);
			aarr.add(abody);
		}

		final Box b = new ShapeBox(aarr);
		b.setDepth(d / 2.);
		b.setHeight(h);

		return b;
	}

	public static Box createHarpoon(boolean up, boolean left,
			TeXEnvironment env, double width) {
		// width = 1.0
		// bearings = 0.055 & 0.056
		// round part width = 0.037
		// true width = 0.852
		final TeXFont tf = env.getTeXFont();
		final int style = env.getStyle();
		final CharBox arr = (CharBox) (left
				? (up ? SymbolAtom.get("leftharpoonup").createBox(env)
						: SymbolAtom.get("leftharpoondown").createBox(env))
				: (up ? SymbolAtom.get("rightharpoonup").createBox(env)
						: SymbolAtom.get("rightharpoondown").createBox(env)));
		final double h = arr.getHeight();
		final double d = arr.getDepth();
		final double arrw = arr.getWidth();
		final double bodyW = 0.889 /* 1 - 0.055 - 0.056 */ * arrw;
		if (width <= TeXFormula.PREC + bodyW) {
			arr.setDepth(d / 2.);
			return arr;
		}

		final double drt = tf.getDefaultRuleThickness(style);
		final double awidth = width - 0.907 /* 0.852 + 0.055 */ * arrw;
		final HorizontalRule body = new HorizontalRule(drt, awidth, 0.);
		final Area abody = body.getArea();
		final Area aarr = arr.getArea();
		aarr.translate(-0.055 * arrw, 0d);

		if (left) {
			abody.translate(0.852 * arrw, -0.230 * arrw);
			aarr.add(abody);
		} else {
			abody.translate(0d, -0.230 * arrw);
			aarr.translate(awidth - 0.037 * arrw, 0d);
			aarr.add(abody);
		}

		final Box b = new ShapeBox(aarr);
		b.setDepth(d / 2.);
		b.setHeight(h);

		return b;
	}

	public static Box createLeftRightArrow(TeXEnvironment env, double width) {
		final CharBox left = (CharBox) Symbols.LEFTARROW.createBox(env);
		final double leftLB = 0.057;
		final double leftW = 0.853;
		final double bodyLW = 0.769;
		final double roundLW = 0.033;
		final CharBox right = (CharBox) Symbols.RIGHTARROW.createBox(env);
		final double rightLB = 0.057;
		final double rightW = 0.853;
		final double bodyRW = 0.769;
		final double roundRW = 0.033;
		return createExtension(env, left, leftLB, leftW, bodyLW, roundLW, right,
				rightLB, rightW, bodyRW, roundRW, width);
	}

	public static Box createExtension(TeXEnvironment env, CharBox left,
			double leftLB, double leftW, double bodyLW, double roundLW,
			CharBox right, double rightLB, double rightW, double bodyRW,
			double roundRW, double width) {
		// leftLB: left bearing for left char
		// leftW: the width of left without connector width (round part)
		// bodyLW: the width of left without connector width and without
		// non-line width
		// (for example, in an arrow, bodyLW is just the width of the arrow
		// without head)
		final double factor = TeXLength.getFactor(TeXLength.Unit.EM, env);
		final TeXFont tf = env.getTeXFont();
		final int style = env.getStyle();
		final double bodyW = (leftW + rightW) * factor;
		final Area aleft = left.getArea();
		final Area aright = right.getArea();
		aleft.translate(-leftLB * factor, 0d);
		if (width <= TeXFormula.PREC + bodyW) {
			final double diff = Math.min(bodyW - width,
					Math.min(bodyLW, bodyRW) * factor);
			aright.translate((leftW - rightLB - roundRW) * factor - diff, 0d);
			aleft.add(aright);
			return new ShapeBox(aleft);
		}

		final double drt = tf.getDefaultRuleThickness(style);
		final double awidth = width - bodyW;
		final HorizontalRule body = new HorizontalRule(drt, awidth, 0d);
		final Area abody = body.getArea();
		abody.translate(leftW * factor, -0.230 * factor);
		aleft.add(abody);
		aright.translate((leftW - rightLB - roundRW) * factor + awidth, 0d);
		aleft.add(aright);

		Box b = new ShapeBox(aleft);
		return b;
	}

	public static Box createXEqual(TeXEnvironment env, double width) {
		// bearing = 0.055
		// round = 0.034
		// true width = 0.777-2*(0.055+0.034)=0.599
		final TeXFont tf = env.getTeXFont();
		final int style = env.getStyle();
		final double factor = TeXLength.getFactor(TeXLength.Unit.EM, env);
		final CharBox equals = (CharBox) Symbols.EQUALS.createBox(env);
		if (width <= TeXFormula.PREC + 0.667 * factor) {
			return equals;
		}

		final double h = equals.getHeight();
		final double d = equals.getDepth();
		final double w = 0.599 * factor;
		final int N = (int) Math.floor((width - 0.068 * factor) / w);
		final double r = width - 0.068 * factor - N * w;
		final double drt = tf.getDefaultRuleThickness(style);
		final Area aeq = equals.getArea();
		aeq.translate(-0.055 * factor, 0d);
		final Area a = aeq.duplicate();
		for (int i = 1; i < N; ++i) {
			a.translate(w, 0d);
			aeq.add(a);
		}

		if (r != 0) {
			a.translate(r, 0d);
			aeq.add(a);
		}

		final Box b = new ShapeBox(aeq);
		b.setDepth(d / 2.);
		b.setHeight(h);

		return b;
	}
}
