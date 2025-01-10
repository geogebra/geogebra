/* FencedAtom.java
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

import java.util.List;

import com.himamis.retex.renderer.share.serialize.HasTrueBase;

/**
 * An atom representing a base atom surrounded with delimiters that change their
 * size according to the height of the base.
 */
public class FencedAtom extends Atom implements HasTrueBase {

	// base atom
	private final Atom base;

	// delimiters
	private SymbolAtom left = null;
	private SymbolAtom right = null;
	private final List<MiddleAtom> middle;

	/**
	 * Creates a new FencedAtom from the given base and delimiters
	 *
	 * @param base
	 *            the base to be surrounded with delimiters
	 * @param l
	 *            the left delimiter
	 * @param r
	 *            the right delimiter
	 */
	public FencedAtom(Atom base, SymbolAtom l, SymbolAtom r) {
		this(base, l, null, r);
	}

	public FencedAtom(Atom base, SymbolAtom l, List m, SymbolAtom r) {
		this.base = base;
		left = l == Symbols.NORMALDOT ? null : l;
		right = r == Symbols.NORMALDOT ? null : r;
		middle = m;
	}

	/**
	 * Centers the given box with resprect to the given axis, by setting an
	 * appropriate shift value.
	 *
	 * @param box
	 *            box to be vertically centered with respect to the axis
	 */
	private static Box center(Box box, double axis) {
		final double h = box.getHeight();
		final double total = h + box.getDepth();
		box.setShift(-(total / 2. - h) - axis);
		return box;
	}

	@Override
	public Box createBox(TeXEnvironment env) {
		final TeXFont tf = env.getTeXFont();
		Box content = base.createBox(env);
		final double axis = tf.getAxisHeight(env.getStyle());
		final double delta = Math.max(content.getHeight() - axis,
				content.getDepth() + axis);
		final double minHeight = Math.max(
				(delta / 500.) * env.lengthSettings().getFactor("delimiterfactor"),
				2. * delta - env.lengthSettings().getLength("delimitershortfall", env));

		// construct box
		final HorizontalBox hBox = new HorizontalBox();

		if (middle != null) {
			for (final MiddleAtom at : middle) {
				final Atom a = at.getBase();
				if (a instanceof SymbolAtom) {
					final Box b = DelimiterFactory
							.create(((SymbolAtom) a).getCf(), env, minHeight);
					at.setBox(center(b, axis));
				}
			}
			if (middle.size() != 0) {
				content = base.createBox(env);
			}
		}

		// left delimiter
		if (left != null) {
			final Box b = DelimiterFactory.create(left.getCf(), env, minHeight);
			hBox.add(center(b, axis));
		}

		// glue between left delimiter and content (if not whitespace)
		if (!(base instanceof SpaceAtom)) {
			final Box glue = Glue.get(TeXConstants.TYPE_OPENING,
					base.getLeftType(), env);
			if (glue != null) {
				hBox.add(glue);
			}
		}

		// add content
		hBox.add(content);

		// glue between right delimiter and content (if not whitespace)
		if (!(base instanceof SpaceAtom)) {
			final Box glue = Glue.get(base.getRightType(),
					TeXConstants.TYPE_CLOSING, env);
			if (glue != null) {
				hBox.add(glue);
			}
		}

		// right delimiter
		if (right != null) {
			final Box b = DelimiterFactory.create(right.getCf(), env,
					minHeight);
			hBox.add(center(b, axis));
		}

		return hBox.setAtom(this);
	}

	@Override
	public int getLeftType() {
		return TeXConstants.TYPE_OPENING;

	}

	@Override
	public int getRightType() {
		return TeXConstants.TYPE_CLOSING;
	}

	@Override
	public String toString() {
		return "FencedAtom: left: " + left + " base: " + base + " right: "
				+ right;
	}

	public Atom getLeft() {
		return left;
	}

	public Atom getRight() {
		return right;
	}

	@Override
	public Atom getTrueBase() {
		return base;
	}

}
