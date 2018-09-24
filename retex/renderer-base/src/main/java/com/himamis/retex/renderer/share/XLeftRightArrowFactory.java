/* XLeftRightArrowFactory.java
 * =========================================================================
 * This file is part of the JLaTeXMath Library - http://jlatexmath.sourceforge.net
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

import com.himamis.retex.renderer.share.platform.geom.Area;

/**
 * Responsible for creating a box containing a delimiter symbol that exists in
 * different sizes.
 */
public class XLeftRightArrowFactory {

	/*
	 * leftarrow & rightarrow have a width of 1.0 but without left/right bearing
	 * (0.057) and the round part (w=0.033) we've a width of 0.853. The body of
	 * the arrow is a line where the top is at 0.270 and the bottom at 0.230 so
	 * the height of the body is 0.04 (the default rule thickness)
	 */

	public static Box create(boolean left, TeXEnvironment env, double width) {
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

		if (left) {
			abody.translate(0.910 * arrw, -0.230 * arrw);
			aarr.add(abody);
		} else {
			aarr.translate(awidth - 0.090 * arrw, 0.);
			abody.translate(0., -0.230 * arrw);
			aarr.add(abody);
		}

		final Box b = new ShapeBox(aarr);
		b.setDepth(d / 2.);
		b.setHeight(h);

		return b;
	}

	public static Box create(TeXEnvironment env, double width) {
		final TeXFont tf = env.getTeXFont();
		final int style = env.getStyle();
		final CharBox left = (CharBox) Symbols.LEFTARROW.createBox(env);
		final CharBox right = (CharBox) Symbols.RIGHTARROW.createBox(env);
		final double arrw = left.getWidth();
		final double bodyW = 2. * 0.853 * arrw;
		final Area aleft = left.getArea();
		final Area aright = right.getArea();
		if (width <= TeXFormula.PREC + bodyW) {
			final double diff = Math.min(bodyW - width,
					0.769 /* true body width */ * arrw);
			aright.translate(0.820 * arrw - diff, 0.);
			aleft.add(aright);
			return new ShapeBox(aleft);
		}

		final double drt = tf.getDefaultRuleThickness(style);
		final double awidth = width - bodyW + 0.180 * arrw;
		final HorizontalRule body = new HorizontalRule(drt, awidth, 0.);
		final Area abody = body.getArea();
		abody.translate(0.910 * arrw, -0.230 * arrw);
		aleft.add(abody);
		aright.translate(0.820 * arrw + awidth, 0.);
		aleft.add(aright);

		return new ShapeBox(aleft);
	}

	public static Box createXMapsto(TeXEnvironment env, double width) {
		final TeXFont tf = env.getTeXFont();
		final int style = env.getStyle();
		final CharBox left = (CharBox) Symbols.MAPSTOCHAR.createBox(env);
		final CharBox right = (CharBox) Symbols.RIGHTARROW.createBox(env);
		final double arrw = right.getWidth();
		final double bodyW = (0. + 0.853) * arrw;
		final Area aleft = left.getArea();
		final Area aright = right.getArea();
		if (width <= TeXFormula.PREC + bodyW) {
			final double diff = Math.min(bodyW - width,
					0. /* mapsto width */ * arrw);
			aright.translate(0. * arrw - diff, 0.);
			aleft.add(aright);
			return new ShapeBox(aleft);
		}

		final double drt = tf.getDefaultRuleThickness(style);
		final double awidth = width - bodyW + 0.180 * arrw;
		final HorizontalRule body = new HorizontalRule(drt, awidth, 0.);
		final Area abody = body.getArea();
		abody.translate(0.096 * arrw, -0.230 * arrw);
		aleft.add(abody);
		aright.translate(0. * arrw + awidth, 0.);
		aleft.add(aright);

		return new ShapeBox(aleft);
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

	public static Box createMapsto(TeXEnvironment env, double width) {
		final CharBox left = (CharBox) Symbols.MAPSTOCHAR.createBox(env);
		final double leftLB = 0.056;
		final double leftW = 0.040;
		final double bodyLW = 0.;
		final double roundLW = 0.028;
		final CharBox right = (CharBox) Symbols.RIGHTARROW.createBox(env);
		final double rightLB = 0.057;
		final double rightW = 0.853;
		final double bodyRW = 0.769;
		final double roundRW = 0.033;
		return createExtension(env, left, leftLB, leftW, bodyLW, roundLW, right,
				rightLB, rightW, bodyRW, roundRW, width);
	}

	private static Box createExtension(TeXEnvironment env, CharBox left,
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
		aleft.translate(-leftLB * factor, 0.);
		if (width <= TeXFormula.PREC + bodyW) {
			final double diff = Math.min(bodyW - width,
					Math.min(bodyLW, bodyRW) * factor);
			aright.translate((leftW - rightLB - roundRW) * factor - diff, 0.);
			aleft.add(aright);
			return new ShapeBox(aleft);
		}

		final double drt = tf.getDefaultRuleThickness(style);
		final double awidth = width - bodyW;
		final HorizontalRule body = new HorizontalRule(drt, awidth, 0.);
		final Area abody = body.getArea();
		abody.translate(leftW * factor, -0.230 * factor);
		aleft.add(abody);
		aright.translate((leftW - rightLB - roundRW) * factor + awidth, 0.);
		aleft.add(aright);

		return new ShapeBox(aleft);
	}
}
