/*
GeoGebra - Dynamic Mathematics for Schools
Copyright (c) GeoGebra GmbH, Altenbergerstr 69, 4040 Linz, Austria
https://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by
the Free Software Foundation.
*/

package org.geogebra.editor.share.catalog;

import static org.geogebra.editor.share.util.ListUtilities.findFirst;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import org.geogebra.editor.share.util.Greek;
import org.geogebra.editor.share.util.Unicode;

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
public class TemplateCatalog {

	private final static ArrayTemplateFactory arrays = new ArrayTemplateFactory();
	private final static FunctionTemplateCatalog functions = new FunctionTemplateCatalog();
	private final static SymbolTemplateCatalog symbols = new SymbolTemplateCatalog();

	private boolean forceBracketAfterFunction;

	private final ArrayTemplate matrixGroup;
	private final FunctionTemplateFactory customFunctionTemplateFactory;
	private final CharacterTemplateFactory characterTemplateFactory;
	private final List<ArrayTemplate> arrayTemplateGroup;
	private final List<FunctionTemplate> generalFunctionTemplateGroup;
	private final SymbolTemplateMap operatorGroup;
	private final HashMap<String, CharacterTemplate> mergeLookup = new HashMap<>();
	private final TreeSet<String> reverseSuffixes = new TreeSet<>();
	private final SymbolTemplateMap symbolGroup;
	private final List<String> inputBoxFunctionVars = new ArrayList<>();

	/**
	 * Create new catalog.
	 */
	public TemplateCatalog() {
		characterTemplateFactory = new CharacterTemplateFactory(); // characters/characters
		customFunctionTemplateFactory = new FunctionTemplateFactory(); // functions/functions

		arrayTemplateGroup = arrays.createArrays(); // arrays/arrays
		matrixGroup = arrays.createMatrix(); //

		generalFunctionTemplateGroup = functions.createGeneralFunctions(); // functions/general

		operatorGroup = symbols.createOperators(); // operators/operators
		symbolGroup = symbols.createSymbols(); // symbols/symbols
		for (SymbolTemplate operator : this.operatorGroup) {
			if (operator.getCasName().length() > 1) {
				addNamedSymbol(operator.getCasName(), operator);
			}
		}
	}

	/**
	 * Enable automatic substitutions (e.g. pi -&gt; unicode pi)
	 */
	public void enableSubstitutions() {
		for (Greek letter : Greek.values()) {
			String name = letter.name();
			// epsilon, Epsilon, upsilon, Upsilon need special treatment
			if (!"psi".equals(name) && name.contains("psi")) {
				name = name.replace("psi", Unicode.psi + "");
			}
			String unicode = letter.getUnicodeNonCurly() + "";
			if (name.equals("phi")) {
				unicode = getPhiUnicode();
			}
			addNamedSymbol(name,
					symbolGroup.getSymbol(unicode));
		}
		addNamedSymbol("inf",
				symbolGroup.getSymbol(Unicode.INFINITY + ""));
		addNamedSymbol("deg",
				symbolGroup.getSymbol(Unicode.DEGREE_STRING));
	}

	/**
	 * @return right phi unicode based on the function variable
	 */
	public String getPhiUnicode() {
		if (inputBoxFunctionVars.contains(Unicode.phi + "")) {
			return Unicode.phi + "";
		}
		return Unicode.phi_symbol + "";
	}

	/**
	 * @param functionVars function variable list
	 */
	public void setInputBoxFunctionVars(List<String> functionVars) {
		inputBoxFunctionVars.clear();
		inputBoxFunctionVars.addAll(functionVars);
	}

	/**
	 * @param name array tag
	 * @return array template
	 */
	public ArrayTemplate getArray(Tag name) {
		return getTemplate(name, arrayTemplateGroup);
	}

	/**
	 * @param arrayOpenKey open parenthesis
	 * @return array with given parentheses
	 */
	public ArrayTemplate getArray(char arrayOpenKey) {
		for (ArrayTemplate arrayTemplate : arrayTemplateGroup) {
			if (arrayTemplate.getOpenDelimiter().getCharacter() == arrayOpenKey) {
				return arrayTemplate;
			}
		}
		throw new ArrayIndexOutOfBoundsException(
				"Template not found " + arrayOpenKey);
	}

	/**
	 * @param arrayCloseKey closing parenthesis key ')', ']', etc.
	 * @return the array template for the given closing key
	 */
	public ArrayTemplate getArrayByCloseKey(char arrayCloseKey) {
		return findFirst(arrayTemplateGroup,
				arrayTemplate -> arrayTemplate.getCloseDelimiter().getCharacter() == arrayCloseKey);
	}

