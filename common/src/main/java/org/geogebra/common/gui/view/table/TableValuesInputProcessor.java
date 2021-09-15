package org.geogebra.common.gui.view.table;

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;

public class TableValuesInputProcessor implements TableValuesProcessor {

	private final Construction cons;

	/**
	 * Creates a TableValuesInputProcessor
	 * @param cons construction
	 */
	public TableValuesInputProcessor(Construction cons) {
		this.cons = cons;
	}

	@Override
	public void processInput(@Nonnull String input, @Nonnull GeoList list, int index) {
		GeoElement element = parseInput(input);
		if (isEmptyValue(element) && index >= list.size()) {
			// Do not process empty input at the end of the table
			// And do not add empty element to an already empty list
			return;
		}
		ensureCapacity(list, index);
		list.setListElement(index, element);
		element.notifyUpdate();
	}

	private boolean isEmptyValue(GeoElement element) {
		return element instanceof GeoText && "".equals(((GeoText) element).getTextString());
	}

	private void ensureCapacity(GeoList list, int index) {
		boolean listWillChange = list.size() < index + 1;
		list.ensureCapacity(index + 1);
		for (int i = list.size(); i < index + 1; i++) {
			list.add(createEmptyInput());
		}
		if (listWillChange) {
			list.notifyUpdate();
		}
	}

	private GeoElement parseInput(String input) {
		String trimmedInput = input.trim();
		if (trimmedInput.equals("")) {
			return createEmptyInput();
		}
		try {
			double parsedInput = Double.parseDouble(trimmedInput);
			return new GeoNumeric(cons, parsedInput);
		} catch (NumberFormatException e) {
			return new GeoText(cons, input);
		}
	}

	private GeoElement createEmptyInput() {
		return new GeoText(cons, "");
	}
}
