/* ScriptsAtom.java
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

import java.util.ArrayList;

import com.himamis.retex.renderer.share.mhchem.CEEmptyAtom;
import com.himamis.retex.renderer.share.serialize.HasTrueBase;

/**
 * An atom representing scripts to be attached to another atom.
 */
public class ScriptsAtom extends Atom implements HasTrueBase {

	// base atom
	private Atom base;

	// subscript and superscript to be attached to the base (if not null)
	private Atom subscript;
	private Atom superscript;
	private TeXConstants.Align align;

	public ScriptsAtom(Atom base, Atom sub, Atom sup,
			TeXConstants.Align align) {
		this.base = base;
		subscript = sub;
		superscript = sup;
		this.align = align;
	}

	public ScriptsAtom(Atom base, Atom sub, Atom sup, boolean left) {
		this(base, sub, sup,
				left ? TeXConstants.Align.LEFT : TeXConstants.Align.RIGHT);
	}

	public ScriptsAtom(Atom base, Atom sub, Atom sup) {
		this(base, sub, sup, !(base instanceof CEEmptyAtom));
	}

	@Override
	public Atom getTrueBase() {
		return base;
	}

	public void setBase(Atom base) {
		this.base = base;
	}

	public void setSup(Atom sup) {
		superscript = sup;
	}

	public void setSub(Atom sub) {
		subscript = sub;
	}

	public void addToSup(Atom a) {
		if (superscript == null) {
			superscript = a;
		} else if (superscript instanceof RowAtom) {
			((RowAtom) superscript).add(a);
		} else {
			superscript = new RowAtom(superscript, a);
		}
	}

	public void addToSub(Atom a) {
		if (subscript == null) {
			subscript = a;
		} else if (subscript instanceof RowAtom) {
			((RowAtom) subscript).add(a);
		} else {
			subscript = new RowAtom(subscript, a);
		}
	}

	public Atom getSup() {
		return superscript;
	}

	public Atom getSub() {
		return subscript;
	}

