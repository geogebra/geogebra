package org.geogebra.common.spreadsheet.core;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.CheckForNull;

// TODO testing: This class contains a lot of tricky logic, so it should be directly unit-tested
//  (not just indirectly via SpreadsheetController).
//  If you follow my advice to <a href="https://docs.google.com/presentation/d/1HAcW_FE7oP60l3cmR7FR89CDKX67PuSxg3Jjsvtv02c/edit#slide=id.g15d630f14f3_0_0">go bottom-up"</a>,
//  to design and test building blocks in isolation, and then assemble larger systems
//  from well-tested  building blocks, you cannot fall into this trap.
//
// TODO design: The API is unsymmetric with respect to the boolean return values of some selection-modifying
//  methods. Either we have _all of them_ return a boolean that indicates whether the selection changed, or
//  none - we shouldn't have an arbitrary mix of both. I can imagine that this may be useful information (e.g.
//  during mouse drags), so we should somehow provide this functionality. But I think it will be simpler
//  to provide a "did the selection change" method instead of passing booleans and potentially having
//  to OR them together in client code. I changed the implementation of the `selections()` method to
//  return _a clone_ (snapshot) of the current selection state, so it can be compared later on to find
//  out if the selection changed.
//
// TODO documentation: Even though this is an internal class, the selection model should be explained
//  (why is there a _list_ of selections, negative indicies refer to headers, etc.).
final class SpreadsheetSelectionController {

	// we can make this configurable
	private int numberOfColumnHeaders = 1; // 0 if no column headers
	private int numberOfRowHeaders = 1; // 0 if no row headers
	private final List<Selection> selections = new ArrayList<>();

	void clearSelection() {
		selections.clear();
	 }

	void selectAll(int numberOfRows, int numberOfColumns) {
		 // TODO The numberOfRows - 2 is really surprising.
		//  If the idea is to exclude the header row, why not TabularRange.range(1, numberOfRows - 1, ...)?
		//
		//  Edit: After some digging, I found that header rows are represented by -1 indexes, so that
		//  explains that. But this ignores the case that there may not be a row or column
		// header. Also, the code would be easier to understand if the index calculation included the
		// header row information, e.g.
		setSelection(new Selection(
				TabularRange.range(0, numberOfRows - 1 - numberOfColumnHeaders,
						0, numberOfColumns - 1 - numberOfRowHeaders)));
	}

	/**
	 * @return The current selections.
	 *
	 * @apiNote This method returns a copy of the internal state, so you can use it to snapshot
	 * the current selection state, and compare that against the selection state after some
	 * (potentially selection-modifying) operations.
	 */
	List<Selection> selections() {
		return new ArrayList<>(selections);
	}

	/**
	 * Clears the list of selection and adds a single element to it
	 * @param selection Selection
	 */
	// TODO naming: setSelection (singular; it takes one selection, and the result is also one selection)
	public void setSelection(Selection selection) {
		this.selections.clear();
		this.selections.add(selection);
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
			// TODO the `getLeft()` name should be changed, see my comments in Selection
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

	/**
	 * @param selection {@link Selection}
	 * @param extendSelection Whether we want to extend the current selection (SHIFT)
	 * @param addSelection Whether we want to add this selection to the current selections (CTRL)
	 */
	public void select(Selection selection, boolean extendSelection, boolean addSelection) {
		Selection lastSelection = getLastSelection();
		if (extendSelection && lastSelection != null) {
			extendSelection(lastSelection, selection, addSelection);
			return;
		}
		if (!addSelection) {
			setSelection(selection);
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
		setSelection(extendedSelection);
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

	// TODO naming: What's the meaning of "last selection"?
	// TODO reduce visibility if possible (from public)
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
