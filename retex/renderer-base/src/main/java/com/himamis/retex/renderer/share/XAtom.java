/* XAtom.java
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

import com.himamis.retex.renderer.share.serialize.HasTrueBase;

/**
 * An atom representing an extensible left or right arrow to handle xleftarrow
 * and xrightarrow commands in LaTeX.
 */
public abstract class XAtom extends Atom implements HasTrueBase {

	protected final Atom over;
	protected final Atom under;
	protected final TeXLength minW;

	public XAtom(Atom over, Atom under, TeXLength minW) {
		this.type = TeXConstants.TYPE_RELATION;
		this.over = over;
		this.under = under;
		this.minW = minW;
	}

	public XAtom(Atom over, Atom under) {
		this(over, under, TeXLength.getZero());
	}

	@Override
	public Box createBox(TeXEnvironment env) {
		final Box O = over != null ? over.createBox(env.supStyle())
				: StrutBox.getEmpty();
		final Box U = under != null ? under.createBox(env.subStyle())
				: StrutBox.getEmpty();
		final Box oside = new SpaceAtom(Unit.MU, 5., 0., 0.)
				.createBox(env.supStyle());
		final Box uside = new SpaceAtom(Unit.MU, 9., 0., 0.)
				.createBox(env.subStyle());
		final Box osep = new SpaceAtom(Unit.MU, 0., 2., 0.)
				.createBox(env);
		final Box usep = new SpaceAtom(Unit.MU, 0., 3.5, 0.)
				.createBox(env);
		double width = Math.max(O.getWidth() + 2. * oside.getWidth(),
				U.getWidth() + 2. * uside.getWidth());
		width = Math.max(width, minW.getValue(env));

		final Box extended = createExtension(env, width);
		width = extended.getWidth();

		final Box ohb = new HorizontalBox(O, width, TeXConstants.Align.CENTER);
		final Box uhb = new HorizontalBox(U, width, TeXConstants.Align.CENTER);

		final VerticalBox vb = new VerticalBox();
		vb.add(ohb);
		vb.add(osep);
		vb.add(extended);
		vb.add(usep);
		vb.add(uhb);

		final double h = vb.getHeight() + vb.getDepth();
		final double d = osep.getHeight() + uhb.getHeight() + uhb.getDepth();
		vb.setDepth(d);
		vb.setHeight(h - d);

		return new HorizontalBox(vb, vb.getWidth() + 2. * osep.getHeight(),
				TeXConstants.Align.CENTER);
	}

	public abstract Box createExtension(TeXEnvironment env, double width);

	@Override
	public Atom getTrueBase() {
		return over != null ? over : under;
	}
}
