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

	private int insertIndex;
	private MetaParameter[] arguments;

	/**
	 * @param name
	 *            function trype
	 * @param texName
	 *            tex name
	 * @param parameters
	 *            parameters
	 */
	public MetaFunction(Tag name, String texName,
			MetaParameter[] parameters) {
		super(name, texName);
        this.arguments = parameters != null ? parameters : new MetaParameter[0];
        insertIndex = 0;
    }

    /**
	 * @return number of arguments.
	 */
    public int size() {
        return arguments.length;
    }

    /**
	 * @return Insert Index
	 */
    public int getInsertIndex() {
        return insertIndex;
    }

    /**
	 * Set insert index
	 * 
	 * @param insertIndex
	 *            insert index
	 */
    void setInsertIndex(int insertIndex) {
        this.insertIndex = insertIndex;
    }

	/**
	 * Up Index for n-th argument
	 * 
	 * @param n
	 *            current arg index
	 * @return arg index after up key pressed
	 */
	public int getUpIndex(int n) {
		if (arguments.length <= n) {
			return -1;
		}
		return arguments[n].getUpIndex();
	}

    /**
	 * Down Index for n-th argument
	 * 
	 * @param n
	 *            current arg index
	 * @return arg index after down key pressed
	 */
	public int getDownIndex(int n) {
		if (arguments.length <= n) {
			return -1;
		}
		return arguments[n].getDownIndex();
	}

	/* Translate CAS argument order into input argument order. *
    public int getArgumentInputIndex(int casOrder) {
		return order[casOrder];
	} */

	/**
	 * @return opening bracket
	 */
	public char getOpeningBracket() {
		return getName() == Tag.APPLY_SQUARE ? '[' : '(';
    }

	/**
	 * @return closing bracket
	 */
	public char getClosingBracket() {
		return getName() == Tag.APPLY_SQUARE ? ']' : ')';
	}
}
