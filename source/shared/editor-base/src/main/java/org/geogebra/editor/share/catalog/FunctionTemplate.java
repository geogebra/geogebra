/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
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
