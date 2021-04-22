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
import java.util.TreeSet;

import com.himamis.retex.editor.share.util.Greek;
import com.himamis.retex.editor.share.util.Unicode;

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

    private final static MetaModelArrays arrays = new MetaModelArrays();
    private final static MetaModelFunctions functions = new MetaModelFunctions();
    private final static MetaModelSymbols symbols = new MetaModelSymbols();

    private int defaultArraySize = 1;
    private int defaultVectorSize = 1;
    private int defaultMatrixColumns = 2;
    private int defaultMatrixRows = 2;

    private boolean forceBracketAfterFunction;

	private MetaArray matrixGroup;
	private FunctionGroup customFunctionGroup;
	private CharacterGroup characterGroup;
	private ListMetaGroup<MetaArray> arrayGroup;
	private ListMetaGroup<MetaFunction> generalFunctionGroup;
	private MapMetaGroup operatorGroup;
	private HashMap<String, MetaCharacter> mergeLookup = new HashMap<>();
	private TreeSet<String> reverseSuffixes = new TreeSet<>();
	private MapMetaGroup symbolGroup;

	/**
	 * Create new meta model.
	 */
    public MetaModel() {
		characterGroup = new CharacterGroup(); // characters/characters
		customFunctionGroup = new FunctionGroup(); // functions/functions

		arrayGroup = arrays.createArraysGroup(); // arrays/arrays
		matrixGroup = arrays.createMatrixGroup(); //

		generalFunctionGroup = functions.createGeneralFunctionsGroup(); // functions/general

		operatorGroup = symbols.createOperators(); // operators/operators
		symbolGroup = symbols.createSymbols(); // symbols/symbols
		for (MetaSymbol operator : this.operatorGroup.getComponents()) {
			if (operator.getCasName().length() > 1) {
				addNamedSymbol(operator.getCasName(), operator);
			}
		}
	}

	/**
	 * Enable automatic substitutions (e.g. pi -> unicode pi)
	 */
	public void enableSubstitutions() {
		for (Greek letter: Greek.values()) {
			String name = letter.name();
			// epsilon, Epsilon, upsilon, Upsilon need special treatment
			if (!"psi".equals(name) && name.contains("psi")) {
				name = name.replace("psi", Unicode.psi + "");
			}
			addNamedSymbol(name,
					symbolGroup.getComponent(letter.getUnicodeNonCurly() + ""));
		}
		addNamedSymbol("inf",
				symbolGroup.getComponent(Unicode.INFINITY + ""));
		addNamedSymbol("deg",
				symbolGroup.getComponent(Unicode.DEGREE_STRING));
	}

	/**
	 * @param name
	 *            array tag
	 * @return array meta-component
	 */
	public MetaArray getArray(Tag name) {
		return getComponent(name, arrayGroup);
	}

	/**
	 * @param arrayOpenKey
	 *            open parenthesis
	 * @return array with given parentheses
	 */
    public MetaArray getArray(char arrayOpenKey) {
		for (MetaArray metaArray : arrayGroup.getComponents()) {
            if (metaArray.getOpenKey() == arrayOpenKey) {
                return metaArray;
            }
        }
        throw new ArrayIndexOutOfBoundsException(
                "Component Not found " + arrayOpenKey);
    }

    /**
	 * @return matrix
	 */
    public MetaArray getMatrix() {
		return matrixGroup;
    }

    /**
	 * @param name
	 *            input
	 * @return whether input is a character name
	 */
    public boolean isCharacter(String name) {
		// TODO never thrown?
        try {
            getCharacter(name);
            return true;
        } catch (ArrayIndexOutOfBoundsException e) {
			return false;
        }
    }

    /**
	 * @param name
	 *            character name
	 * @return character
	 */
    public MetaCharacter getCharacter(String name) {
		return characterGroup.getComponent(name);
    }

    /**
	 * @param name
	 *            operator name
	 * @return Operator.
	 */
    public boolean isOperator(String name) {
		return operatorGroup.getComponent(name) != null;
    }

    /**
	 * get operator
	 * 
	 * @param name
	 *            operator name
	 * @return operator
	 */
    public MetaCharacter getOperator(String name) {
		return operatorGroup.getComponent(name);
    }

    /**
	 * @param name
	 *            character name
	 * @return whether it's a symbol
	 */
    public boolean isSymbol(String name) {
		return symbolGroup.getComponent(name) != null;
    }

    /**
	 * get symbol
	 * 
	 * @param name
	 *            symbol name
	 * @return symbol meta component
	 */
    public MetaSymbol getSymbol(String name) {
		return symbolGroup.getComponent(name);
    }

    /**
	 * get custom function
	 * 
	 * @param name
	 *            function name
	 * @return built infunction
	 */
	public MetaFunction getGeneral(Tag name) {
		return getComponent(name, generalFunctionGroup);
    }

    /**
	 * Function.
	 * 
	 * @param casName
	 *            function name
	 * @return whether it's acceptable
	 */
    public boolean isFunction(String casName) {
		return Tag.lookup(casName) != null
				|| FunctionGroup.isAcceptable(casName);
    }

    /**
	 * get function
	 * 
	 * @param name
	 *            function name
	 * @param square
	 *            whether to use square brackets
	 * @return function
	 */
    public MetaFunction getFunction(String name, boolean square) {
		return customFunctionGroup.getComponent(name, square);
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
	public <T extends MetaComponent> T getComponent(Tag name,
			ListMetaGroup<T> group)
			throws ArrayIndexOutOfBoundsException {

		T meta = group.getComponent(name);
		if (meta != null) {
			return meta;
		}

		throw new ArrayIndexOutOfBoundsException(
				"Component Not found:" + name);
	}

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

	/**
	 * @param key
	 *            open parenthesis
	 * @return whether array with given open key exists
	 */
    public boolean isArrayOpenKey(char key) {
		for (MetaArray metaArray : arrayGroup.getComponents()) {
            if (metaArray.getOpenKey() == key) {
                return true;
            }
        }
        return false;
    }

	/**
	 * @param key
	 *            key
	 * @return whether key is one of (, [
	 */
    public boolean isFunctionOpenKey(char key) {
		boolean isFunctionOpenKey = getArray(Tag.REGULAR)
				.getOpenKey() == key;
		isFunctionOpenKey |= getArray(Tag.SQUARE)
				.getOpenKey() == key;
        return isFunctionOpenKey;
    }

	/**
	 * @param key
	 *            key
	 * @return whether key is closing parenthesis of an array
	 */
    public boolean isArrayCloseKey(char key) {
		boolean isArrayCloseKey = getArray(Tag.REGULAR)
				.getCloseKey() == key;
		isArrayCloseKey |= getArray(Tag.SQUARE)
				.getCloseKey() == key;
		isArrayCloseKey |= getArray(Tag.CURLY)
				.getCloseKey() == key;
		isArrayCloseKey |= getArray(Tag.FLOOR)
				.getCloseKey() == key;
		isArrayCloseKey |= getArray(Tag.CEIL)
				.getCloseKey() == key;
		return isArrayCloseKey;
	}

	/**
	 * @param name
	 *            character name
	 * @return a single character with given multi-character name
	 */
	public MetaCharacter merge(String name) {
		return mergeLookup.get(name);
	}

	/**
	 * @param suffix function name
	 * @return whether the string is reverse suffix of a known symbol
	 */
	public boolean isReverseSuffix(String suffix) {
		if (reverseSuffixes.contains(suffix)) {
			return true;
		}
		String higher =  reverseSuffixes.higher(suffix);
		return higher != null && higher.startsWith(suffix);
	}

	public boolean isForceBracketAfterFunction() {
		return forceBracketAfterFunction;
	}

	public void setForceBracketAfterFunction(boolean forceBracketAfterFunction) {
		this.forceBracketAfterFunction = forceBracketAfterFunction;
	}

	private void addNamedSymbol(String name, MetaCharacter symbol) {
		mergeLookup.put(name, symbol);
		reverseSuffixes.add(new StringBuilder(name).reverse().toString());
	}
}
