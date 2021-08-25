package org.geogebra.common.gui.view.table;

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;

public class TableValuesInputProcessor implements TableValuesProcessor {

	private Construction cons;

	/**
	 * Creates a TableValuesInputProcessor
	 * @param cons construction
	 */
	public TableValuesInputProcessor(Construction cons) {
		this.cons = cons;
	}

	@Override
	public void processInput(@Nonnull String input, @Nonnull GeoList list, int index)
			throws InvalidInputException {
		ensureCapacity(list, index);
		GeoNumeric numeric = parseInput(input);
		list.setListElement(index, numeric);
		numeric.notifyUpdate();
	}

	private void ensureCapacity(GeoList list, int index) {
		list.ensureCapacity(index + 1);
		for (int i = list.size(); i < index + 1; i++) {
			list.add(new GeoNumeric(cons, Double.NaN));
		}
	}

	private GeoNumeric parseInput(String input) throws InvalidInputException {
		String trimmedInput = input.trim();
		if (trimmedInput.equals("")) {
			return new GeoNumeric(cons, Double.NaN);
		}
		try {
			double parsedInput = Double.parseDouble(trimmedInput);
			return new GeoNumeric(cons, parsedInput);
		} catch (NumberFormatException e) {
			throw new InvalidInputException();
		}
	}

}
