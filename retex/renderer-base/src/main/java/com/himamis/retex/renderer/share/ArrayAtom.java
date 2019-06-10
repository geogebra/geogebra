/* ArrayAtom.java
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

import java.util.ArrayList;
import java.util.List;

public class ArrayAtom extends Atom {

	public static final SpaceAtom hsep = new SpaceAtom(Unit.EM, 1.0,
			0.0, 0.0);
	public static final SpaceAtom semihsep = new SpaceAtom(Unit.EM,
			0.5, 0.0, 0.0);
	public static final SpaceAtom vsep_in = new SpaceAtom(Unit.EX,
			0.0, 1., 0.0);
	public static final SpaceAtom vsep_ext_top = new SpaceAtom(
			Unit.EX, 0.0, 0.4, 0.0);
	public static final SpaceAtom vsep_ext_bot = new SpaceAtom(
			Unit.EX, 0.0, 0.4, 0.0);

	protected ArrayOfAtoms matrix;
	protected ArrayOptions options;
	protected boolean spaceAround;

	/**
	 * Creates an empty matrix
	 *
	 */
	public ArrayAtom(ArrayOfAtoms array, ArrayOptions options,
			boolean spaceAround) {
		this.matrix = array;
		this.spaceAround = spaceAround;
		if (options != null) {
			this.options = options.complete(matrix.col);
		}
	}

	/**
	 * Creates an empty matrix
	 *
	 */
	public ArrayAtom(ArrayOfAtoms array, ArrayOptions options) {
		this(array, options, false);
	}

	public double[] getSepForColumns(double[] cseps) {
		final int col = matrix.col;
		double[] lrseps = new double[2 * col];
		for (int i = 0; i < col; ++i) {
			if (i == 0) {
				lrseps[0] = cseps[0];
				lrseps[1] = cseps[1] / 2.;
			} else if (i == col - 1) {
				lrseps[2 * col - 2] = cseps[col - 1] / 2.;
				lrseps[2 * col - 1] = cseps[col];
			} else {
				lrseps[2 * i] = cseps[i] / 2.;
				lrseps[2 * i + 1] = cseps[i + 1] / 2.;
			}

		}

		return lrseps;
	}

	public double[] getColumnSep(TeXEnvironment env, double width) {
		final int col = matrix.col;
		final double[] seps = new double[col + 1];

		// Array : hsep_col/2 elem hsep_col elem hsep_col ... hsep_col elem
		// hsep_col/2
		seps[0] = seps[col] = spaceAround ? semihsep.createBox(env).getWidth()
				: 0.;

		int i = 1;
		if (options.getAlignment(0) == TeXConstants.Align.NONE) {
			seps[1] = 0.;
			i = 2;
		}

		final double hw = hsep.createBox(env).getWidth();
		while (i < col) {
			if (options.getAlignment(i) == TeXConstants.Align.NONE) {
				seps[i + 1] = seps[i] = 0.;
				i += 2;
			} else {
				seps[i++] = hw;
			}
		}

		return seps;
	}

	@Override
	public Box createBox(TeXEnvironment env) {
		final int row = matrix.row;
		final int col = matrix.col;

		if (row == 0 || col == 0) {
			return StrutBox.getEmpty();
		}

		final Box[][] boxarr = new Box[row][col];
		final double[] rowDepth = new double[row];
		final double[] rowHeight = new double[row];
		final double[] colWidth = new double[col];
		final double drt = env.getTeXFont()
				.getDefaultRuleThickness(env.getStyle());
		final List<MulticolumnAtom> listMulti = new ArrayList<MulticolumnAtom>();

		final List<List<Atom>> separatorAtoms = options.getSeparators();
		final List<Box> separatorBoxes = options.getSeparatorBoxes(env);

		double matW = 0.;

		double hinit = Double.NEGATIVE_INFINITY;
		double dinit = Double.NEGATIVE_INFINITY;

		double arraystretch = env.lengthSettings().getFactor("arraystretch");
		final double tabcolsep = env.lengthSettings().getLength("tabcolsep", env);

		for (final Box b : separatorBoxes) {
			hinit = Math.max(hinit, b.getHeight());
			dinit = Math.max(dinit, b.getDepth());
		}

		for (int i = 0; i < row; ++i) {
			rowDepth[i] = dinit;
			rowHeight[i] = hinit;
			for (int j = 0; j < col; ++j) {
				final Atom at = matrix.get(i, j);
				final Box b = (at == null) ? StrutBox.getEmpty()
						: at.createBox(env);
				boxarr[i][j] = b;

				rowDepth[i] = Math.max(b.getDepth(), rowDepth[i]);
				rowHeight[i] = Math.max(b.getHeight(), rowHeight[i]);

				if (b.type != TeXConstants.TYPE_MULTICOLUMN) {
					colWidth[j] = Math.max(b.getWidth(), colWidth[j]);
					colWidth[j] += tabcolsep;
				} else {
					final MulticolumnAtom mcat = (MulticolumnAtom) at;
					mcat.setRowColumn(i, j);
					listMulti.add(mcat);
					final int n = mcat.getSkipped();
					final double w = b.getWidth() / n;
					for (int k = j; k < j + n; ++k) {
						colWidth[k] = Math.max(colWidth[k], w);
					}
				}
			}
			rowHeight[i] *= arraystretch;
		}

		final double[] seps = getColumnSep(env, 0. /* not used */);
		final double[] hseps = getSepForColumns(seps);

		for (final MulticolumnAtom multi : listMulti) {
			final int c = multi.getCol();
			final int r = multi.getRow();
			final int n = multi.getSkipped();
			final int N = c + n - 1;
			double w = colWidth[c] + hseps[2 * c + 1]
					+ separatorBoxes.get(c + 1).getWidth();
			for (int j = c + 1; j < N; ++j) {
				w += colWidth[j] + hseps[2 * j] + hseps[2 * j + 1]
						+ separatorBoxes.get(j + 1).getWidth();
			}
			w += colWidth[N] + hseps[2 * N];
			final double boxW = boxarr[r][c].getWidth();
			if (boxW > w) {
				final double extraW = (boxW - w) / n;
				for (int j = c; j < c + n; ++j) {
					colWidth[j] += extraW;
				}
				multi.setWidth(boxW);
			} else {
				multi.setWidth(w);
			}
		}

		for (int j = 0; j < col; ++j) {
			matW += colWidth[j] + hseps[2 * j] + hseps[2 * j + 1]
					+ separatorBoxes.get(j).getWidth();
		}
		matW += separatorBoxes.get(col).getWidth();

		final VerticalBox vb = new VerticalBox();
		final Box Vsep = vsep_in.createBox(env);
		vb.add(vsep_ext_top.createBox(env));
		final double vsepH = Vsep.getHeight();
		final double halfVsepH = vsepH / 2.;
		final double textwidth = env.lengthSettings().getTextwidth(env);

		for (int i = 0; i < row; ++i) {
			final HorizontalBox hb = new HorizontalBox();
			final double rhi = rowHeight[i];
			final double rdi = rowDepth[i];
			for (int j = 0; j < col; ++j) {
				final int typ = boxarr[i][j].type;
				if (typ == TeXConstants.TYPE_HLINE) {
					final HlineBox hlb = (HlineBox) boxarr[i][j];
					if (i >= 1 && boxarr[i
							- 1][j].type == TeXConstants.TYPE_HLINE) {
						hb.add(new StrutBox(0., 3. * drt, 0., 0.));
					}
					hlb.setDims(matW, -halfVsepH);
					hb.add(hlb);
					break;
				} else if (typ == TeXConstants.TYPE_INTERTEXT) {
					final double f = textwidth == Double.POSITIVE_INFINITY
							? colWidth[j] : textwidth;
					hb.add(new HorizontalBox(boxarr[i][j], f,
							TeXConstants.Align.LEFT));
					break;
				} else {
					final double l = hseps[2 * j];
					if (typ == TeXConstants.TYPE_MULTICOLUMN) {
						final MulticolumnAtom matom = (MulticolumnAtom) matrix
								.get(i, j);
						final int n = matom.getSkipped();
						final double r = hseps[2 * (j + n) - 1];
						if (matom.mustBeRecreated()) {
							boxarr[i][j] = matom.createBox(env);
						}
						final List<List<Atom>> separatorAtomsMC = matom.getOptions()
								.getSeparators();

						if (j == 0) {
							hb.add(createSeparator(env, separatorAtomsMC.get(0),
									rhi + rdi + vsepH, rdi + halfVsepH));
						}

						CellBox cb = new CellBox(boxarr[i][j], rhi + halfVsepH,
								rdi + halfVsepH, l, r, matom.getWidth(),
								matom.getOptions().getAlignment(0));

						cb.setBg(matrix.getColor(i, j));
						hb.add(cb);

						hb.add(createSeparator(env, separatorAtomsMC.get(1),
								rhi + rdi + vsepH, rdi + halfVsepH));
						j += n - 1;
					} else {
						if (j == 0) {
							hb.add(createSeparator(env, separatorAtoms.get(0),
									rhi + rdi + vsepH, rdi + halfVsepH));
						}

						final double r = hseps[2 * j + 1];
						CellBox cb = new CellBox(boxarr[i][j], rhi + halfVsepH,
								rdi + halfVsepH, l, r, colWidth[j],
								options.getAlignment(j));
						cb.setBg(matrix.getColor(i, j));
						hb.add(cb);
						hb.add(createSeparator(env, separatorAtoms.get(j + 1),
								rhi + rdi + vsepH, rdi + halfVsepH));
					}
				}
			}

			if (boxarr[i][0].type != TeXConstants.TYPE_HLINE) {
				hb.setHeight(rowHeight[i]);
				hb.setDepth(rowDepth[i]);
				vb.add(hb);

				if (i < row - 1) {
					vb.add(Vsep);
				}
			} else {
				vb.add(hb);
			}
		}

		vb.add(vsep_ext_bot.createBox(env));
		final double totalHeight = vb.getHeight() + vb.getDepth();
		final double axis = env.getTeXFont().getAxisHeight(env.getStyle());
		vb.setHeight(totalHeight / 2. + axis);
		vb.setDepth(totalHeight / 2. - axis);

		return vb;
	}

	private static Box createSeparator(TeXEnvironment env, List<Atom> separators,
			final double h, final double s) {
		for (Atom atom : separators) {
			if (atom instanceof VlineAtom) {
				VlineAtom vline = (VlineAtom) atom;
				vline.setHeight(h);
				vline.setShift(s);
			}
		}

		return new RowAtom(separators).createBox(env);
	}

	public ArrayOfAtoms getMatrix() {
		return matrix;
	}
}
