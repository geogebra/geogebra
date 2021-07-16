/* NthRoot.java
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

import com.himamis.retex.renderer.share.platform.FactoryProvider;
import com.himamis.retex.renderer.share.serialize.HasTrueBase;

/**
 * An atom representing an nth-root construction.
 */
public class NthRoot extends Atom implements HasTrueBase {

	private static final double FACTOR = 0.55;

	// base atom to be put under the root sign
	private final Atom base;

	// root atom to be put in the upper left corner above the root sign
	private final Atom root;

	public NthRoot(Atom base, Atom root) {
		this.base = base == null ? EmptyAtom.get() : base;
		this.root = root;
	}

	@Override
	public Box createBox(TeXEnvironment env) {
		// first create a simple square root construction
		TeXFont tf = env.getTeXFont();
		int style = env.getStyle();
		// calculate minimum clearance clr
		double clr;
		final double drt = tf.getDefaultRuleThickness(style);
		if (style < TeXConstants.STYLE_TEXT) {
			clr = tf.getXHeight(style,
					tf.getChar(Symbols.SQRT.getCf(), style).getFontInfo());
		} else {
			clr = drt;
		}
		clr = drt + Math.abs(clr) / 4.;

		// cramped style for the formula under the root sign
		Box bs = base.createBox(env.crampStyle());
		HorizontalBox b = new HorizontalBox(bs);
		b.add(new SpaceAtom(Unit.MU, 1., 0., 0.)
				.createBox(env.crampStyle()));
		// create root sign
		double totalH = b.getHeight() + b.getDepth();
		Box rootSign = DelimiterFactory.create(Symbols.SQRT.getCf(), env,
				totalH + clr + drt);

		if (rootSign instanceof CharBox) {
			rootSign = FactoryProvider.getInstance().getBoxDecorator().decorate(rootSign);
		}

		// add half the excess to clr
		double delta = rootSign.getDepth() - (totalH + clr);
		clr += delta / 2.;

		// create total box
		rootSign.setShift(-(b.getHeight() + clr));
		OverBar ob = new OverBar(b, clr, rootSign.getHeight());
		ob.setShift(-(b.getHeight() + clr + drt));
		HorizontalBox squareRoot = new HorizontalBox(rootSign);
		squareRoot.add(ob);

		if (root == null) {
			// simple square root
			return squareRoot;
		} else { // nthRoot, not a simple square root
			// create box from root
			Box r = root.createBox(env.rootStyle());

			// shift root up
			double bottomShift = FACTOR
					* (squareRoot.getHeight() + squareRoot.getDepth());
			r.setShift(squareRoot.getDepth() - r.getDepth() - bottomShift);

			// negative kern
			Box negativeKern = new SpaceAtom(Unit.MU, -10., 0., 0.)
					.createBox(env);

			// arrange both boxes together with the negative kern
			HorizontalBox res = new HorizontalBox();
			double pos = r.getWidth() + negativeKern.getWidth();
			if (pos < 0) {
				res.add(new StrutBox(-pos, 0., 0., 0.));
			}

			res.add(r);
			res.add(negativeKern);
			res.add(squareRoot);
			return res.setAtom(this);
		}
	}

	public Atom getRoot() {
		return root;
	}

	@Override
	public Atom getTrueBase() {
		return base;
	}

}
