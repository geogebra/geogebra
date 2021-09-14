package org.geogebra.common.gui.view.table;

import java.util.Objects;

public class TableValuesCell {

	private final boolean erroneus;
	private final String input;

	/**
	 * Create a TableValuesCell model.
	 * @param input input string
	 * @param erroneus if the input has errors
	 */
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

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		TableValuesCell that = (TableValuesCell) o;
		return erroneus == that.erroneus && input.equals(that.input);
	}

	@Override
	public int hashCode() {
		return Objects.hash(erroneus, input);
	}
}
