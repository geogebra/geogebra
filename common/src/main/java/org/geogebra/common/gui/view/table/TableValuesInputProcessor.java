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
import org.geogebra.common.main.settings.TableSettings;

public class TableValuesInputProcessor implements TableValuesProcessor {

	private final Construction cons;
	private final TableValuesView tableValues;
	private final TableValuesModel model;
	private final TableSettings settings;

	/**
	 * Creates a TableValuesInputProcessor
	 * @param cons construction
	 * @param tableValues Table Values view
	 */
	public TableValuesInputProcessor(
			Construction cons, TableValuesView tableValues, TableSettings settings) {
		this.cons = cons;
		this.tableValues = tableValues;
		this.settings = settings;
		model = tableValues.getTableValuesModel();
	}

	@Override
	public void processInput(@Nonnull String input, GeoList list, int index) {
		GeoElement element = parseInput(input);
		if (isEmptyValue(element) && (list == null || index >= list.size())) {
			// Do not process empty input at the end of the table
			// And do not add empty element to an already empty list
			return;
		}
		model.startBatchUpdate();
		GeoList column = ensureList(list);
		ensureCapacity(column, index);
		column.setListElement(index, element);
		column.setDefinition(null);
		element.notifyUpdate();
		if (isEmptyValue(element)) {
			removeEmptyColumnAndRows(column, index);
		}
		if (list == tableValues.getValues()) {
			settings.setValueList(list);
		}
		model.endBatchUpdate();
		cons.getUndoManager().storeUndoInfo();
	}

	private boolean isEmptyValue(GeoElement element) {
		return element instanceof GeoText && "".equals(((GeoText) element).getTextString());
	}

	private GeoList ensureList(GeoList list) {
		if (list == null) {
			GeoList column = new GeoList(cons);
			column.notifyAdd();
			tableValues.doShowColumn(column);
			return column;
		}
		return list;
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

	private void removeEmptyColumnAndRows(GeoList column, int index) {
		removeColumnIfEmpty(column);
		if (index == column.size() - 1) {
			removeEmptyRowsFromBottom();
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

	private void removeEmptyRowsFromBottom() {
		while (model.getRowCount() > 0 && isLastRowEmpty()) {
			removeLastRow();
		}
	}

	private boolean isLastRowEmpty() {
		int lastRowIndex = model.getRowCount() - 1;
		for (int columnIndex = 0; columnIndex < model.getColumnCount(); columnIndex++) {
			if (!"".equals(model.getCellAt(lastRowIndex, columnIndex).getInput())) {
				return false;
			}
		}
		return true;
	}

	private void removeLastRow() {
		int lastRowIndex = model.getRowCount() - 1;
		List<GeoList> columnsToRemove = new ArrayList<>();
		for (int columnIndex = 0; columnIndex < model.getColumnCount(); columnIndex++) {
			GeoEvaluatable evaluatable = tableValues.getEvaluatable(columnIndex);
			if (evaluatable instanceof GeoList) {
				GeoList column = (GeoList) evaluatable;
				if (lastRowIndex < column.size()) {
					column.remove(lastRowIndex);
				}
				if (columnIndex != 0 && column.size() == 0) {
					columnsToRemove.add(column);
				}
			}
		}
		for (GeoList column : columnsToRemove) {
			column.remove();
		}
	}
}
