package org.geogebra.common.gui.view.table;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;
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
	public void processInput(@Nonnull String input, @Nonnull GeoList list, int index) {
		GeoElement element = parseInput(input);
		if (isEmptyValue(element) && index >= list.size()) {
			// Do not process empty input at the end of the table
			// And do not add empty element to an already empty list
			return;
		}
		ensureCapacity(list, index);
		list.setListElement(index, element);
		if (isEmptyValue(element)) {
			removeEmptyColumnAndRows(list, index);
		}
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

	private boolean hasEmptyValue(int columnIndex, int rowIndex) {
		GeoEvaluatable evaluatable = tableValues.getEvaluatable(columnIndex);
		GeoList column;
		if (evaluatable instanceof GeoList) {
			column = (GeoList) evaluatable;
		} else {
			return false;
		}
		GeoElement value = rowIndex < column.size() ? column.get(rowIndex) : null;
		return value == null || isEmptyValue(value);
	}

	private void removeEmptyColumnAndRows(GeoList column, int index) {
		removeColumnIfEmpty(column);

		if (index == column.size() - 1) {
			while (tableValues.getTableValuesModel().getRowCount() > 0 && isLastRowEmpty()) {
				removeLastRow();
			}
		}
	}

	private void removeColumnIfEmpty(GeoList column) {
		if (column == tableValues.getValues()) {
			return;
		}
		for (int i = 0; i < column.size(); i++) {
			GeoElement element = column.get(i);
			if (!isEmptyValue(element)) {
				return;
			}
		}
		column.remove();
	}

	private boolean isLastRowEmpty() {
		TableValuesModel model = tableValues.getTableValuesModel();
		int lastRowIndex = model.getRowCount() - 1;
		for (int columnIndex = 0; columnIndex < model.getColumnCount(); columnIndex++) {
			if (!hasEmptyValue(columnIndex, lastRowIndex)) {
				return false;
			}
		}
		return true;
	}

	private void removeLastRow() {
		TableValuesModel model = tableValues.getTableValuesModel();
		int lastRowIndex = model.getRowCount() - 1;
		List<GeoList> columnsToRemove = new ArrayList<>();
		for (int columnIndex = 0; columnIndex < model.getColumnCount(); columnIndex++) {
			GeoList column = (GeoList) tableValues.getEvaluatable(columnIndex);
			if (lastRowIndex < column.size()) {
				column.remove(lastRowIndex);
			}
			if (columnIndex != 0 && column.size() == 0) {
				columnsToRemove.add(column);
			}
		}
		for (GeoList column : columnsToRemove) {
			column.remove();
		}
	}

	private GeoElement createEmptyInput() {
		return new GeoText(cons, "");
	}
}
