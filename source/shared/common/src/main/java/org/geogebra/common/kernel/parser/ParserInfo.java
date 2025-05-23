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
