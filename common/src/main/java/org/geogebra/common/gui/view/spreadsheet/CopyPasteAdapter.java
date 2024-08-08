package org.geogebra.common.gui.view.spreadsheet;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.SpreadsheetTableModel;
import org.geogebra.common.util.debug.Log;

public class CopyPasteAdapter {

	private final App app;
	private final SpreadsheetTableModel tableModel;

	/**
	 * @param app application
	 * @param tableModel classic spreadsheet model (optional)
	 */
	public CopyPasteAdapter(App app, SpreadsheetTableModel tableModel) {
		this.app = app;
		this.tableModel = tableModel;
	}

	/**
	 * Pastes data from 2D String array into a given set of cells. The data may
	 * be pasted multiple times to fill in an oversized target rectangle (and
	 * maybe overflow a bit).
	 *
	 * @param data
	 *            pasted data
	 * @param column1
	 *            minimum target column
	 * @param row1
	 *            minimum target row
	 * @param column2
	 *            maximum target column
	 * @param row2
	 *            maximum target row
	 * @return whether all cells were pasted successfully
	 */
	public boolean pasteExternalMultiple(String[][] data, int column1,
			int row1, int column2, int row2) {

		boolean oldEqualsSetting = app.getSettings().getSpreadsheet()
				.equalsRequired();
		app.getSettings().getSpreadsheet().setEqualsRequired(true);

		boolean succ = true;

		// Fixing NPE in chrome:
		if (data == null) {
			return false;
		} else if (data[0] == null) {
			return false;
		}

		int rowStep = data.length;
		int columnStep = data[0].length;

		if (columnStep == 0) {
			return false;
		}

		int maxColumn = Math.max(column2, column1 + rowStep);
		int maxRow = Math.max(row2, row1 + rowStep);

		// paste data multiple times to fill in the selection rectangle (and
		// maybe overflow a bit)
		for (int c = column1; c <= column2; c += columnStep) {
			for (int r = row1; r <= row2; r += rowStep) {
				succ = succ && pasteExternal(data, c, r, maxColumn, maxRow);
			}
		}

		app.getSettings().getSpreadsheet().setEqualsRequired(oldEqualsSetting);

		return succ;
	}

	/**
	 * @param data serialized data
	 * @param column1 min column
	 * @param row1 min row
	 * @param maxColumn max column
	 * @param maxRow max row
	 * @return success
	 */
	public boolean pasteExternal(String[][] data, int column1, int row1,
			int maxColumn, int maxRow) {
		app.setWaitCursor();
		boolean succ = false;

		try {
			if (tableModel != null && tableModel.getRowCount() < row1 + data.length) {
				tableModel.setRowCount(row1 + data.length);
			}
			GeoElementND[][] values2 = new GeoElement[data.length][];
			int maxLen = -1;
			RelativeCopy relativeCopy = new RelativeCopy(app.getKernel());
			for (int row = row1; row < row1 + data.length; ++row) {
				if (row < 0 || row > maxRow) {
					continue;
				}
				int iy = row - row1;
				values2[iy] = new GeoElement[data[iy].length];
				if (maxLen < data[iy].length) {
					maxLen = data[iy].length;
				}
				if (tableModel != null && tableModel.getColumnCount() < column1 + data[iy].length) {
					tableModel.setColumnCount(column1 + data[iy].length);
				}
				for (int column = column1; column < column1
						+ data[iy].length; ++column) {
					if (column < 0 || column > maxColumn) {
						continue;
					}
					int ix = column - column1;
					// "]");
					if (data[iy][ix] == null) {
						continue;
					}
					data[iy][ix] = data[iy][ix].trim();
					if (data[iy][ix].length() == 0) {
						GeoElement value0 = RelativeCopy.getValue(app, column,
								row);
						if (value0 != null) {
							value0.removeOrSetUndefinedIfHasFixedDescendent();
						}
					} else {
						GeoElement value0 = RelativeCopy.getValue(app, column,
								row);
						values2[iy][ix] = relativeCopy
								.prepareAddingValueToTableNoStoringUndoInfo(
										data[iy][ix], value0, column, row, true);
						// values2[iy][ix].setAuxiliaryObject(values2[iy][ix].isGeoNumeric());
						values2[iy][ix].setAuxiliaryObject(true);

					}
				}
			}
			app.repaintSpreadsheet();

			/*
			 * if (values2.length == 1 || maxLen == 1) {
			 * createPointsAndAList1(values2); } if (values2.length == 2 ||
			 * maxLen == 2) { createPointsAndAList2(values2); }
			 */

			succ = true;
		} catch (Exception ex) {
			Log.debug(ex);
		} finally {
			app.setDefaultCursor();
		}

		return succ;
	}
}
