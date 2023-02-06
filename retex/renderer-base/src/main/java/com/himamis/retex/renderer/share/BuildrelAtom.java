/* BuildrelAtom.java
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

/**
 * An atom representing another atom with an atom above it (if not null)
 * seperated by a kern and in a smaller size depending on "overScriptSize"
 * and/or an atom under it (if not null) seperated by a kern and in a smaller
 * size depending on "underScriptSize"
 */
public class BuildrelAtom extends Atom implements HasUnderOver {

	// base, underscript and overscript
	private final Atom base;
	private final Atom over;
	private final boolean scriptSize;

	public BuildrelAtom(Atom base, Atom over, boolean scriptSize) {
		this.base = base;
		this.over = over;
		this.scriptSize = scriptSize;
		this.type = TeXConstants.TYPE_RELATION;
	}

	public BuildrelAtom(Atom base, Atom over) {
		this(base, over, true);
	}

	@Override
	public Box createBox(TeXEnvironment env) {
		// create boxes in right style and calculate maximum width
		Box b = base.createBox(env);
		Box o = over.createBox(scriptSize ? env.supStyle() : env);
		final double max = Math.max(o.getWidth(), b.getWidth());
		final Atom trueBase = base.getBase();
		final double delta = trueBase.getItalic(env);
		final TeXFont tf = env.getTeXFont();
		final int style = env.getStyle();
		final double bigop5 = tf.getBigOpSpacing5(style);
		final double kern = Math.max(tf.getBigOpSpacing1(style),
				tf.getBigOpSpacing3(style) - o.getDepth());

		// create vertical box
		VerticalBox vBox = new VerticalBox();

		// last font used by the base (for Mspace atoms following)
		env.setLastFont(b.getLastFont());

		o = changeWidth(o, max);
		b = changeWidth(b, max);

		vBox.add(new StrutBox(0., bigop5, 0., 0.));
		o.setShift(delta / 2.);
		vBox.add(o);
		vBox.add(new StrutBox(0., kern, 0., 0.));
		vBox.add(b);

		final double h = b.getHeight() + bigop5 + kern + o.getHeight()
				+ o.getDepth();
		final double total = vBox.getHeight() + vBox.getDepth();
		vBox.setHeight(h);
		vBox.setDepth(total - h);

		return vBox;
	}

	private static Box changeWidth(Box b, double maxWidth) {
		if (b != null) {
			if (Math.abs(maxWidth - b.getWidth()) > TeXFormula.PREC) {
				return new HorizontalBox(b, maxWidth,
						TeXConstants.Align.CENTER);
			} else {
				b.setHeight(Math.max(b.getHeight(), 0.));
				b.setDepth(Math.max(b.getDepth(), 0.));
			}
		}
		return b;
	}

	@Override
	public Atom getTrueBase() {
		return base;
	}

	@Override
	public Atom getUnderOver() {
		return over;
	}

	@Override
	public boolean isUnder() {
		return false;
	}
}