	/**
	 * @return matrix
	 */
	public ArrayTemplate getMatrix() {
		return matrixGroup;
	}

	/**
	 * @param name character name
	 * @return character
	 */
	public CharacterTemplate getCharacter(String name) {
		return characterTemplateFactory.createCharacter(name);
	}

	/**
	 * @param name operator name
	 * @return Operator.
	 */
	public boolean isOperator(String name) {
		return operatorGroup.getSymbol(name) != null;
	}

	/**
	 * get operator
	 * @param name operator name
	 * @return operator
	 */
	public CharacterTemplate getOperator(String name) {
		return operatorGroup.getSymbol(name);
	}

	/**
	 * @param name character name
	 * @return whether it's a symbol
	 */
	public boolean isSymbol(String name) {
		return symbolGroup.getSymbol(name) != null;
	}

	/**
	 * get symbol
	 * @param name symbol name
	 * @return symbol template
	 */
	public SymbolTemplate getSymbol(String name) {
		return symbolGroup.getSymbol(name);
	}

	/**
	 * get general function
	 * @param name function name
	 * @return built-in function
	 */
	public FunctionTemplate getGeneral(Tag name) {
		return getTemplate(name, generalFunctionTemplateGroup);
	}

	/**
	 * Function.
	 * @param casName function name
	 * @return whether it's acceptable
	 */
	public boolean isFunction(String casName) {
		return Tag.lookup(casName) != null
				|| FunctionTemplateFactory.isAcceptable(casName);
	}

	/**
	 * get function
	 * @param name function name
	 * @param square whether to use square brackets
	 * @return function
	 */
	public FunctionTemplate getFunction(String name, boolean square) {
		return customFunctionTemplateFactory.createFunction(name, square);
	}

	/**
	 * @param name template tag
	 * @param list parent group
	 * @return template of parent group with given tag
	 * @throws ArrayIndexOutOfBoundsException when tag unknown in this group
	 */
	public <T extends Template> T getTemplate(Tag name, List<T> list)
			throws ArrayIndexOutOfBoundsException {
		T template = findFirst(list, t -> t.getTag().equals(name));
		if (template != null) {
			return template;
		}

		throw new ArrayIndexOutOfBoundsException(
				"Template not found:" + name);
	}

	/**
	 * @param key open parenthesis
	 * @return whether array with given open key exists
	 */
	public boolean isArrayOpenKey(char key) {
		return findFirst(arrayTemplateGroup,
				arrayTemplate -> arrayTemplate.getOpenDelimiter().getCharacter() == key) != null;
	}

	/**
	 * @param key key
	 * @return whether key is one of (, [
	 */
	public boolean isFunctionOpenKey(char key) {
		boolean isFunctionOpenKey = getArray(Tag.REGULAR).getOpenDelimiter().getCharacter() == key;
		isFunctionOpenKey |= getArray(Tag.SQUARE).getOpenDelimiter().getCharacter() == key;
		return isFunctionOpenKey;
	}

	/**
	 * @param key key
	 * @return whether key is closing parenthesis of an array
	 */
	public boolean isArrayCloseKey(char key) {
		boolean isArrayCloseKey = getArray(Tag.REGULAR)
				.getCloseDelimiter().getCharacter() == key;
		isArrayCloseKey |= getArray(Tag.SQUARE)
				.getCloseDelimiter().getCharacter() == key;
		isArrayCloseKey |= getArray(Tag.CURLY)
				.getCloseDelimiter().getCharacter() == key;
		isArrayCloseKey |= getArray(Tag.FLOOR)
				.getCloseDelimiter().getCharacter() == key;
		isArrayCloseKey |= getArray(Tag.CEIL)
				.getCloseDelimiter().getCharacter() == key;
		return isArrayCloseKey;
	}

	/**
	 * @param name character name
	 * @return a single character with given multi-character name
	 */
	public CharacterTemplate merge(String name) {
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
		String higher = reverseSuffixes.higher(suffix);
		return higher != null && higher.startsWith(suffix);
	}

	public boolean isForceBracketAfterFunction() {
		return forceBracketAfterFunction;
	}

	public void setForceBracketAfterFunction(boolean forceBracketAfterFunction) {
		this.forceBracketAfterFunction = forceBracketAfterFunction;
	}

	private void addNamedSymbol(String name, CharacterTemplate symbol) {
		mergeLookup.put(name, symbol);
		reverseSuffixes.add(new StringBuilder(name).reverse().toString());
	}
}
