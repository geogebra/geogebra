/* Glue.java
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
 * Represents glue by its 3 components. Contains the "glue rules".
 */
public final class Glue {

	private static final Glue THIN = new Glue(3., 0., 0.);
	private static final Glue MED = new Glue(4., 4., 2.);
	private static final Glue THICK = new Glue(5., 0., 5.);

	private static final int ORD = 0;
	private static final int OP = 1;
	private static final int BIN = 2;
	private static final int REL = 3;
	private static final int OPEN = 4;
	private static final int CLOSE = 5;
	private static final int PUNCT = 6;
	private static final int INNER = 7;

	private static final int DISPLAY = 0;
	private static final int TEXT = 1;
	private static final int SCRIPT = 2;
	private static final int SCRIPT_SCRIPT = 3;

	// the glue table representing the "glue rules" (as in TeXBook p. 170)
	private static final Glue[] glues = new Glue[256];

	static {
		glues[index(ORD, OP, DISPLAY)] = THIN;
		glues[index(ORD, OP, TEXT)] = THIN;
		glues[index(ORD, OP, SCRIPT)] = THIN;
		glues[index(ORD, OP, SCRIPT_SCRIPT)] = THIN;

		glues[index(ORD, BIN, DISPLAY)] = MED;
		glues[index(ORD, BIN, TEXT)] = MED;

		glues[index(ORD, REL, DISPLAY)] = THICK;
		glues[index(ORD, REL, TEXT)] = THICK;

		glues[index(ORD, INNER, DISPLAY)] = THIN;
		glues[index(ORD, INNER, TEXT)] = THIN;

		glues[index(OP, ORD, DISPLAY)] = THIN;
		glues[index(OP, ORD, TEXT)] = THIN;
		glues[index(OP, ORD, SCRIPT)] = THIN;
		glues[index(OP, ORD, SCRIPT_SCRIPT)] = THIN;

		glues[index(OP, OP, DISPLAY)] = THIN;
		glues[index(OP, OP, TEXT)] = THIN;
		glues[index(OP, OP, SCRIPT)] = THIN;
		glues[index(OP, OP, SCRIPT_SCRIPT)] = THIN;

		glues[index(OP, REL, DISPLAY)] = THICK;
		glues[index(OP, REL, TEXT)] = THICK;

		glues[index(OP, INNER, DISPLAY)] = THIN;
		glues[index(OP, INNER, TEXT)] = THIN;

		glues[index(BIN, ORD, DISPLAY)] = MED;
		glues[index(BIN, ORD, TEXT)] = MED;

		glues[index(BIN, OP, DISPLAY)] = MED;
		glues[index(BIN, OP, TEXT)] = MED;

		glues[index(BIN, OPEN, DISPLAY)] = MED;
		glues[index(BIN, OPEN, TEXT)] = MED;

		glues[index(BIN, INNER, DISPLAY)] = MED;
		glues[index(BIN, INNER, TEXT)] = MED;

		glues[index(REL, ORD, DISPLAY)] = THICK;
		glues[index(REL, ORD, TEXT)] = THICK;

		glues[index(REL, OP, DISPLAY)] = THICK;
		glues[index(REL, OP, TEXT)] = THICK;

		glues[index(REL, OPEN, DISPLAY)] = THICK;
		glues[index(REL, OPEN, TEXT)] = THICK;

		glues[index(REL, INNER, DISPLAY)] = THICK;
		glues[index(REL, INNER, TEXT)] = THICK;

		glues[index(CLOSE, OP, DISPLAY)] = THIN;
		glues[index(CLOSE, OP, TEXT)] = THIN;
		glues[index(CLOSE, OP, SCRIPT)] = THIN;
		glues[index(CLOSE, OP, SCRIPT_SCRIPT)] = THIN;

		glues[index(CLOSE, BIN, DISPLAY)] = THIN;
		glues[index(CLOSE, BIN, TEXT)] = THIN;

		glues[index(CLOSE, REL, DISPLAY)] = THICK;
		glues[index(CLOSE, REL, TEXT)] = THICK;

		glues[index(CLOSE, INNER, DISPLAY)] = THIN;
		glues[index(CLOSE, INNER, TEXT)] = THIN;

		glues[index(PUNCT, ORD, DISPLAY)] = THIN;
		glues[index(PUNCT, ORD, TEXT)] = THIN;

		glues[index(PUNCT, OP, DISPLAY)] = MED;
		glues[index(PUNCT, OP, TEXT)] = MED;

		glues[index(PUNCT, REL, DISPLAY)] = THICK;
		glues[index(PUNCT, REL, TEXT)] = THICK;

		glues[index(PUNCT, OPEN, DISPLAY)] = THIN;
		glues[index(PUNCT, OPEN, TEXT)] = THIN;

		glues[index(PUNCT, CLOSE, DISPLAY)] = THIN;
		glues[index(PUNCT, CLOSE, TEXT)] = THIN;

		glues[index(PUNCT, PUNCT, DISPLAY)] = MED;
		glues[index(PUNCT, PUNCT, TEXT)] = MED;

		glues[index(PUNCT, INNER, DISPLAY)] = THICK;
		glues[index(PUNCT, INNER, TEXT)] = THICK;

		glues[index(INNER, ORD, DISPLAY)] = THIN;
		glues[index(INNER, ORD, TEXT)] = THIN;

		glues[index(INNER, OP, DISPLAY)] = THIN;
		glues[index(INNER, OP, TEXT)] = THIN;
		glues[index(INNER, OP, SCRIPT)] = THIN;
		glues[index(INNER, OP, SCRIPT_SCRIPT)] = THIN;

		glues[index(INNER, BIN, DISPLAY)] = MED;
		glues[index(INNER, BIN, TEXT)] = MED;

		glues[index(INNER, REL, DISPLAY)] = THICK;
		glues[index(INNER, REL, TEXT)] = THICK;

		glues[index(INNER, OPEN, DISPLAY)] = THIN;
		glues[index(INNER, OPEN, TEXT)] = THIN;

		glues[index(INNER, PUNCT, DISPLAY)] = THIN;
		glues[index(INNER, PUNCT, TEXT)] = THIN;

		glues[index(INNER, INNER, DISPLAY)] = THIN;
		glues[index(INNER, INNER, TEXT)] = THIN;
	}

