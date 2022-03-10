package org.geogebra.common.gui.view.table;

import java.util.Objects;

public class TableValuesCell {

	private final boolean erroneous;
	private final String input;

	/**
	 * Create a TableValuesCell model.
	 * @param input input string
	 * @param erroneous if the input has errors
	 */
	public TableValuesCell(String input, boolean erroneous) {
		this.input = input;
		this.erroneous = erroneous;
	}

	public boolean isErroneous() {
		return erroneous;
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
		return erroneous == that.erroneous && input.equals(that.input);
	}

	@Override
	public int hashCode() {
		return Objects.hash(erroneous, input);
	}
}
