package org.geogebra.common.gui.view.table;

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;

public class TableValuesInputProcessor implements TableValuesProcessor {

	private final Construction cons;
	private final TableValues tableValues;

	/**
	 * Creates a TableValuesInputProcessor
	 * @param cons construction
	 * @param tableValues Table Values view
	 */
	public TableValuesInputProcessor(Construction cons, TableValues tableValues) {
		this.cons = cons;
		this.tableValues = tableValues;
	}

	@Override
	public void processInput(@Nonnull String input, GeoList list, int index)
			throws InvalidInputException {
		GeoNumeric numeric = parseInput(input);
		if (isEmptyValue(numeric) && (list == null || index >= list.size())) {
			// Do not process empty input at the end of the table
			return;
		}
		GeoList column = ensureList(list);
		ensureCapacity(column, index);
		column.setListElement(index, numeric);
		if (isEmptyValue(numeric) && index == list.size() - 1) {
			if (list.size() == 1) {
				column.remove();
			}
			while (tableValues.getTableValuesModel().getRowCount() > 0 && isLastRowEmpty()) {
				removeLastRow();
			}
		}
		numeric.notifyUpdate();
	}

	private boolean isEmptyValue(GeoNumeric numeric) {
		return Double.isNaN(numeric.getDouble());
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

	private void ensureCapacity(GeoList list, int index) {
		boolean listWillChange = list.size() < index + 1;
		list.ensureCapacity(index + 1);
		for (int i = list.size(); i < index + 1; i++) {
			list.add(new GeoNumeric(cons, Double.NaN));
		}
		if (listWillChange) {
			list.notifyUpdate();
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

	private boolean isLastRowEmpty() {
		TableValuesModel model = tableValues.getTableValuesModel();
		int lastRowIndex = model.getRowCount() - 1;
		for (int columnIndex = 1; columnIndex < model.getColumnCount(); columnIndex++) {
			if (!isEmptyValue(columnIndex, lastRowIndex)) {
				return false;
			}
		}
		return true;
	}

	private boolean isEmptyValue(int columnIndex, int rowIndex) {
		GeoEvaluatable evaluatable = tableValues.getEvaluatable(columnIndex);
		GeoList column;
		if (evaluatable instanceof GeoList) {
			column = (GeoList) evaluatable;
		} else {
			return false;
		}
		GeoElement value = column.get(rowIndex);
		return (value == null || (value instanceof GeoNumeric && isEmptyValue((GeoNumeric) value)));
	}

	private void removeLastRow() {
		TableValuesModel model = tableValues.getTableValuesModel();
		int lastRowIndex = model.getRowCount() - 1;
		for (int columnIndex = 1; columnIndex < model.getColumnCount(); columnIndex++) {
			GeoList column = (GeoList) tableValues.getEvaluatable(columnIndex);
			column.remove(lastRowIndex);
			if (column.size() == 0) {
				column.remove();
			}
		}
	}
}
