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

import java.util.List;

public class MetaArray extends MetaComponent implements MetaGroup {

    public static final String CURLY = "curly";
    public static final String REGULAR = "regular";
    public static final String SQUARE = "square";
    public static final String APOSTROPHES = "apostrophes";

    private MetaComponent open;
    private MetaComponent close;
    private MetaComponent field;
    private MetaComponent row;
    private String tagName;

    MetaArray(String tagName, String name, List<MetaComponent> metaComponents) {
        super(name, name, name, (char) 0, (char) 0);
        this.tagName = tagName;
        setupComponents(metaComponents);
    }

    private void setupComponents(List<MetaComponent> metaComponents) {
        open = getComponent(metaComponents, MetaModel.OPEN);
        close = getComponent(metaComponents, MetaModel.CLOSE);
        field = getComponent(metaComponents, MetaModel.FIELD);
        row = getComponent(metaComponents, MetaModel.ROW);
    }

    private MetaComponent getComponent(List<MetaComponent> metaComponents, String name) {
        for (MetaComponent metaComponent : metaComponents) {
            if (metaComponent.getName().equals(name)) {
                return metaComponent;
            }
        }
        throw new RuntimeException("Component does not exist");
    }

    public MetaComponent getOpen() {
        return open;
    }

    public char getOpenKey() {
        return open.getKey();
    }

    public MetaComponent getClose() {
        return close;
    }

    public char getCloseKey() {
        return close.getKey();
    }

    public MetaComponent getField() {
        return field;
    }

    public char getFieldKey() {
        return field.getKey();
    }

    public MetaComponent getRow() {
        return row;
    }

    public char getRowKey() {
        return row.getKey();
    }

    public String getTagName() {
        return tagName;
    }

    public boolean isArray() {
        return tagName.equals("Array");
    }

    public boolean isMatrix() {
        return tagName.equals("Matrix");
    }

	public String getGroup() {
		return tagName;
	}

	public MetaComponent getComponent(String componentName) {
		// TODO Auto-generated method stub
		return null;
	}

}
