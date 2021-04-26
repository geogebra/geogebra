package org.geogebra.common.kernel.parser;

public class ParserInfo {
	private final boolean inputBox;
	private final boolean decimalComma;

	public ParserInfo(boolean inputBox, boolean decimalComma) {
		this.inputBox = inputBox;
		this.decimalComma = decimalComma;
	}

	public boolean isInputBox() {
		return inputBox;
	}

	public boolean isDecimalComma() {
		return decimalComma;
	}
}
