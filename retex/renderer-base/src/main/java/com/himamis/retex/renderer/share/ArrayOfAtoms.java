/* ArrayOfAtoms.java
 * =========================================================================
 * This file is part of the JLaTeXMath Library - http://forge.scilab.org/jlatexmath
 *
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

package com.himamis.retex.renderer.share;

import java.util.ArrayList;

import com.himamis.retex.renderer.share.platform.graphics.Color;

public class ArrayOfAtoms implements AtomConsumer {

	protected RowAtom ra;
	protected ArrayList<ArrayList<Atom>> array;
	protected ArrayList<ArrayList<Color>> colors;
	protected ArrayList<Color> rowcolors;
	protected ArrayList<Atom> currentRow;
	public int col;
	protected int row;
	protected boolean oneColumn = false;

	public ArrayOfAtoms() {
		ra = new RowAtom();
		array = new ArrayList<ArrayList<Atom>>();
		currentRow = new ArrayList<Atom>();
		array.add(currentRow);
		row = 0;
	}

	public ArrayList<Atom> getFirstColumn() {
		final ArrayList<Atom> l = new ArrayList<>(row);
		for (final ArrayList<Atom> al : array) {
			if (al == null || al.size() == 0) {
				l.add(EmptyAtom.get());
			} else {
				l.add(al.get(0));
			}
		}
		return l;
	}

	@Override
	public boolean init(TeXParser tp) {
		return false;
	}

	@Override
	public boolean close(TeXParser tp) {
		return false;
	}

	@Override
	public boolean isClosable() {
		return false;
	}

	@Override
	public RowAtom steal(TeXParser tp) {
		final RowAtom old = ra;
		ra = new RowAtom();
		return old;
	}

	@Override
	public boolean isArray() {
		return true;
	}

	@Override
	public boolean isAmpersandAllowed() {
		return !oneColumn;
	}

	@Override
	public boolean isHandlingArg() {
		return false;
	}

	@Override
	public void lbrace(TeXParser tp) {
	}

	@Override
	public void rbrace(TeXParser tp) {
	}

	public final boolean isOneColumn() {
		return oneColumn;
	}

	public final void setOneColumn(boolean oneColumn) {
		this.oneColumn = oneColumn;
	}

	@Override
	public void add(TeXParser tp, Atom a) {
		if (a instanceof EnvArray.ColSep) {
			currentRow.add(ra.simplify());
			ra = new RowAtom();
		} else if (a instanceof EnvArray.RowSep) {
			currentRow.add(ra.simplify());
			currentRow = new ArrayList<Atom>();
			array.add(currentRow);
			ra = new RowAtom();
			++row;
		} else if (a instanceof EnvArray.CellColor) {
			updateColors(((EnvArray.CellColor) a).getColor());
		} else if (a instanceof EnvArray.RowColor) {
			updateRowColors(((EnvArray.RowColor) a).getColor());
		} else {
			ra.add(a);
			if (a instanceof HlineAtom) {
				addRow();
			} else if (a instanceof HdotsforAtom
					|| a instanceof MulticolumnAtom) {
				final MulticolumnAtom ma = (MulticolumnAtom) a;
				addCol(ma.getSkipped());
			}
		}
	}

	public void add(Atom a) {
		if (a instanceof EnvArray.ColSep) {
			currentRow.add(ra.simplify());
			ra = new RowAtom();
		} else if (a instanceof EnvArray.RowSep) {
			currentRow.add(ra.simplify());
			currentRow = new ArrayList<Atom>();
			array.add(currentRow);
			ra = new RowAtom();
			++row;
		} else {
			ra.add(a);
		}
	}

	public void updateRowColors(Color c) {
		if (rowcolors == null) {
			rowcolors = new ArrayList<Color>(array.size());
		}
		final int N = array.size() - rowcolors.size();
		for (int i = 0; i < N; i++) {
			rowcolors.add(null);
		}
		rowcolors.set(rowcolors.size() - 1, c);
	}

	public void updateColors(Color c) {
		if (colors == null) {
			colors = new ArrayList<ArrayList<Color>>(array.size());
		}
		final int N = array.size() - colors.size();
		for (int i = 0; i < N; ++i) {
			colors.add(null);
		}

		final int last = colors.size() - 1;
		ArrayList<Color> rowC = colors.get(last);
		ArrayList<Atom> rowA = array.get(last);
		if (rowC == null) {
			rowC = new ArrayList<Color>(rowA.size());
			colors.set(last, rowC);
		}
		final int Nc = rowA.size() - rowC.size();
		for (int i = 0; i < Nc; ++i) {
			rowC.add(null);
		}
		rowC.add(c);
	}

	public Color getColor(final int i, final int j) {
		if (colors != null) {
			if (i < colors.size()) {
				final ArrayList<Color> colorsR = colors.get(i);
				if (colorsR != null && j < colorsR.size()) {
					final Color c = colorsR.get(j);
					if (c != null) {
						return c;
					}
				}
			}
		}
		if (rowcolors != null && i < rowcolors.size()) {
			return rowcolors.get(i);
		}
		return null;
	}

	@Override
	public Atom getLastAtom() {
		return ra.getLastAtom();
	}

	private void addCol(int n) {
		// this function is used when a multicolumn is added
		currentRow.add(ra.simplify());
		ra = new RowAtom();
		for (int i = 0; i < n - 2; i++) {
			currentRow.add(null);
		}
	}

	private void addRow() {
		add(null, EnvArray.RowSep.get());
	}

	public int getRows() {
		return row;
	}

	public int getCols() {
		return col;
	}

	public Atom get(final int i, final int j) {
		return array.get(i).get(j);
	}

	public VRowAtom getAsVRow() {
		VRowAtom vr = new VRowAtom();
		vr.setAddInterline(true);
		for (ArrayList<Atom> r : array) {
			for (Atom a : r) {
				vr.append(a);
			}
		}

		return vr;
	}

	public void checkDimensions() {
		if (currentRow.size() != 0 || !ra.isEmpty()) {
			addRow();
		}

		row = array.size() - 1;
		col = array.get(0).size();

		// Get the max number of columns
		for (int i = 1; i < row; ++i) {
			col = Math.max(array.get(i).size(), col);
		}

		/*
		 * Thanks to Juan Enrique Escobar Robles for this patch which lets the
		 * user have empty columns
		 */
		for (int i = 0; i < row; ++i) {
			final ArrayList<Atom> r = array.get(i);
			final int N = col - r.size();
			if (N > 0) {
				r.ensureCapacity(col);
				for (int j = 0; j < N; ++j) {
					r.add(null);
				}
			}
		}
	}

	@Override
	public String toString() {
		String s = "";
		for (ArrayList<Atom> aa : array) {
			for (Atom a : aa) {
				s += a + " & ";
			}
			s += "\n";
		}

		return s;
	}
}
