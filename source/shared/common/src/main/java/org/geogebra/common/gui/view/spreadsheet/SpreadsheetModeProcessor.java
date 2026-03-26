/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.gui.view.spreadsheet;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementSpreadsheet;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.App;
import org.geogebra.common.main.SpreadsheetTableModel;
import org.geogebra.common.spreadsheet.core.SelectionType;
import org.geogebra.common.spreadsheet.core.SpreadsheetCoords;
import org.geogebra.common.spreadsheet.core.TabularRange;
import org.geogebra.common.util.debug.Log;

public class SpreadsheetModeProcessor {

	private final App app;
	private final SpreadsheetTableModel tableModel;
	private final Kernel kernel;
	private final @CheckForNull MyTable table;
	private GeoElement targetCell;

	/**
	 * @param app
	 *            application
	 * @param table
	 *            table
	 */
	public SpreadsheetModeProcessor(@Nonnull App app, @CheckForNull MyTable table) {
		this.app = app;
		this.tableModel = app.getSpreadsheetTableModel();
		this.kernel = app.getKernel();
		this.table = table;
	}

	/**
	 * Creates autofunction cells based on the given cell range and the current
	 * autofunction mode.
	 *
	 * @param range tabular range
	 * @param shiftDown whether Shift key is pressed
	 */
	public void performAutoFunctionCreation(TabularRange range, boolean shiftDown) {
		if (range.isContiguousColumns() || range.isContiguousRows()) {
			return;
		}

		boolean isOK = true;
		TabularRange targetRange;

		// Case 1: Partial row, targetCell created beneath the column
		if (range.isPartialRow() || (!range.isPartialColumn() && shiftDown)) {

			int maxColumn = getMaxUsedColumn(range) + 1;
			targetRange = new TabularRange(range.getMinRow(), maxColumn,
					range.getMaxRow(), maxColumn);
			for (int row = range.getMinRow(); row <= range.getMaxRow(); row++) {

				// try to clear the target cell, exit if this is not possible
				if (RelativeCopy.getValue(tableModel, maxColumn, row) != null) {
					isOK = delete(maxColumn, row, maxColumn, row);
				}
				// create new targetCell
				if (isOK) {
					GeoElement targetCell = new GeoNumeric(kernel.getConstruction(), 0);
					targetCell.setLabel(GeoElementSpreadsheet
							.getSpreadsheetCellName(maxColumn, row));
					createAutoFunctionCell(targetCell, new TabularRange(
							row, range.getMinColumn(), row, maxColumn - 1));
				}
			}

			app.setMoveMode();
			if (table != null) {
				table.setSelection(targetRange);
				table.repaint();
			}
		} else {
			int maxRow = getMaxUsedRow(range) + 1;
			targetRange = new TabularRange(maxRow, range.getMinColumn(),
					maxRow, range.getMaxColumn());
			for (int col = range.getMinColumn(); col <= range.getMaxColumn(); col++) {

				// try to clear the target cell, exit if this is not possible
				if (RelativeCopy.getValue(tableModel, col, maxRow) != null) {
					isOK = delete(col, maxRow, col, maxRow);
				}
				// create new targetCell
				if (isOK) {
					String cellName = GeoElementSpreadsheet
							.getSpreadsheetCellName(col, maxRow);
					GeoElement cell = kernel.lookupLabel(cellName);
					GeoElement targetCell;
					if (cell == null) {
						targetCell = new GeoNumeric(kernel.getConstruction(), 0);
						targetCell.setLabel(cellName);
					} else {
						targetCell = cell;
					}
					createAutoFunctionCell(targetCell, new TabularRange(range.getMinRow(), col,
							maxRow - 1, col));
				}
			}

			app.setMoveMode();
			if (table != null) {
				table.setSelection(targetRange);
				table.repaint();
			}
		}
	}

	private int getMaxUsedColumn(TabularRange range) {
		if (range.isContiguousRows() || range.isContiguousColumns()) {
			return range.getMaxColumn();
		}

		for (int row = range.getMinRow(); row <= range.getMaxRow(); row++) {
			if (kernel.getGeoAt(range.getMaxColumn(), row) != null) {
				return range.getMaxColumn();
			}
		}

		return range.getMaxColumn() - 1;
	}

