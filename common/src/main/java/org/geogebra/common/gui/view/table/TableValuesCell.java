package org.geogebra.common.gui.view.table;

public class TableValuesCell {

	private final boolean erroneus;
	private final String input;

	public TableValuesCell(String input, boolean erroneus) {
		this.input = input;
		this.erroneus = erroneus;
	}

	public boolean isErroneus() {
		return erroneus;
	}

	public String getInput() {
		return input;
	}
}
