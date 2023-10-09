package org.geogebra.common.spreadsheet.core;

import java.util.ArrayList;
import java.util.List;

final class SpreadsheetSelectionController {
	private final ArrayList<Selection> selections = new ArrayList<>();

	void setDimensions(int numberOfRows, int numberOfColumns) {
		// stub
	}

	 void clearSelection() {
		selections.clear();
	 }

	void selectAll() {
		clearSelection();
		selections.add(new Selection(SelectionType.ALL,
				new TabularRange(-1, -1, -1, -1)));
	}

	List<Selection> selections() {
		return selections;
	}

	void selectRow(int row, boolean extendingCurrentSelection) {
		if (!extendingCurrentSelection) {
			clearSelection();
		}
		selections.add(new Selection(SelectionType.ROWS,
				new TabularRange(row, row, -1, -1)));
	}

	void selectColumn(int column, boolean extendingCurrentSelection) {
		clearSelection();
		selections.add(new Selection(SelectionType.COLUMNS,
				new TabularRange(-1, -1, column, column)));
	}

	void selectCell(int row, int column, boolean extendingCurrentSelection) {
		// stub
	}

	void moveLeft(boolean extendingCurrentSelection) {
		// stub
	}

	void moveRight(boolean extendingCurrentSelection) {
		// stub
	}

	void enter() {
		// stub
	}

	void cancel() {
		// stub
	}

	void addSelectionListener(SpreadsheetSelectionListener listener) {
		// stub
	}

	public void select(Selection selection, boolean extend) {
		if (!extend) {
			selections.clear();
			selections.add(selection);
			return;
		}
		ArrayList<Selection> independent = new ArrayList<>();
		Selection merged = selection;
		for (Selection other: selections) {
			Selection mergeResult = merged.merge(other);
			if (mergeResult == null) {
				independent.add(other);
			} else {
				merged = mergeResult;
			}
		}
		selections.clear();
		selections.addAll(independent);
		selections.add(merged);
	}

	public boolean isSelected(int row, int column) {
		return selections.stream().anyMatch(s -> s.contains(row, column));
	}
}
