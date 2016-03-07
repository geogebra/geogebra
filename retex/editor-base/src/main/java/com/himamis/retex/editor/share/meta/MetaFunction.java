/* MetaFunction.java
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

/**
 * MetaFunction Component.
 *
 * @author Bea Petrovicova
 */
public class MetaFunction extends MetaComponent {

    private final int order[];
    private int insertIndex, initialIndex;
    private MetaParameter arguments[];
    private String desc;

    public MetaFunction(String name, String casName, String texName, char key, MetaParameter parameters[]) {
        super(name, casName, texName, key, key);
        this.arguments = parameters != null ? parameters : new MetaParameter[0];
        insertIndex = 0;
        initialIndex = 0;

        order = new int[parameters != null ? parameters.length : 0];
        for (int inputOrder = 0; inputOrder < order.length; inputOrder++) {
            order[arguments[inputOrder].getOrder()] = inputOrder;
        }
    }

    /**
     * Description.
     */
    public String getDescription() {
        return desc;
    }

    /**
     * Description.
     */
    public void setDescription(String desc) {
        this.desc = desc;
    }

    /**
     * Parameter
     */
    public MetaParameter getParameter(int casOrder) {
        return arguments[casOrder];
    }

    /**
     * Number of arguments.
     */
    public int size() {
        return arguments.length;
    }

    /**
     * Insert Index
     */
    public int getInsertIndex() {
        return insertIndex;
    }

    /**
     * Insert Index
     */
    void setInsertIndex(int insertIndex) {
        this.insertIndex = insertIndex;
    }

    /**
     * Initial Index
     */
    public int getInitialIndex() {
        return initialIndex;
    }

    /**
     * Initial Index
     */
    void setInitialIndex(int initialIndex) {
        this.initialIndex = initialIndex;
    }

    /**
     * Up Index for n-th argument
     */
    public int getUpIndex(int n) {
        return arguments[n].getUpIndex();
    }

    /**
     * Down Index for n-th argument
     */
    public int getDownIndex(int n) {
        return arguments[n].getDownIndex();
    }

	/* Translate CAS argument order into input argument order. *
    public int getArgumentInputIndex(int casOrder) {
		return order[casOrder];
	} */

    public String getOpeningBracket() {
        return "(";
    }

    public String getClosingBracket() {
        return ")";
    }
}
