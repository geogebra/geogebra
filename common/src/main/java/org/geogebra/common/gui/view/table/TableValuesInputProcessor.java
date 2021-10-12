package org.geogebra.common.gui.view.table;

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;

public class TableValuesInputProcessor implements TableValuesProcessor {

	private final Construction cons;
	private final TableValues tableValues;
	private final TableValuesModel model;

	/**
	 * Creates a TableValuesInputProcessor
	 * @param cons construction
	 * @param tableValues Table Values view
	 */
	public TableValuesInputProcessor(Construction cons, TableValues tableValues) {
		this.cons = cons;
		this.tableValues = tableValues;
		this.model = tableValues.getTableValuesModel();
	}

	@Override
	public void processInput(@Nonnull String input, GeoList list, int index) {
		GeoElement element = parseInput(input);
		if (model.isEmptyValue(element) && (list == null || index >= list.size())) {
			// Do not process empty input at the end of the table
			// And do not add empty element to an already empty list
			return;
		}
		model.insert(element, ensureList(list), index);
	}

	private GeoList ensureList(GeoList list) {
		if (list == null) {
			GeoList column = new GeoList(cons);
			column.notifyAdd();
			tableValues.showColumn(column);
			return column;
		}
		return list;
	}

	private GeoElement parseInput(String input) {
		String trimmedInput = input.trim();
		if (trimmedInput.equals("")) {
			return model.createEmptyInput();
		}
		try {
			double parsedInput = Double.parseDouble(trimmedInput);
			return new GeoNumeric(cons, parsedInput);
		} catch (NumberFormatException e) {
			return new GeoText(cons, input);
		}
	}
}
