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

/**
 * Array/array. This class is part of model.
 * <p/>
 * array(i)
 * array(i*j)
 * vector(i)
 * array(i*j)
 *
 * @author Bea Petrovicova
 */
public class MathArray extends MathContainer {

    private int columns, rows;
    private MetaArray meta;

    public MathArray(MetaArray meta, int columns) {
        super(columns);
        this.meta = meta;
        this.columns = columns;
        this.rows = 1;
    }

    public MathArray(MetaArray meta, int columns, int rows) {
        super(columns * rows);
        this.meta = meta;
        this.columns = columns;
        this.rows = rows;
    }

    public void addRow() {
        for (int i = 0; i < columns; i++) {
            MathSequence argument = new MathSequence();
            argument.setParent(this);
            arguments.add(argument);
        }
        rows += 1;
    }

    public MathSequence getArgument(int i) {
        return (MathSequence) super.getArgument(i);
    }

    public void setArgument(int i, MathSequence argument) {
        super.setArgument(i, argument);
    }

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
    public void addArgument(int i, MathComponent argument) {
        arguments.add(i, argument);
        columns += 1;
    }

    public void delArgument(int i) {
        arguments.remove(i);
        columns -= 1;
    }

    /**
     * Sets i-th row, j-th column cell.
     */
    public void setArgument(int i, int j, MathSequence argument) {
        setArgument(i * columns + j, argument);
    }

    /**
     * Returns i-th row, j-th column cell.
     */
    public MathSequence getArgument(int i, int j) {
        return getArgument(i * columns + j);
    }

    /**
     * Uid name.
     */
    public String getName() {
        return meta.getName();
    }

    public MetaComponent getOpen() {
        return meta.getOpen();
    }

    public char getOpenKey() {
        return meta.getOpenKey();
    }

    public MetaComponent getClose() {
        return meta.getClose();
    }

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

    public MathArray copy() {
        MathArray array = new MathArray(meta, columns, rows);
        array.copy(0, 0, this);
        return array;
    }

    /**
     * Copy array into this object (with silent clipping).
     *
     * @param ioffset leading empty rows
     * @param joffset leading empty columns.
     */
    public void copy(int ioffset, int joffset, MathArray array) {
        for (int i = 0; (i < (rows + joffset) || i < array.rows); i++) {
            for (int j = 0; (j < (columns + ioffset) || j < array.columns); j++) {
                MathSequence component = array.getArgument(i, j);
                component = component.copy();
                setArgument(i + ioffset, j + joffset, component);
            }
        }
    }

    /**
     * Number of columns.
     */
    public int columns() {
        return columns;
    }

    /**
     * Number of rows.
     */
    public int rows() {
        return rows;
    }

	public void checkMatrix(MetaModel metaModel) {
		int matrixWidth = -1;
		for (int i = 0; i < this.size(); i++) {
			if (getArgument(i).size() == 1
					&& getArgument(i).getArgument(0) instanceof MathArray) {
				MathArray row = (MathArray) getArgument(i).getArgument(0);

				if (row.meta != metaModel.getArray(MetaArray.CURLY)) {
					return;
				}
				else if (matrixWidth == -1) {
					matrixWidth = row.size();
				} else if (matrixWidth != row.size()) {
					return;
				}
			}
		}

		if (matrixWidth >= 0) {
			this.columns = matrixWidth;
			this.rows = size();
			ArrayList<MathComponent> entries = new ArrayList<MathComponent>();
			for (int i = 0; i < this.size(); i++) {
				for (int j = 0; j < ((MathContainer) getArgument(i)
						.getArgument(0)).size(); j++) {
					MathComponent arg = ((MathContainer) getArgument(i)
							.getArgument(0)).getArgument(j);
					arg.setParent(this);
					entries.add(arg);
				}
			}
			this.arguments.clear();
			this.arguments.addAll(entries);
			this.meta = metaModel.getMatrix();
		}

	}

}