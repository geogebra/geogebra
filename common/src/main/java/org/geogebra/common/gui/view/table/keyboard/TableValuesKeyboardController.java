package org.geogebra.common.gui.view.table.keyboard;

import javax.annotation.Nonnull;

import org.geogebra.common.gui.view.table.TableValues;
import org.geogebra.common.gui.view.table.TableValuesModel;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
//import org.geogebra.common.ownership.NonOwning;

import com.google.j2objc.annotations.Weak;

/**
 * @apiNote All row and column indexes are 0-based.
 */
public final class TableValuesKeyboardController {

	public static enum Key {
		ARROW_LEFT, ARROW_RIGHT, ARROW_UP, ARROW_DOWN, RETURN;
	}

//	@NonOwning
	@Weak
	public TableValuesKeyboardControllerDelegate delegate;

	private final @Nonnull TableValues tableValuesView;
	private final @Nonnull TableValuesModel tableValuesModel;

	private int selectedRow = -1;
	private int selectedColumn = -1;

	public TableValuesKeyboardController(@Nonnull TableValues tableValuesView) {
		this.tableValuesView = tableValuesView;
		this.tableValuesModel = tableValuesView.getTableValuesModel();
	}

	/**
	 * @return the selected column index, or -1 if no cell is selected.
	 */
	public int getSelectedRow() {
		return selectedRow;
	}

	/**
	 * @return the selected row index, or -1 if no cell is selected.
	 */
	public int getSelectedColumn() {
		return selectedColumn;
	}

	/**
	 * Selects a cell. If either row or column is outside the valid range, nothing will happen.
	 *
	 * @param row the row index to select.
	 * @param column the column index to select.
	 */
	public void select(int row, int column) {
		if (row < 0 || column < 0
				|| row >= getNavigableRowCount()
				|| column >= getNavigableColumnCount()) {
			return;
		}
		selectedRow = row;
		selectedColumn = column;
	}

	/**
	 * Clears (removes) any selection.
	 */
	public void deselect() {
		selectedRow = -1;
		selectedColumn = -1;
	}

	public void keyPressed(Key key) {
		if (selectedRow < 0 || selectedColumn < 0) {
			return;
		}
		switch (key) {
		case ARROW_LEFT:
			handleArrowLeft();
			break;
		case ARROW_RIGHT:
			handleArrowRight();
			break;
		case ARROW_UP:
			handleArrowUp();
			break;
		case ARROW_DOWN:
			handleArrowDown();
			break;
		case RETURN:
			handleArrowDown();
			break;
		}
	}

	private void handleArrowLeft() {
		if (isFirstColumn(selectedColumn)) {
			return;
		}
		select(selectedRow, findFirstFocusableColumnLeftOf(selectedColumn));
	}

	private void handleArrowRight() {
		if (isLastColumn(selectedColumn) && !tableValuesModel.getIsEditable()) {
			return;
		}
		int column = findFirstFocusableColumnRightOf(selectedColumn);
		if (column == -1 && tableValuesModel.getIsEditable()) {
			extendModelByOneColumn();
			column = getNavigableColumnCount() - 1;
		}
		select(selectedRow, column);
	}

	private void handleArrowUp() {
		if (isFirstRow(selectedRow)) {
			return;
		}
		select(selectedRow - 1, selectedColumn);
	}

	private void handleArrowDown() {
		if (isLastRow(selectedRow)) {
			if (!tableValuesModel.getIsEditable()) {
				return;
			}
//			if (!tableValuesModel.isEmptyValue())
			extendModelByOneRow();
		}
		select(selectedRow + 1, selectedColumn);
	}

	private boolean isFirstRow(int row) {
		return getNavigableRowCount() > 0 ? row == 0 : false;
	}

	private boolean isLastRow(int row) {
		return getNavigableRowCount() > 0 ? row == getNavigableRowCount() - 1 : false;
	}

	private boolean isFirstColumn(int column) {
		return getNavigableColumnCount() > 0 ? column == 0 : false;
	}

	private boolean isLastColumn(int column) {
		return getNavigableColumnCount() > 0 ? column == getNavigableColumnCount() - 1 : false;
	}

	private void extendModelByOneColumn() {

	}

	private void extendModelByOneRow() {

	}

	private int findFirstFocusableColumnLeftOf(int column) {
		for (int index = column - 1; index >= 0; index--) {
			if (isColumnFocusable(index)) {
				return index;
			}
		}
		return -1;
	}

	private int findFirstFocusableColumnRightOf(int column) {
		for (int index = column + 1; index < getNavigableColumnCount(); index++) {
			if (isColumnFocusable(index)) {
				return index;
			}
		}
		return -1;
	}

	private boolean isColumnFocusable(int column) {
		if (column < 0) {
			return false;
		}
		GeoEvaluatable evaluatable = tableValuesView.getEvaluatable(column);
		if (evaluatable == null) {
			return false;
		}
		return !(evaluatable instanceof GeoFunctionable);
	}

	private int getNavigableRowCount() {
		return tableValuesModel.getRowCount() + (tableValuesModel.getIsEditable() ? 1 : 0);
	}

	private int getNavigableColumnCount() {
		return tableValuesModel.getColumnCount() + (tableValuesModel.getIsEditable() ? 1 : 0);
	}
}
