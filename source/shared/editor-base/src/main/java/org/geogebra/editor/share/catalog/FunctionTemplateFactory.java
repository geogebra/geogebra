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

import javax.annotation.CheckForNull;

import org.geogebra.editor.share.input.Character;
import org.geogebra.editor.share.util.Unicode;

/**
 * Factory for creating custom function templates.
 */
public class FunctionTemplateFactory {

	private static final Parameter[] DEFAULT_PARAMETERS = new Parameter[]{
			Parameter.BASIC, Parameter.BASIC};
	private static final FunctionTemplate APPLY = new FunctionTemplate(Tag.APPLY, null,
			DEFAULT_PARAMETERS);
	private static final FunctionTemplate APPLY_SQUARE =
			new FunctionTemplate(Tag.APPLY_SQUARE, null,
					DEFAULT_PARAMETERS);

	/**
	 * @param name function name
	 * @param square use [ rather than (
	 * @return function
	 */
	@CheckForNull FunctionTemplate createFunction(String name, boolean square) {
		if (!isAcceptable(name)) {
			return null;
		}
		return square ? APPLY_SQUARE : APPLY;
	}

	/**
	 * @param functionName potential function name
	 * @return whether the name could be user defined function (has just letters
	 * + digits, contains letter)
	 */
	public static boolean isAcceptable(String functionName) {
		// Accept only functions that consist of no special characters
		String stem = functionName;
		while (!stem.isEmpty()
				&& primeOrPower(stem.charAt(stem.length() - 1))) {
			stem = stem.substring(0, stem.length() - 1);
		}
		return !stem.isEmpty()
				&& Character.areLettersOrDigits(stem)
				&& containsLetter(stem);
	}

	private static boolean primeOrPower(char c) {
		return c == '\'' || c == Unicode.SUPERSCRIPT_MINUS
				|| Unicode.isSuperscriptDigit(c);
	}

	private static boolean containsLetter(String functionName) {
		for (int i = 0; i < functionName.length(); i++) {
			if (Character.isLetter(functionName.charAt(i))) {
				return true;
			}
		}
		return false;
	}
}
