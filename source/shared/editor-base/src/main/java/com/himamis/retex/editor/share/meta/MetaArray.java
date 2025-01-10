/* MetaArray.java
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

package com.himamis.retex.editor.share.meta;

public class MetaArray extends MetaComponent {

    private MetaArrayComponent open;

	private MetaArrayComponent close;
    private MetaArrayComponent field;
    private MetaArrayComponent row;
	private int dimension;

	MetaArray(int dimension, Tag name) {
		super(name, name + "");
		this.dimension = dimension;
	}

    public MetaArrayComponent getOpen() {
        return open;
    }

    public char getOpenKey() {
        return open.getKey();
    }

    public MetaArrayComponent getClose() {
        return close;
    }

    public char getCloseKey() {
        return close.getKey();
    }

    public MetaArrayComponent getField() {
        return field;
    }

    public char getFieldKey() {
        return field.getKey();
    }

    public MetaArrayComponent getRow() {
        return row;
    }

    public char getRowKey() {
        return row.getKey();
    }

    public boolean isArray() {
		return dimension == 1;
    }

    public boolean isMatrix() {
		return dimension == 2;
    }

	public void setOpen(MetaArrayComponent open) {
		this.open = open;
	}

	public void setClose(MetaArrayComponent close) {
		this.close = close;
	}

	public void setField(MetaArrayComponent field) {
		this.field = field;
	}

	public void setRow(MetaArrayComponent row) {
		this.row = row;
	}

}
