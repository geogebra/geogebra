/* XArrowAtom.java
 * =========================================================================
 * This file is part of the JLaTeXMath Library - http://forge.scilab.org/jlatexmath
 *
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
 * An atom representing an extensible left or right arrow to handle xleftarrow
 * and xrightarrow commands in LaTeX.
 */
public class XArrowAtom extends XAtom {

	public static enum Kind {
		Left, Right, LR, LeftAndRight, RightAndLeft, LeftHarpoonUp, LeftHarpoonDown, RightHarpoonUp, RightHarpoonDown, LeftRightHarpoons, RightLeftHarpoons, RightSmallLeftHarpoons, SmallRightLeftHarpoons,
	};

	private Kind kind;

	@Override
	public Atom duplicate() {
		return setFields(new XArrowAtom(over, under, minW, kind));
	}

	public XArrowAtom(Atom over, Atom under, TeXLength minW, Kind kind) {
		super(over, under, minW);
		this.kind = kind;
	}

	public XArrowAtom(Atom over, Atom under, Kind kind) {
		this(over, under, TeXLength.getZero(), kind);
	}

	@Override
	public Box createExtension(TeXEnvironment env, double width) {
		switch (kind) {
		case Left:
			return XFactory.createArrow(true, env, width);
		case Right:
			return XFactory.createArrow(false, env, width);
		case LR:
			return XFactory.createLeftRightArrow(env, width);
		case LeftAndRight: {
			final Box right = XFactory.createArrow(false, env, width);
			final Box left = XFactory.createArrow(true, env, width);
			final VerticalBox vb = new VerticalBox(left);
			vb.add(right);
			return vb;
		}
		case RightAndLeft: {
			final Box right = XFactory.createArrow(false, env, width);
			final Box left = XFactory.createArrow(true, env, width);
			final VerticalBox vb = new VerticalBox(right);
			vb.add(left);
			final double totalH = vb.getHeight() + vb.getDepth();
			final double factor = env.getTeXFont().getQuad(env.getStyle());
			final double height = 0.250 * factor + totalH / 2.;
			final HorizontalBox hb = new HorizontalBox(vb);
			hb.setHeight(height);
			hb.setDepth(totalH - height);
			return hb;
		}
		case LeftHarpoonUp:
			return XFactory.createHarpoon(true, true, env, width);
		case LeftHarpoonDown:
			return XFactory.createHarpoon(false, true, env, width);
		case RightHarpoonUp:
			return XFactory.createHarpoon(true, false, env, width);
		case RightHarpoonDown:
			return XFactory.createHarpoon(false, false, env, width);
		case LeftRightHarpoons:
		// /___________
		// ___________
		// /
		{
			final Box right = XFactory.createHarpoon(false, false, env, width);
			final Box left = XFactory.createHarpoon(true, true, env, width);
			final VerticalBox vb = new VerticalBox(left);
			vb.add(new StrutBox(0.,
					new TeXLength(TeXLength.Unit.MU, -2.).getValue(env), 0.,
					0.));
			vb.add(right);
			final double totalH = vb.getHeight() + vb.getDepth();
			final double factor = env.getTeXFont().getQuad(env.getStyle());
			final double height = 0.250 * factor + totalH / 2.;
			final HorizontalBox hb = new HorizontalBox(vb);
			hb.setHeight(height);
			hb.setDepth(totalH - height);
			return hb;
		}
		case RightLeftHarpoons:
		// ___________\
		// ___________
		// \
		{
			final Box right = XFactory.createHarpoon(true, false, env, width);
			final Box left = XFactory.createHarpoon(false, true, env, width);
			final VerticalBox vb = new VerticalBox(right);
			vb.add(new StrutBox(0.,
					new TeXLength(TeXLength.Unit.MU, -2.).getValue(env), 0.,
					0.));
			vb.add(left);
			final double totalH = vb.getHeight() + vb.getDepth();
			final double factor = env.getTeXFont().getQuad(env.getStyle());
			final double height = 0.250 * factor + totalH / 2.;
			final HorizontalBox hb = new HorizontalBox(vb);
			hb.setHeight(height);
			hb.setDepth(totalH - height);
			return hb;
		}
		case RightSmallLeftHarpoons: {
			final Box right = XFactory.createHarpoon(true, false, env, width);
			final Box left = SymbolAtom.get("leftharpoondown").createBox(env);
			final VerticalBox vb = new VerticalBox(right);
			vb.add(new StrutBox(0.,
					new TeXLength(TeXLength.Unit.MU, -2.).getValue(env), 0.,
					0.));
			vb.add(new HorizontalBox(left, right.getWidth(),
					TeXConstants.Align.CENTER));
			final double totalH = vb.getHeight() + vb.getDepth();
			final double factor = env.getTeXFont().getQuad(env.getStyle());
			final double height = 0.250 * factor + totalH / 2.;
			final HorizontalBox hb = new HorizontalBox(vb);
			hb.setHeight(height);
			hb.setDepth(totalH - height);
			return hb;
		}
		case SmallRightLeftHarpoons: {
			final Box right = SymbolAtom.get("rightharpoonup").createBox(env);
			final Box left = XFactory.createHarpoon(false, true, env, width);
			final VerticalBox vb = new VerticalBox(new HorizontalBox(right,
					left.getWidth(), TeXConstants.Align.CENTER));
			vb.add(new StrutBox(0.,
					new TeXLength(TeXLength.Unit.MU, -2.).getValue(env), 0.,
					0.));
			vb.add(left);
			final double totalH = vb.getHeight() + vb.getDepth();
			final double factor = env.getTeXFont().getQuad(env.getStyle());
			final double height = 0.250 * factor + totalH / 2.;
			final HorizontalBox hb = new HorizontalBox(vb);
			hb.setHeight(height);
			hb.setDepth(totalH - height);
			return hb;
		}
		default:
			System.err.print(kind + " not implemented");
			return StrutBox.getEmpty();
		}
	}

}
