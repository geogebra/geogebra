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
		setSelections(new Selection(SelectionType.ALL,
				new TabularRange(-1, -1, -1, -1)));
	}

	List<Selection> selections() {
		return selections;
	}

	/**
	 * Clears the list of selection and adds a single element to it
	 * @param selection Selection
	 */
	public void setSelections(Selection selection) {
		this.selections.clear();
		this.selections.add(selection);
	}

	void selectRow(int row, boolean extendingCurrentSelection) {
		setSelections(new Selection(SelectionType.ROWS,
				new TabularRange(row, row, -1, -1)));
	}

	void selectColumn(int column, boolean extendingCurrentSelection) {
		setSelections(new Selection(SelectionType.COLUMNS,
				new TabularRange(-1, -1, column, column)));
	}

	void selectCell(int row, int column, boolean extendingCurrentSelection) {
		// stub
	}

	/**
	 * @param extendingCurrentSelection True if the current selection should expand, false else
	 */
	void moveLeft(boolean extendingCurrentSelection) {
		if (getLastSelection() != null) {
			select(getLastSelection().getLeft(), extendingCurrentSelection, false);
		}
	}

	/**
	 * @param extendingCurrentSelection True if the current selection should expand, false else
	 * @param numberOfColumns Number of rows
	 */
	void moveRight(boolean extendingCurrentSelection, int numberOfColumns) {
		if (getLastSelection() != null) {
			select(getLastSelection().getRight(numberOfColumns), extendingCurrentSelection, false);
		}
	}

	/**
	 * @param extendingCurrentSelection True if the current selection should expand, false else
	 */
	void moveUp(boolean extendingCurrentSelection) {
		if (getLastSelection() != null) {
			select(getLastSelection().getTop(), extendingCurrentSelection, false);
		}
	}

	/**
	 * @param extendingCurrentSelection True if the current selection should expand, false else
	 * @param numberOfRows Number of rows
	 */
	void moveDown(boolean extendingCurrentSelection, int numberOfRows) {
		if (getLastSelection() != null) {
			select(getLastSelection().getBottom(numberOfRows), extendingCurrentSelection, false);
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
	 * @param extend Whether we want to extend the current selection (SHIFT)
	 * @param addSelection Whether we want to add this selection to the current selections (CTRL)
	 */
	public void select(Selection selection, boolean extend, boolean addSelection) {
		if (extend && getLastSelection() != null) {
			extendSelection(getLastSelection(), selection, addSelection);
			return;
		} else if (!addSelection) {
			setSelections(selection);
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
	private Selection getLastSelection() {
		return selections.isEmpty() ? null : selections.get(selections.size() - 1);
	}
}
