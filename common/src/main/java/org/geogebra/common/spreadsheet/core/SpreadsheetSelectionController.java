package org.geogebra.common.spreadsheet.core;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;

import javax.annotation.CheckForNull;

final class SpreadsheetSelectionController {
	private final ArrayList<Selection> selections = new ArrayList<>();

	void setDimensions(int numberOfRows, int numberOfColumns) {
		// stub
	}

	 void clearSelections() {
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
		clearSelections();
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
		clearSelections();
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

	/**
	 * @param row Row index
	 * @param column Column index
	 * @return Whether there is only a single cell selected
	 */
	public boolean isOnlyCellSelected(int row, int column) {
		return selections.size() == 1 && selections.get(0).getRange().isSingleCell()
				&& isSelected(row, column);
	}

	/**
	 * @param row Row index
	 * @return Whether there is only a single row selected
	 */
	boolean isOnlyRowSelected(int row) {
		return selections.size() == 1 && selections.get(0).getRange().isSingleRow()
				&& isSelected(row, -1);
	}

	/**
	 * @return Whether currently only rows are selected
	 */
	boolean areOnlyRowsSelected() {
		return selections.stream().allMatch(selection -> selection.getRange().isRow());
	}

	/**
	 * @param column Column Index
	 * @return Whether there is only a single column selected
	 */
	boolean isOnlyColumnSelected(int column) {
		return selections.size() == 1 && selections.get(0).getRange().isSingleColumn()
				&& isSelected(-1, column);
	}

	/**
	 * @return Whether currently only columns are selected
	 */
	boolean areOnlyColumnsSelected() {
		return selections.stream().allMatch(selection -> selection.getRange().isColumn());
	}

	/**
	 * @return True if only cells are selected (i.e. no <b>whole</b> rows or columns)
	 */
	boolean areOnlyCellsSelected() {
		return selections.stream().allMatch(
				selection -> !selection.getRange().isRow() && !selection.getRange().isColumn());
	}

	/**
	 * @return Whether there is at least one selection that is of type {@link SelectionType#ALL}
	 */
	boolean areAllCellsSelected() {
		return selections.size() > 0 && selections.stream().anyMatch(
				selection -> selection.getType() == SelectionType.ALL);
	}

	/**
	 * @return The row indexes of all selections without duplicates
	 */
	List<Integer> getAllRowIndexes() {
		return getAllIndexesWithoutDuplicates(selection -> selection.getRange().getMinRow(),
				selection -> selection.getRange().getMaxRow());
	}

	/**
	 * @return The column indexes of all selections without duplicates
	 */
	List<Integer> getAllColumnIndexes() {
		return getAllIndexesWithoutDuplicates(selection -> selection.getRange().getMinColumn(),
				selection -> selection.getRange().getMaxColumn());
	}

	/**
	 * @param getMinIndex Function that accepts an instance of {@link Selection} and returns
	 * some wanted minimum index (e.g. {@link TabularRange#getMinRow()}
	 * @param getMaxIndex Function that accepts an instance of {@link Selection} and returns
	 * some wanted maximum index (e.g. {@link TabularRange#getMaxRow()}
	 * @return All indexes from all current selections without duplicates
	 */
	List<Integer> getAllIndexesWithoutDuplicates(Function<Selection, Integer> getMinIndex,
			Function<Selection, Integer> getMaxIndex) {
		List<Integer> indexes = new ArrayList<>();
		for (Selection selection : selections) {
			int index = getMinIndex.apply(selection);
			while (index <= getMaxIndex.apply(selection)) {
				if (!indexes.contains(index)) {
					indexes.add(index);
				}
				index++;
			}
		}
		return indexes;
	}

	/**
	 * @return The lowest row index from all current selections
	 */
	int getUppermostSelectedRowIndex() {
		return getExtremeIndexFor(selection -> selection.getRange().getMinRow(),
				(fromRowIndex, otherFromRowIndex) -> fromRowIndex > otherFromRowIndex);
	}

	/**
	 * @return The highest row index from all current selections
	 */
	int getBottommostSelectedRowIndex() {
		return getExtremeIndexFor(selection -> selection.getRange().getMaxRow(),
				(toRowIndex, otherToRowIndex) -> toRowIndex < otherToRowIndex);
	}

	/**
	 * @return The lowest column index from all current selections
	 */
	int getLeftmostSelectedColumnIndex() {
		return getExtremeIndexFor(selection -> selection.getRange().getMinColumn(),
				(fromColumnIndex, otherFromColumnIndex) -> fromColumnIndex > otherFromColumnIndex);
	}

	/**
	 * @return The highest column index from all current selections
	 */
	int getRightmostSelectedColumnIndex() {
		return getExtremeIndexFor(selection -> selection.getRange().getMaxColumn(),
				(toColumnIndex, otherToColumnIndex) -> toColumnIndex < otherToColumnIndex);
	}

	/**
	 * @param getIndex Function that accepts an instance of {@link Selection} and returns some
	 * wanted index (e.g. {@link TabularRange#getMinRow()})
	 * @param swapIndex Predicate that accepts two arguments of type Integer, where the first one
	 * is the currently stored index that is to be returned - returns true if the index that is to
	 * be returned should be swapped with the result of the getIndex Function, false else
	 * @return Needed extreme index
	 */
	private int getExtremeIndexFor(Function<Selection, Integer> getIndex,
			BiPredicate<Integer, Integer> swapIndex) {
		if (selections.isEmpty()) {
			return -1;
		}
		int index = getIndex.apply(selections.get(0));
		for (Selection selection : selections) {
			if (swapIndex.test(index, getIndex.apply(selection))) {
				index = getIndex.apply(selection);
			}
		}
		return index;
	}
}