	@Override
	public Box createBox(TeXEnvironment env) {
		if (subscript == null && superscript == null) {
			return base.createBox(env);
		} else {
			final Atom trueBase = base.getBase();
			if (trueBase instanceof RowAtom
					&& ((RowAtom) trueBase).lookAtLast()) {
				return createBoxForRowAtom(env);
			}

			int style = env.getStyle();

			if (base.type_limits == TeXConstants.SCRIPT_LIMITS
					|| (base.type_limits == TeXConstants.SCRIPT_NORMAL
							&& style == TeXConstants.STYLE_DISPLAY)) {
				return new BigOperatorAtom(base, subscript, superscript)
						.createBox(env).setAtom(this);
			}

			final boolean it = base.setAddItalicCorrection(subscript == null);
			Box b = base.createBox(env);
			base.setAddItalicCorrection(it);

			Box scriptspace = new StrutBox(
					env.lengthSettings().getLength("scriptspace", env), 0., 0., 0.);
			TeXFont tf = env.getTeXFont();

			HorizontalBox hor = new HorizontalBox(b);

			FontInfo lastFontId = b.getLastFont();
			// if no last font found (whitespace box), use default "mu font"
			if (lastFontId == null) {
				lastFontId = TeXFont.MUFONT;
			}

			TeXEnvironment subStyle = env.subStyle();
			TeXEnvironment supStyle = env.supStyle();

			// set delta and preliminary shift-up and shift-down values
			double delta = 0.;
			double shiftUp;
			double shiftDown;

			if (trueBase instanceof CharAtom) {
				final CharAtom ca = (CharAtom) trueBase;
				shiftUp = shiftDown = 0.;
				CharFont cf = ca.getCharFont(tf);
				if ((!ca.isMarkedAsTextSymbol() || !tf.hasSpace(cf.fontInfo))
						&& subscript != null) {
					delta = tf.getChar(cf, style).getItalic();
				}
			} else {
				if (trueBase instanceof SymbolAtom && trueBase
						.getType() == TeXConstants.TYPE_BIG_OPERATOR) {
					if (trueBase.isMathMode()
							&& trueBase.mustAddItalicCorrection()) {
						delta = trueBase.getItalic(env);
					}
				}
				shiftUp = b.getHeight() - tf.getSupDrop(supStyle.getStyle());
				shiftDown = b.getDepth() + tf.getSubDrop(subStyle.getStyle());
			}

			if (superscript == null) { // only subscript
				Box x = subscript.createBox(subStyle);
				// calculate and set shift amount
				x.setShift(
						Math.max(Math.max(shiftDown, tf.getSub1(style)),
								x.getHeight() - 4. * Math
										.abs(tf.getXHeight(style, lastFontId))
										/ 5.));
				hor.add(x);

				return hor.setAtom(this);
			} else {
				Box x = superscript.createBox(supStyle);
				double msiz = x.getWidth();
				if (subscript != null && align == TeXConstants.Align.RIGHT) {
					msiz = Math.max(msiz,
							subscript.createBox(subStyle).getWidth());
				}

				HorizontalBox sup = new HorizontalBox(x, msiz, align);
				// add scriptspace (constant value!)
				sup.add(scriptspace);
				// adjust shift-up
				double p;
				if (style == TeXConstants.STYLE_DISPLAY) {
					p = tf.getSup1(style);
				} else if (env.crampStyle().getStyle() == style) {
					p = tf.getSup3(style);
				} else {
					p = tf.getSup2(style);
				}
				shiftUp = Math.max(Math.max(shiftUp, p), x.getDepth()
						+ Math.abs(tf.getXHeight(style, lastFontId)) / 4.);

				if (subscript == null) { // only superscript
					sup.setShift(-shiftUp);
					hor.add(sup);
				} else { // both superscript and subscript
					Box y = subscript.createBox(subStyle);
					HorizontalBox sub = new HorizontalBox(y, msiz, align);
					// add scriptspace (constant value!)
					sub.add(scriptspace);
					// adjust shift-down
					shiftDown = Math.max(shiftDown, tf.getSub2(style));
					// position both sub- and superscript
					double drt = tf.getDefaultRuleThickness(style);
					// space between sub- en
					double interSpace = shiftUp - x.getDepth() + shiftDown
							- y.getHeight();
					// superscript
					if (interSpace < 4. * drt) { // too small
						shiftUp += 4. * drt - interSpace;
						// set bottom superscript at least 4/5 of X-height
						// above
						// baseline
						double psi = 4.
								* Math.abs(tf.getXHeight(style, lastFontId))
								/ 5. - (shiftUp - x.getDepth());

						if (psi > 0.) {
							shiftUp += psi;
							shiftDown -= psi;
						}
					}
					// create total box

					VerticalBox vBox = new VerticalBox();
					sup.setShift(delta);
					vBox.add(sup);
					// recalculate interspace
					interSpace = shiftUp - x.getDepth() + shiftDown
							- y.getHeight();
					vBox.add(new StrutBox(0., interSpace, 0., 0.));
					vBox.add(sub);
					vBox.setHeight(shiftUp + x.getHeight());
					vBox.setDepth(shiftDown + y.getDepth());
					hor.add(vBox);
				}

				return hor.setAtom(this);
			}
		}
	}

	private Box createBoxForRowAtom(TeXEnvironment env) {
		final Atom trueBase = base.getBase();
		final RowAtom ra = (RowAtom) trueBase;
		final Atom last = ra.last();
		final Box b = new ScriptsAtom(last, subscript, superscript, align)
				.createBox(env);
		final HorizontalBox hb = new HorizontalBox(base.createBox(env));
		if (subscript != null) {
			final double italic = last.getItalic(env);
			hb.add(new StrutBox(-italic, 0., 0., 0.));
		}
		final ArrayList<Box> c = ((HorizontalBox) b).getChildren();
		for (int i = 1; i < c.size(); ++i) {
			hb.add(c.get(i));
		}
		return hb;
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

}
