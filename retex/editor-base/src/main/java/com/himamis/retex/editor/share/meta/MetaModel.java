/* MetaModel.java
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

import java.util.HashMap;

/**
 * Distinction between operators, core functions and functions is based on
 * visual aspects of math elements rather than based on mathematical analogy.
 * <p>
 * operator) a * b
 * <p>
 * core function) common math symbols with custom appearance such as fraction,
 * square root, integration
 * <p>
 * function(...)
 */
public class MetaModel {

    /* Arrays and matrices. */
    public static final String ARRAYS = "Arrays";
    public static final String MATRIX = "Matrix";
    /* Categories. */
    public static final String CHARACTERS = "Characters";
    public static final String GENERAL = "General";
    public static final String OPERATORS = "Operators";
    public static final String SYMBOLS = "Symbols";
    public static final String FUNCTIONS = "Functions";
    private final static MetaModelArrays arrays = new MetaModelArrays();
    private final static MetaModelFunctions functions = new MetaModelFunctions();
    private final static MetaModelSymbols symbols = new MetaModelSymbols();

    private int defaultArraySize = 1;
    private int defaultVectorSize = 1;
    private int defaultMatrixColumns = 2;
    private int defaultMatrixRows = 2;
	private MetaArray matrixGroup;
	private FunctionGroup customFunctionGroup;
	private CharacterGroup characterGroup;
	private ListMetaGroup arrayGroup;
	private ListMetaGroup generalFunctionGroup;
	private MetaGroupCollection operatorGroup;
	private HashMap<String, MetaCharacter> mergeLookup = new HashMap<>();
	private MetaGroupCollection symbolGroup;

    public MetaModel() {
		characterGroup = new CharacterGroup(); // characters/characters
		customFunctionGroup = new FunctionGroup(); // functions/functions

		arrayGroup = arrays.createArraysGroup(); // arrays/arrays
		matrixGroup = arrays.createMatrixGroup(); //

		generalFunctionGroup = functions.createGeneralFunctionsGroup(); // functions/general

		operatorGroup = symbols.createOperators(); // operators/operators
		symbolGroup = symbols.createSymbols(); // symbols/symbols
		for (MetaComponent operator : this.operatorGroup.getComponents()) {
			mergeLookup.put(((MetaSymbol) operator).getCasName(),
					(MetaCharacter) operator);
		}
    }

	private static MetaArray getMetaArray(ListMetaGroup metaGroup,
			Tag name) {
        return (MetaArray) metaGroup.getComponent(name);
    }

    /**
     * get array
     */
	public MetaArray getArray(Tag name) {
		return (MetaArray) getComponent(name, arrayGroup);
    }

    public MetaArray getArray(char arrayOpenKey) {
		for (MetaComponent component : arrayGroup.getComponents()) {
            MetaArray metaArray = (MetaArray) component;
            if (metaArray.getOpenKey() == arrayOpenKey) {
                return metaArray;
            }
        }
        throw new ArrayIndexOutOfBoundsException(
                "Component Not found " + arrayOpenKey);
    }

    /**
     * get matrix
     */
    public MetaArray getMatrix() {
		return matrixGroup;
    }

