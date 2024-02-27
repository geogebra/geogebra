package org.geogebra.common.spreadsheet.core;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.CheckForNull;

// TODO this should be directly unit-tested (not just indirectly via SpreadsheetController)
final class SpreadsheetSelectionController {
	private final ArrayList<Selection> selections = new ArrayList<>();

//	void setDimensions(int numberOfRows, int numberOfColumns) {
//		// stub
//	}

	 void clearSelection() {
		selections.clear();
	 }

	void selectAll(int numberOfRows, int numberOfColumns) {
		setSelections(new Selection(
				TabularRange.range(-1, numberOfRows - 2, -1, numberOfColumns - 2)));
	}

	List<Selection> selections() {
		return selections;
	}

	/**
	 * Clears the list of selection and adds a single element to it
	 * @param selection Selection
	 * @return whether selection changed
	 */
	public boolean setSelections(Selection selection) {
		if (selections.size() == 1
				&& selections.get(0).getRange().isEqualCells(selection.getRange())) {
			return false;
		}
		this.selections.clear();
		this.selections.add(selection);
		return true;
	}

	/**
	 * Selects a row with given index
	 * @param rowIndex Index
	 * @param extendSelection Whether we want to extend the current selection
	 * @param addSelection Whether we want to add it to the current selections
	 */
	void selectRow(int rowIndex,
			boolean extendSelection, boolean addSelection) {
		Selection row = new Selection(
				TabularRange.range(rowIndex, rowIndex, -1, -1));
		select(row, extendSelection, addSelection);
	}

	/**
	 * Selects a column with given index
	 * @param columnIndex Index
	 * @param extendSelection Whether we want to extend the current selection
	 * @param addSelection Whether we want to add it to the current selections
	 */
	void selectColumn(int columnIndex,
			boolean extendSelection, boolean addSelection) {
		Selection column = new Selection(
				TabularRange.range(-1, -1, columnIndex, columnIndex));
		select(column, extendSelection, addSelection);
	}

	void selectCell(int rowIndex, int columnIndex, boolean extendSelection, boolean addSelection) {
		Selection selection = Selection.getSingleCellSelection(rowIndex, columnIndex);
		select(selection, extendSelection, addSelection);
	}

	/**
	 * @param extendSelection True if the current selection should expand, false else
	 */
	void moveLeft(boolean extendSelection) {
		Selection lastSelection = getLastSelection();
		if (lastSelection != null) {
			select(lastSelection.getLeft(extendSelection), extendSelection, false);
		}
	}

	/**
	 * @param extendSelection True if the current selection should expand, false else
	 * @param numberOfColumns Number of columns in the table
	 */
	void moveRight(boolean extendSelection, int numberOfColumns) {
		Selection lastSelection = getLastSelection();
		if (lastSelection != null) {
			select(lastSelection.getRight(numberOfColumns, extendSelection),
					extendSelection, false);
		}
	}

	/**
	 * @param extendSelection True if the current selection should expand, false else
	 */
	void moveUp(boolean extendSelection) {
		Selection lastSelection = getLastSelection();
		if (lastSelection != null) {
			select(lastSelection.getTop(extendSelection), extendSelection, false);
		}
	}

	/**
	 * @param extendSelection True if the current selection should expand, false else
	 * @param numberOfRows Number of rows
	 */
	void moveDown(boolean extendSelection, int numberOfRows) {
		Selection lastSelection = getLastSelection();
		if (lastSelection != null) {
			select(lastSelection.getBottom(numberOfRows, extendSelection),
					extendSelection, false);
		}
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

	/**
	 * @param selection {@link Selection}
	 * @param extendSelection Whether we want to extend the current selection (SHIFT)
	 * @param addSelection Whether we want to add this selection to the current selections (CTRL)
	 */
	public boolean select(Selection selection, boolean extendSelection, boolean addSelection) {
		Selection lastSelection = getLastSelection();
		if (extendSelection && lastSelection != null) {
			extendSelection(lastSelection, selection, addSelection);
			return true;
		} else if (!addSelection) {
			return setSelections(selection);
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
		return true;
	}

	/**
	 * Extends a selection with another selection
	 * @param current Current Selection
	 * @param other Selection used to extend the current selection
	 * @param addSelection Whether we want to add this selection to the current selections
	 */
	private void extendSelection(Selection current, Selection other, boolean addSelection) {
		Selection extendedSelection = current.getExtendedSelection(other);
		this.selections.remove(current);
		if (addSelection) {
			this.selections.add(extendedSelection);
			return;
		}
		setSelections(extendedSelection);
	}

	public boolean isSelected(int row, int column) {
		return selections.stream().anyMatch(s -> s.contains(row, column));
	}

	/**
	 * @return True if there is currently at least one cell selected, false else
	 */
	public boolean hasSelection() {
		return !selections.isEmpty();
	}

	/**
	 * @return Last Selection if present, null otherwise
	 */
	public @CheckForNull Selection getLastSelection() {
		return selections.isEmpty() ? null : selections.get(selections.size() - 1);
	}

	public boolean isOnlyCellSelected(int row, int column) {
		return selections.size() == 1 && selections.get(0).getRange().isSingleCell()
				&& isSelected(row, column);
	}
}