	// the glue components
	private final double space;
	private final double stretch;
	private final double shrink;

	private Glue(final double space, final double stretch,
			final double shrink) {
		this.space = space;
		this.stretch = stretch;
		this.shrink = shrink;
	}

	/**
	 * Creates a box representing the glue type according to the "glue rules"
	 * based on the atom types between which the glue must be inserted.
	 *
	 * @param lType
	 *            left atom type
	 * @param rType
	 *            right atom type
	 * @param env
	 *            the TeXEnvironment
	 * @return a box containing representing the glue
	 */
	public static Box get(final int lType, final int rType,
			final TeXEnvironment env) {
		// types > INNER are considered of type ORD for glue calculations
		final int l = (lType > TeXConstants.TYPE_INNER
				? TeXConstants.TYPE_ORDINARY : lType);
		final int r = (rType > TeXConstants.TYPE_INNER
				? TeXConstants.TYPE_ORDINARY : rType);

		// search right glue-type in "glue-table"
		final Glue g = glues[index(l, r, env.getStyle() >> 1)];
		return g == null ? null : g.createBox(env);
	}

	private Box createBox(final TeXEnvironment env) {
		final TeXFont tf = env.getTeXFont();
		// use "quad" from a font marked as an "mu font"
		final double f = tf.getQuad(env.getStyle(), TeXFont.MUFONT) / 18.;

		return new GlueBox(space * f, stretch * f, shrink * f);
	}

	private static final int index(final int i, final int j, final int k) {
		return i | (j << 3) | (k << 6);
	}

	@Override
	public String toString() {
		return "Glue: " + space;
	}
}
