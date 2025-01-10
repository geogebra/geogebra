/* FlalignAtom.java
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

public class FlalignAtom extends AlignAtom {

	/**
	 * Creates an empty matrix
	 *
	 */
	public FlalignAtom(ArrayOfAtoms array) {
		super(array, false);
	}

	@Override
	public double[] getColumnSep(TeXEnvironment env, double width) {
		final int col = matrix.col;
		final double[] seps = new double[col + 1];
		final double w = env.lengthSettings().getTextwidth(env);

		// flalign env. : hsep=(textwidth-matWidth)/(2n+1) and hsep eq_lft
		// \medskip el_rgt hsep ... hsep elem hsep
		final double alignW = align.createBox(env).getWidth();
		double alignSep;
		if (w != Double.POSITIVE_INFINITY) {
			int halfcol = col / 2;
			int tmp = (col - 1) / 2;
			alignSep = Math.max((w - width - halfcol * alignW) / tmp, 0);
		} else {
			alignSep = hsep.createBox(env).getWidth();
		}

		seps[0] = seps[col] = 0.;
		for (int i = 1; i < col; i++) {
			seps[i] = (i % 2 == 0) ? alignSep : alignW;
		}

		return seps;
	}
}
