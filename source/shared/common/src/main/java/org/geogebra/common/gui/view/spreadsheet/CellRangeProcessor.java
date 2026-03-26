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

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementSpreadsheet;
import org.geogebra.common.main.SpreadsheetTableModel;
import org.geogebra.common.spreadsheet.core.Direction;

/**
 * 
 * Utility class with methods for processing cell ranges (e.g inserting rows,
 * creating lists of cells). Typical usage is via the instance of this class
 * created by the constructor of MyTable.
 * 
 * @author G. Sturr
 * 
 */
public class CellRangeProcessor {

	private final MyTable table;
	private final SpreadsheetTableModel tableModel;

	/**
	 * @param table
	 *            table
	 * @param tableModel
	 *            table model
	 */
	public CellRangeProcessor(MyTable table, SpreadsheetTableModel tableModel) {
		this.table = table;
		this.tableModel = tableModel;
	}

	/**
	 * @param column1
	 *            minimum selected column
	 * @param column2
	 *            maximum selected column
	 * @param insertLeft
	 *            true = insert left of column1, false = insert right of column2
	 * @return whether a modification happened
	 */
	public boolean insertColumn(int column1, int column2, boolean insertLeft) {
		boolean modified;
		if (insertLeft) {
			modified = shiftColumnsRight(column1);
			table.getCellFormatHandler().shiftFormats(column1, 1,
					Direction.Right);
		} else {
			modified = shiftColumnsRight(column2 + 1);
			table.getCellFormatHandler().shiftFormats(column2 + 1, 1,
					Direction.Right);
		}
		table.repaint();
		return modified;
	}

	/**
	 * @param column1
	 *            minimum selected column
	 * @param column2
	 *            maximum selected column
	 * @return whether a modification happened
	 */
	public boolean deleteColumns(int column1, int column2) {
		table.getCopyPasteCut().delete(column1, 0, column2,
				tableModel.getHighestUsedRow());
		boolean modified = shiftColumnsLeft(column2 + 1, column2 - column1 + 1);
		table.getCellFormatHandler().shiftFormats(column2 + 1,
				column2 - column1 + 1, Direction.Left);
		table.repaint();
		return modified;
	}

	private boolean shiftColumnsRight(int startColumn) {
		int maxColumn = tableModel.getHighestUsedColumn();
		int maxRow = tableModel.getHighestUsedRow();
		boolean modified = false;
		for (int column = maxColumn; column >= startColumn; --column) {
			for (int row = 0; row <= maxRow; ++row) {
				GeoElement geo = RelativeCopy.getValue(tableModel, column, row);
				if (geo == null) {
					continue;
				}

				String newLabel = GeoElementSpreadsheet
						.getSpreadsheetCellName(column + 1, row);
				geo.setLabel(newLabel);
				modified = true;
			}
		}

		return modified;
	}

	private boolean shiftColumnsLeft(int startColumn, int shiftAmount) {
		int maxColumn = tableModel.getHighestUsedColumn();
		int maxRow = tableModel.getHighestUsedRow();
		boolean modified = false;
		for (int column = startColumn; column <= maxColumn; ++column) {
			for (int row = 0; row <= maxRow; ++row) {
				GeoElement geo = RelativeCopy.getValue(tableModel, column, row);
				if (geo == null) {
					continue;
				}

				String newLabel = GeoElementSpreadsheet
						.getSpreadsheetCellName(column - shiftAmount, row);
				geo.setLabel(newLabel);
				modified = true;
			}
		}

		return modified;
	}

	/**
	 * @param row1
	 *            minimum selected row
	 * @param row2
	 *            maximum selected row
	 * @param insertAbove
	 *            true = insert above row1, false = insert below row2
	 *  @return whether a modification happened
	 */
	public boolean insertRow(int row1, int row2, boolean insertAbove) {
		boolean modified;
		if (insertAbove) {
			modified = shiftRowsDown(row1);
			table.getCellFormatHandler().shiftFormats(row1, 1, Direction.Down);
		} else {
			modified = shiftRowsDown(row2 + 1);
			table.getCellFormatHandler().shiftFormats(row2 + 1, 1,
					Direction.Down);
		}
		table.repaint();
		return modified;
	}

	/**
	 * @param row1
	 *            minimum selected row
	 * @param row2
	 *            maximum selected row
	 * @return whether a modification happened
	 */
	public boolean deleteRows(int row1, int row2) {
		table.getCopyPasteCut().delete(0, row1,
				tableModel.getHighestUsedColumn(), row2);
		boolean modified = shiftRowsUp(row2 + 1, row2 - row1 + 1);
		table.getCellFormatHandler().shiftFormats(row2 + 1, row2 - row1 + 1,
				Direction.Up);
		table.repaint();
		return modified;
	}

	private boolean shiftRowsDown(int startRow) {
		int maxColumn = tableModel.getHighestUsedColumn();
		int maxRow = tableModel.getHighestUsedRow();
		boolean modified = false;

		for (int row = maxRow; row >= startRow; --row) {
			for (int column = 0; column <= maxColumn; ++column) {
				GeoElement geo = RelativeCopy.getValue(tableModel, column, row);
				if (geo == null) {
					continue;
				}
				String newLabel = GeoElementSpreadsheet
						.getSpreadsheetCellName(column, row + 1);
				geo.setLabel(newLabel);
				modified = true;
			}
		}

		return modified;
	}

	private boolean shiftRowsUp(int startRow, int shiftAmount) {
		boolean modified = false;
		int maxColumn = tableModel.getHighestUsedColumn();
		int maxRow = tableModel.getHighestUsedRow();

		for (int row = startRow; row <= maxRow; ++row) {
			for (int column = 0; column <= maxColumn; ++column) {
				GeoElement geo = RelativeCopy.getValue(tableModel, column, row);
				if (geo == null) {
					continue;
				}
				String newLabel = GeoElementSpreadsheet
						.getSpreadsheetCellName(column, row - shiftAmount);
				geo.setLabel(newLabel);
				modified = true;
			}
		}
		return modified;
	}

}
