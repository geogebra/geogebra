/* MultlineAtom.java
 * =========================================================================
 * This file is part of the JLaTeXMath Library - http://forge.scilab.org/jlatexmath
 *
 * Copyright (C) 2010 DENIZET Calixte
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
 * An atom representing a vertical row of other atoms.
 */
public class MultlineAtom extends Atom {

	public static final SpaceAtom vsep_in = new SpaceAtom(Unit.EX, 0.,
			1., 0.);
	public static final int MULTLINE = 0;
	public static final int GATHER = 1;
	public static final int GATHERED = 2;

	private ArrayOfAtoms column;
	private int type;

	public MultlineAtom(ArrayOfAtoms column, int type) {
		this.column = column;
		this.type = type;
	}

	@Override
	public Box createBox(TeXEnvironment env) {
		if (type == GATHERED) {
			return new ArrayAtom(column, ArrayOptions.getEmpty())
					.createBox(env);
		}
		Box[] boxes = new Box[column.row];
		for (int i = 0; i < column.row; ++i) {
			boxes[i] = column.get(i, 0).createBox(env);
		}
		double tw = env.lengthSettings().getTextwidth(env);
		if (tw == Double.POSITIVE_INFINITY) {
			tw = -Double.POSITIVE_INFINITY;
			for (int i = 0; i < column.row; ++i) {
				tw = Math.max(tw, boxes[i].getWidth());
			}
		}

		final VerticalBox vb = new VerticalBox();
		Atom at = column.get(0, 0);
		TeXConstants.Align atAlignment = at.getAlignment();
		TeXConstants.Align alignment;
		if (atAlignment != TeXConstants.Align.NONE) {
			alignment = atAlignment;
		} else {
			alignment = type == GATHER ? TeXConstants.Align.CENTER
					: TeXConstants.Align.LEFT;
		}

		vb.add(new HorizontalBox(boxes[0], tw, alignment));
		Box Vsep = vsep_in.createBox(env);
		for (int i = 1; i < column.row - 1; i++) {
			at = column.get(i, 0);
			atAlignment = at.getAlignment();
			alignment = atAlignment == TeXConstants.Align.NONE
					? TeXConstants.Align.CENTER : atAlignment;
			vb.add(Vsep);
			vb.add(new HorizontalBox(boxes[i], tw, alignment));
		}

		if (column.row > 1) {
			at = column.get(column.row - 1, 0);
			atAlignment = at.getAlignment();
			if (atAlignment != TeXConstants.Align.NONE) {
				alignment = atAlignment;
			} else {
				alignment = type == GATHER ? TeXConstants.Align.CENTER
						: TeXConstants.Align.RIGHT;
			}
			vb.add(Vsep);
			vb.add(new HorizontalBox(boxes[column.row - 1], tw, alignment));
		}

		final double height = vb.getHeight() + vb.getDepth();
		vb.setHeight(height / 2.);
		vb.setDepth(height / 2.);

		return vb;
	}

}
