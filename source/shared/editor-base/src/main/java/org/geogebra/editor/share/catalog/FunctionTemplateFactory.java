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
