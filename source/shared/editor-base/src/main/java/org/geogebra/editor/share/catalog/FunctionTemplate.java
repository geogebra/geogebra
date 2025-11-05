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

import java.util.Locale;

import javax.annotation.Nonnull;

/**
 * Template for mathematical functions with parameters.
 */
public class FunctionTemplate extends Template {

	private final Parameter[] arguments;
	private final int insertIndex;
	private final int defaultSize;

	/**
	 * @param name function type
	 * @param texName tex name
	 * @param arguments arguments
	 */
	FunctionTemplate(Tag name, String texName, @Nonnull Parameter[] arguments,
			int insertIndex, int defaultSize) {
		super(name, texName);
		this.arguments = arguments;
		this.insertIndex = insertIndex;
		this.defaultSize = defaultSize;
	}

	FunctionTemplate(Tag name, String texName, @Nonnull Parameter[] arguments,
			int insertIndex) {
		this(name, texName, arguments, insertIndex, -1);
	}

	FunctionTemplate(Tag name, String texName, @Nonnull Parameter... arguments) {
		this(name, texName, arguments, 0, -1);
	}

	FunctionTemplate(Tag name) {
		this(name, name.toString().toLowerCase(Locale.ROOT), Parameter.BASIC);
	}

	/**
	 * @return number of arguments.
	 */
	public int getArgumentCount() {
		return defaultSize < 0 ? arguments.length : defaultSize;
	}

	/**
	 * @return Insert Index
	 */
	public int getInsertIndex() {
		return insertIndex;
	}

	/**
	 * Up Index for n-th argument
	 * @param n current arg index
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
	 * @param n current arg index
	 * @return arg index after down key pressed
	 */
	public int getDownIndex(int n) {
		if (arguments.length <= n) {
			return -1;
		}
		return arguments[n].getDownIndex();
	}

	/**
	 * @return opening bracket
	 */
	public char getOpeningBracket() {
		return getTag() == Tag.APPLY_SQUARE ? '[' : '(';
	}

	/**
	 * @return closing bracket
	 */
	public char getClosingBracket() {
		return getTag() == Tag.APPLY_SQUARE ? ']' : ')';
	}

}
