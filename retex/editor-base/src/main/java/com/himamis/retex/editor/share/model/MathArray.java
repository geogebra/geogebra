/* MathArray.java
 * =========================================================================
 * This file is part of the Mirai Math TN - http://mirai.sourceforge.net
 *
 * Copyright (C) 2008-2009 Bea Petrovicova
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
 */

package com.himamis.retex.editor.share.model;

import java.util.ArrayList;

import com.himamis.retex.editor.share.meta.MetaArray;
import com.himamis.retex.editor.share.meta.MetaComponent;
import com.himamis.retex.editor.share.meta.MetaModel;
import com.himamis.retex.editor.share.meta.Tag;

/**
 * Array/array. This class is part of model.
 * <p>
 * array(i) array(i*j) vector(i) array(i*j)
 *
 * @author Bea Petrovicova
 */
public class MathArray extends MathContainer {

	private int columns;
	private int rows;
	private MetaArray meta;

	/**
	 * @param meta
	 *            meta model
	 * @param columns
	 *            number of columns
	 */
	public MathArray(MetaArray meta, int columns) {
		super(columns);
		this.meta = meta;
		this.columns = columns;
		this.rows = 1;
	}

	/**
	 * @param meta
	 *            meta model
	 * @param columns
	 *            number of columns
	 * @param rows
	 *            number of rows
	 */
	public MathArray(MetaArray meta, int columns, int rows) {
		super(columns * rows);
		this.meta = meta;
		this.columns = columns;
		this.rows = rows;
	}

	/**
	 * Add empty cell to each column.
	 */
	public void addRow() {
		for (int i = 0; i < columns; i++) {
			MathSequence argument = new MathSequence();
			argument.setParent(this);
			arguments.add(argument);
		}
		rows += 1;
	}

	@Override
	public MathSequence getArgument(int i) {
		return (MathSequence) super.getArgument(i);
	}

	public void setArgument(int i, MathSequence argument) {
		super.setArgument(i, argument);
	}

	/**
	 * Add a column.
	 */
	public void addArgument() {
		MathSequence argument = new MathSequence();
		super.addArgument(argument);
		columns += 1;
	}

	@Override
	public void addArgument(MathComponent argument) {
		super.addArgument(argument);
		columns += 1;
	}

	@Override
	public boolean addArgument(int i, MathComponent argument) {
		if (super.addArgument(i, argument)) {
			columns += 1;
			return true;
		}

		return false;
	}

	@Override
	public void delArgument(int i) {
		super.removeArgument(i);
		columns -= 1;
	}

	/**
	 * Sets i-th row, j-th column cell.
	 * 
	 * @param i
	 *            row
	 * @param j
	 *            column
	 * @param argument
	 *            cell value
	 */
	public void setArgument(int i, int j, MathSequence argument) {
		setArgument(i * columns + j, argument);
	}

	/**
	 * Returns i-th row, j-th column cell.
	 * 
	 * @param i
	 *            row
	 * @param j
	 *            column
	 * @return matrix cell
	 */
	public MathSequence getArgument(int i, int j) {
		return getArgument(i * columns + j);
	}

	public MetaComponent getOpen() {
		return meta.getOpen();
	}

	/**
	 * @return open key
	 */
	public char getOpenKey() {
		return meta.getOpenKey();
	}

	/**
	 * @return close meta component
	 */
	public MetaComponent getClose() {
		return meta.getClose();
	}

	/**
	 * @return close key
	 */
	public char getCloseKey() {
		return meta.getCloseKey();
	}

	public MetaComponent getField() {
		return meta.getField();
	}

	public char getFieldKey() {
		return meta.getFieldKey();
	}

	public MetaComponent getRow() {
		return meta.getRow();
	}

	public char getRowKey() {
		return meta.getRowKey();
	}

	public boolean is1DArray() {
		return rows() == 1 && meta.isArray();
	}

	public boolean isArray() {
		return meta.isArray();
	}

	public boolean isVector() {
		return rows() == 1 && meta.isMatrix();
	}

	public boolean isMatrix() {
		return meta.isMatrix();
	}

	@Override
	public MathArray copy() {
		MathArray array = new MathArray(meta, columns, rows);
		array.copy(0, 0, this);
		return array;
	}

	/**
	 * Copy array into this object (with silent clipping).
	 *
	 * @param ioffset
	 *            leading empty rows
	 * @param joffset
	 *            leading empty columns.
	 * @param array
	 *            original array
	 */
	public void copy(int ioffset, int joffset, MathArray array) {
		for (int i = 0; (i < (rows + joffset) || i < array.rows); i++) {
			for (int j = 0; (j < (columns + ioffset)
					|| j < array.columns); j++) {
				MathSequence component = array.getArgument(i, j);
				component = component.copy();
				setArgument(i + ioffset, j + joffset, component);
			}
		}
	}

	/**
	 * @return Number of columns.
	 */
	public int columns() {
		return columns;
	}

	/**
	 * @return Number of rows.
	 */
	public int rows() {
		return rows;
	}

	/**
	 * Check whether this is a matrix, update row/column values.
	 * 
	 * @param metaModel
	 *            new model
	 */
	public void checkMatrix(MetaModel metaModel) {
		int matrixWidth = numberOfColumns(metaModel);
		if (matrixWidth >= 0) {
			rows = size();
			flattenMatrix();
			columns = matrixWidth;
			meta = metaModel.getMatrix();
		}
	}

	// Returns the number of columns or -1 if it's not a matrix
	private int numberOfColumns(MetaModel metaModel) {
		int matrixWidth = -1;
		for (int i = 0; i < size(); i++) {
			if (getArgument(i).size() == 1
					&& getArgument(i).getArgument(0) instanceof MathArray) {
				MathArray row = (MathArray) getArgument(i).getArgument(0);

				if (row.meta != metaModel.getArray(Tag.CURLY)) {
					return -1;
				} else if (matrixWidth == -1) {
					matrixWidth = row.size();
				} else if (matrixWidth != row.size()) {
					return -1;
				}
			} else {
				return -1;
			}
		}
		return matrixWidth;
	}

	private void flattenMatrix() {
		ArrayList<MathComponent> entries = new ArrayList<>();
		for (int i = 0; i < size(); i++) {
			for (int j = 0; j < ((MathContainer) getArgument(i).getArgument(0))
					.size(); j++) {
				MathComponent arg = ((MathContainer) getArgument(i)
						.getArgument(0)).getArgument(j);
				entries.add(arg);
			}
		}
		clearArguments();
		for (MathComponent entry : entries) {
			addArgument(entry);
		}
	}

	/**
	 *
	 * @param container a MathFieldContainer
	 * @return true if container is a matrix.
	 */
	public static boolean isMatrix(MathComponent container) {
		return container instanceof MathArray
				&& ((MathArray) container).isMatrix();
	}
}