	private int getMaxUsedRow(TabularRange range) {

		if (range.isContiguousRows() || range.isContiguousColumns()) {
			return range.getMaxRow();
		}

		for (int col = range.getMinColumn(); col <= range.getMaxColumn(); col++) {
			if (kernel.getGeoAt(col, range.getMaxRow()) != null) {
				return range.getMaxRow();
			}
		}

		return range.getMaxRow() - 1;
	}

	/**
	 * Creates an autofunction in the given target cell based on the current
	 * autofunction mode and the given cell range.
	 * 
	 * @param functionTargetCell
	 *            target cell
	 * @param range
	 *            input cell range
	 * @return success
	 */
	public boolean createAutoFunctionCell(GeoElement functionTargetCell, TabularRange range) {

		boolean success = true;

		// Get the targetCell label and the selected cell range
		String targetCellLabel = functionTargetCell.getLabelSimple();
		String cellRangeString = CellRangeUtil.getCellRangeString(range, true,
				app.getLocalization());

		// Create a String expression for the new autofunction command geo
		String cmd = cmdForMode();

		String expr = targetCellLabel + " = " + cmd + "[" + cellRangeString
				+ "]";
		Log.debug(expr);
		// Create the new geo
		if (!range.contains(functionTargetCell.getSpreadsheetCoords())) {
			kernel.getAlgebraProcessor().processAlgebraCommandNoExceptions(expr,
					false);
		} else {
			functionTargetCell.setUndefined();
			success = false;
		}

		return success;
	}

	private String cmdForMode() {
		int mode = app.getMode();
		return switch (mode) {
			case EuclidianConstants.MODE_SPREADSHEET_SUM -> "Sum";
			case EuclidianConstants.MODE_SPREADSHEET_COUNT -> "Length";
			case EuclidianConstants.MODE_SPREADSHEET_AVERAGE -> "Mean";
			case EuclidianConstants.MODE_SPREADSHEET_MAX -> "Max";
			case EuclidianConstants.MODE_SPREADSHEET_MIN -> "Min";
			default -> null;
		};
	}

	/**
	 * Stops the autofunction from updating and creates a new geo for the target
	 * cell based on the current autofunction mode.
	 */
	public void stopAutoFunction() {
		if (table == null) {
			return;
		}
		table.setTableMode(MyTable.TABLE_MODE_STANDARD);

		TabularRange firstSelection = table.getFirstSelection();
		if (firstSelection != null && createAutoFunctionCell(targetCell,
				firstSelection)) {
			// select the new geo
			app.setMoveMode();
			SpreadsheetCoords coords = targetCell.getSpreadsheetCoords();
			table.changeSelection(coords.row, coords.column, false);
			table.repaint();
		}
	}

	/**
	 * Set target cell to a new number at given coords
	 * 
	 * @param column
	 *            column
	 * @param row
	 *            row
	 */
	public void initTargetCell(int column, int row) {
		targetCell = new GeoNumeric(kernel.getConstruction(), 0);
		targetCell.setLabel(GeoElementSpreadsheet
				.getSpreadsheetCellName(column, row));
		targetCell.setUndefined();

	}

	/**
	 * Updates the autofunction by recalculating the autofunction value as the
	 * user drags the mouse to create a selection. The current autofunction
	 * value is displayed in the targetCell.
	 */
	public void updateAutoFunction() {
		if (table == null) {
			return;
		}
		TabularRange selection = table.getFirstSelection();
		if (targetCell == null || selection == null || CellRangeUtil.isEmpty(selection, tableModel)
				|| table.getTableMode() != MyTable.TABLE_MODE_AUTOFUNCTION) {
			app.setMoveMode();
			return;
		}

		// Get a string representation of the selected range (e.g. A1:B3)
		String cellRangeString = CellRangeUtil
				.getCellRangeString(selection, true, app.getLocalization());

		// Build a String expression for the autofunction
		String cmd = cmdForMode();

		String expr = cmd + "[" + cellRangeString + "]";

		// Evaluate the autofunction and put the result in targetCell
		if (!selection.contains(targetCell.getSpreadsheetCoords())) {
			((GeoNumeric) targetCell).setValue(
					kernel.getAlgebraProcessor().evaluateToDouble(expr));
		} else {
			targetCell.setUndefined();
		}
	}

	private boolean delete(int col1, int row1, int col2, int row2) {
		return CopyPasteCut.delete(app, col1, row1, col2, row2,
				table == null ? SelectionType.CELLS : table.getSelectionType());
	}
}