    /**
     * Character.
     */
    public boolean isCharacter(String name) {
        try {
            getCharacter(name);
            return true;
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }

    /**
     * get character
     */
    public MetaCharacter getCharacter(String name) {
		return (MetaCharacter) characterGroup.getComponent(name);
    }

    /**
     * Operator.
     */
    public boolean isOperator(String name) {
		return operatorGroup.getComponent(name) != null;
    }

    /**
     * get operator
     */
    public MetaCharacter getOperator(String name) {
		return (MetaCharacter) getComponent(name, operatorGroup);
    }

    /**
     * Symbol.
     */
    public boolean isSymbol(String name) {
		return symbolGroup.getComponent(name) != null;
    }

    /**
     * get symbol
     */
    public MetaSymbol getSymbol(String name) {
		return (MetaSymbol) getComponent(name, symbolGroup);
    }

    /**
     * Custom Function.
     */
	public boolean isGeneral(Tag name) {
		return name != Tag.APPLY && name != Tag.APPLY_SQUARE;
    }

    /**
     * get custom function
     */
	public MetaFunction getGeneral(Tag name) {
		return (MetaFunction) getComponent(name, generalFunctionGroup);
    }

    /**
     * Function.
     */
    public boolean isFunction(String casName) {
		return Tag.lookup(casName) != null
				|| FunctionGroup.isAcceptable(casName);
    }

    /**
     * get function
     */
    public MetaFunction getFunction(String name, boolean square) {

		return customFunctionGroup.getComponent(name, square);
    }

    /**
     * get component
     */
	public MetaComponent getComponent(String name, MetaGroupCollection group) {
		return group.getComponent(name);
	}

	/**
	 * @param name
	 *            component tag
	 * @param group
	 *            parent group
	 * @return component of parent group with given tag
	 * @throws ArrayIndexOutOfBoundsException
	 *             when tag unknown in this group
	 */
	public MetaComponent getComponent(Tag name, ListMetaGroup group)
			throws ArrayIndexOutOfBoundsException {

		MetaComponent meta = group.getComponent(name);
		if (meta != null) {
			return meta;
		}

		throw new ArrayIndexOutOfBoundsException(
				"Component Not found:" + name);
	}

    /**
     * get group
     */
	// public MetaGroup getGroup(Tag groupName) {
	// for (int i = 0; i < groups.size(); i++) {
	// if (groups.get(i).getName().equals(groupName)) {
	// return groups.get(i);
	// }
	// }
	//
	// throw new ArrayIndexOutOfBoundsException("ListMetaGroup Not found "
	// + groupName);
	// }

    /**
     * get groups
     */
	// public MetaGroup[] getGroups(Tag tab) {
	// ArrayList<MetaGroup> arrayList = new ArrayList<MetaGroup>();
	// for (int i = 0; i < groups.size(); i++) {
	// if (groups.get(i).getGroup().equals(tab)) {
	// arrayList.add(groups.get(i));
	// }
	// }
	// return arrayList.toArray(new MetaGroup[arrayList.size()]);
	// }

    public int getDefaultArraySize() {
        return defaultArraySize;
    }

    public void setDefaultArraySize(int defaultArraySize) {
        this.defaultArraySize = defaultArraySize;
    }

    public int getDefaultVectorSize() {
        return defaultVectorSize;
    }

    public void setDefaultVectorSize(int defaultVectorSize) {
        this.defaultVectorSize = defaultVectorSize;
    }

    public int getDefaultMatrixColumns() {
        return defaultMatrixColumns;
    }

    public void setDefaultMatrixColumns(int defaultMatrixColumns) {
        this.defaultMatrixColumns = defaultMatrixColumns;
    }

    public int getDefaultMatrixRows() {
        return defaultMatrixRows;
    }

    public void setDefaultMatrixRows(int defaultMatrixRows) {
        this.defaultMatrixRows = defaultMatrixRows;
    }

    public boolean isArrayOpenKey(char key) {
		ListMetaGroup metaGroup = arrayGroup;
        for (MetaComponent metaComponent : metaGroup.getComponents()) {
            MetaArray metaArray = (MetaArray) metaComponent;
            if (metaArray.getOpenKey() == key) {
                return true;
            }
        }
        return false;
    }

    public boolean isFunctionOpenKey(char key) {
		ListMetaGroup metaGroup = arrayGroup;
        boolean isFunctionOpenKey = false;
		isFunctionOpenKey |= getMetaArray(metaGroup, Tag.REGULAR)
				.getOpenKey() == key;
		isFunctionOpenKey |= getMetaArray(metaGroup, Tag.SQUARE)
				.getOpenKey() == key;
        return isFunctionOpenKey;
    }

    public boolean isArrayCloseKey(char key) {
		ListMetaGroup metaGroup = arrayGroup;
        boolean isArrayCloseKey = false;
		isArrayCloseKey |= getMetaArray(metaGroup, Tag.REGULAR)
				.getCloseKey() == key;
		isArrayCloseKey |= getMetaArray(metaGroup, Tag.SQUARE)
				.getCloseKey() == key;
		isArrayCloseKey |= getMetaArray(metaGroup, Tag.CURLY)
				.getCloseKey() == key;
		isArrayCloseKey |= getMetaArray(metaGroup, Tag.FLOOR)
				.getCloseKey() == key;
		isArrayCloseKey |= getMetaArray(metaGroup, Tag.CEIL)
				.getCloseKey() == key;
        return isArrayCloseKey;
    }

	/**
	 * @param prefix
	 *            prefix
	 * @param symbol
	 *            last added symbol
	 * @return a single character merged from prefix and symbol,
	 */
	public MetaCharacter merge(String prefix, MetaCharacter symbol) {
		String mergeName = prefix + symbol.getUnicodeString();
		return mergeLookup.get(mergeName);
	}
}
