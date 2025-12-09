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

package org.geogebra.common.kernel.parser;

/**
 * Flags for parsing expressions.
 */
public class ParserInfo {
	private final boolean inputBox;
	private final boolean decimalComma;

	/**
	 * @param inputBox whether the expression is parsed for input box
	 * @param decimalComma whether to prefer decimal comma to dot
	 */
	public ParserInfo(boolean inputBox, boolean decimalComma) {
		this.inputBox = inputBox;
		this.decimalComma = decimalComma;
	}

	/**
	 * @return whether the expression is parsed for input box
	 */
	public boolean isInputBox() {
		return inputBox;
	}

	/**
	 * @return whether to prefer decimal comma to dot
	 */
	public boolean isDecimalComma() {
		return decimalComma;
	}
}
