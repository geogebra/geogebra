package org.geogebra.common.gui.view.spreadsheet;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementSpreadsheet;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.App;
import org.geogebra.common.spreadsheet.core.SpreadsheetCoords;
import org.geogebra.common.spreadsheet.core.TabularRange;
import org.geogebra.common.util.debug.Log;

public class SpreadsheetModeProcessor {

	private App app;
	private Kernel kernel;
	private CopyPasteCut copyPasteCut;
	private MyTable table;
	private GeoElement targetCell;

	/**
	 * @param app
	 *            application
	 * @param table
	 *            table
	 */
	public SpreadsheetModeProcessor(App app, MyTable table) {
		this.app = app;
		this.kernel = app.getKernel();
		this.copyPasteCut = table.getCopyPasteCut();
		this.table = table;
	}

	/**
	 * Creates autofunction cells based on the given cell range and the current
	 * autofunction mode.
	 */
	public void performAutoFunctionCreation(TabularRange cr, boolean shiftDown) {

		if (cr.isColumn() || cr.isRow()) {
			return;
		}

		boolean isOK = true;
		GeoElement targetCell1 = null;
		TabularRange targetRange;

		// Case 1: Partial row, targetCell created beneath the column
		if (cr.isPartialRow() || (!cr.isPartialColumn() && shiftDown)) {

			int maxColumn = getMaxUsedColumn(cr) + 1;
			targetRange = new TabularRange(cr.getMinRow(), maxColumn,
					cr.getMaxRow(), maxColumn);
			for (int row = cr.getMinRow(); row <= cr.getMaxRow(); row++) {

				// try to clear the target cell, exit if this is not possible
				if (RelativeCopy.getValue(app, maxColumn, row) != null) {
					isOK = copyPasteCut.delete(maxColumn, row, maxColumn, row);
				}
				// create new targetCell
				if (isOK) {
					targetCell1 = new GeoNumeric(kernel.getConstruction(), 0);
					targetCell1.setLabel(GeoElementSpreadsheet
							.getSpreadsheetCellName(maxColumn, row));
					createAutoFunctionCell(targetCell1, new TabularRange(
							row, cr.getMinColumn(), row, maxColumn - 1));
				}
			}

			app.setMoveMode();
			table.setSelection(targetRange);
			table.repaint();
		} else {
			int maxRow = getMaxUsedRow(cr) + 1;
			targetRange = new TabularRange(maxRow, cr.getMinColumn(),
					maxRow, cr.getMaxColumn());
			for (int col = cr.getMinColumn(); col <= cr.getMaxColumn(); col++) {

				// try to clear the target cell, exit if this is not possible
				if (RelativeCopy.getValue(app, col, maxRow) != null) {
					isOK = copyPasteCut.delete(col, maxRow, col, maxRow);
				}
				// create new targetCell
				if (isOK) {
					String cellName = GeoElementSpreadsheet
							.getSpreadsheetCellName(col, maxRow);
					GeoElement cell = kernel.lookupLabel(cellName);
					if (cell == null) {
						targetCell1 = new GeoNumeric(kernel.getConstruction(),
								0);
						targetCell1.setLabel(cellName);
					} else {
						targetCell1 = cell;
					}
					createAutoFunctionCell(targetCell1, new TabularRange(cr.getMinRow(), col,
							maxRow - 1, col));
				}
			}

			app.setMoveMode();
			table.setSelection(targetRange);
			table.repaint();
		}
	}

	private int getMaxUsedColumn(TabularRange cr) {

		if (cr.isRow() || cr.isColumn()) {
			return cr.getMaxColumn();
		}

		for (int row = cr.getMinRow(); row <= cr.getMaxRow(); row++) {
			if (kernel.getGeoAt(cr.getMaxColumn(), row) != null) {
				return cr.getMaxColumn();
			}
		}

		return cr.getMaxColumn() - 1;
	}

	private int getMaxUsedRow(TabularRange cr) {

		if (cr.isRow() || cr.isColumn()) {
			return cr.getMaxRow();
		}

		for (int col = cr.getMinColumn(); col <= cr.getMaxColumn(); col++) {
			if (kernel.getGeoAt(col, cr.getMaxRow()) != null) {
				return cr.getMaxRow();
			}
		}

		return cr.getMaxRow() - 1;
	}

	/**
	 * Creates an autofunction in the given target cell based on the current
	 * autofunction mode and the given cell range.
	 * 
	 * @param functionTargetCell
	 *            target cell
	 * @param cr
	 *            input cell range
	 * @return success
	 */
	public boolean createAutoFunctionCell(GeoElement functionTargetCell, TabularRange cr) {

		boolean success = true;

		// Get the targetCell label and the selected cell range
		String targetCellLabel = functionTargetCell.getLabelSimple();
		String cellRangeString = table.getCellRangeProcessor()
				.getCellRangeString(cr);

		// Create a String expression for the new autofunction command geo
		String cmd = null;
		if (app.getMode() == EuclidianConstants.MODE_SPREADSHEET_SUM) {
			cmd = "Sum";
		} else if (app.getMode() == EuclidianConstants.MODE_SPREADSHEET_COUNT) {
			cmd = "Length";
		} else if (app.getMode() == EuclidianConstants.MODE_SPREADSHEET_AVERAGE) {
			cmd = "Mean";
		} else if (app.getMode() == EuclidianConstants.MODE_SPREADSHEET_MAX) {
			cmd = "Max";
		} else if (app.getMode() == EuclidianConstants.MODE_SPREADSHEET_MIN) {
			cmd = "Min";
		}

		String expr = targetCellLabel + " = " + cmd + "[" + cellRangeString
				+ "]";
		Log.debug(expr);
		// Create the new geo
		if (!cr.contains(functionTargetCell.getSpreadsheetCoords())) {
			kernel.getAlgebraProcessor().processAlgebraCommandNoExceptions(expr,
					false);
		} else {
			functionTargetCell.setUndefined();
			success = false;
		}

		return success;
	}

	/**
	 * Stops the autofunction from updating and creates a new geo for the target
	 * cell based on the current autofunction mode.
	 */
	public void stopAutoFunction() {

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

		TabularRange selection = table.getFirstSelection();
		if (targetCell == null || CellRangeUtil.isEmpty(selection, app)
				|| table.getTableMode() != MyTable.TABLE_MODE_AUTOFUNCTION) {
			app.setMoveMode();
			return;
		}

		// Get a string representation of the seleced range (e.g. A1:B3)
		String cellRangeString = table.getCellRangeProcessor()
				.getCellRangeString(selection);

		// Build a String expression for the autofunction
		String cmd = null;
		if (app.getMode() == EuclidianConstants.MODE_SPREADSHEET_SUM) {
			cmd = "Sum";
		} else if (app.getMode() == EuclidianConstants.MODE_SPREADSHEET_COUNT) {
			cmd = "Length";
		} else if (app.getMode() == EuclidianConstants.MODE_SPREADSHEET_AVERAGE) {
			cmd = "Mean";
		} else if (app.getMode() == EuclidianConstants.MODE_SPREADSHEET_MAX) {
			cmd = "Max";
		} else if (app.getMode() == EuclidianConstants.MODE_SPREADSHEET_MIN) {
			cmd = "Min";
		}

		String expr = cmd + "[" + cellRangeString + "]";

		// Evaluate the autofunction and put the result in targetCell
		if (!selection.contains(targetCell.getSpreadsheetCoords())) {
			((GeoNumeric) targetCell).setValue(
					kernel.getAlgebraProcessor().evaluateToDouble(expr));
		} else {
			((GeoNumeric) targetCell).setUndefined();
		}
	}
}
