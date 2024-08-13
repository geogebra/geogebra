package org.geogebra.common.gui.view.spreadsheet;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.SpreadsheetTableModel;
import org.geogebra.common.spreadsheet.core.TabularRange;
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
	 * be pasted multiple times (both vertically and horizontally) to fill the target rectangle.
	 * In each direction at least one copy is pasted, meaning that the target rectangle may
	 * overflow.
	 *
	 * @param data
	 *            pasted data
	 * @param tiledRange
	 *            destination range extended ti a multiple of the data size
	 * @return success
	 */
	public boolean pasteExternalMultiple(String[][] data, TabularRange tiledRange) {
		boolean succ = true;
		// Fixing NPE in chrome:
		if (data == null || data[0] == null || data[0].length == 0) {
			return false;
		}

		int rowStep = data.length;
		int columnStep = data[0].length;

		// paste data multiple times to fill in the selection rectangle (and
		// maybe overflow a bit)
		for (int c = tiledRange.getMinColumn(); c <= tiledRange.getMaxColumn(); c += columnStep) {
			for (int r = tiledRange.getMinRow(); r <= tiledRange.getMaxRow(); r += rowStep) {
				succ = succ && pasteExternal(data, c, r, tiledRange.getMaxColumn(),
						tiledRange.getMaxRow());
			}
		}

		return succ;
	}

	/**
	 * Pastes a single copy of tabular data into a given range. If the size of the data
	 * exceeds size of the range, the data will be cropped.
	 * @param data serialized data
	 * @param minColumn min column (inclusive)
	 * @param minRow min row (inclusive)
	 * @param maxColumn max column (inclusive)
	 * @param maxRow max row (inclusive)
	 * @return success
	 */
	public boolean pasteExternal(String[][] data, int minColumn, int minRow,
			int maxColumn, int maxRow) {
		app.setWaitCursor();
		boolean success = false;

		try {
			if (tableModel != null && tableModel.getRowCount() < minRow + data.length) {
				tableModel.setRowCount(minRow + data.length);
			}
			GeoElementND[][] values = new GeoElement[data.length][];
			int maxLen = -1;
			RelativeCopy relativeCopy = new RelativeCopy(app.getKernel());
			for (int row = minRow; row < minRow + data.length; ++row) {
				if (row < 0 || row > maxRow) {
					continue;
				}
				int iy = row - minRow;
				values[iy] = new GeoElement[data[iy].length];
				if (maxLen < data[iy].length) {
					maxLen = data[iy].length;
				}
				if (tableModel != null
						&& tableModel.getColumnCount() < minColumn + data[iy].length) {
					tableModel.setColumnCount(minColumn + data[iy].length);
				}
				for (int column = minColumn; column < minColumn
						+ data[iy].length; ++column) {
					if (column < 0 || column > maxColumn) {
						continue;
					}
					int ix = column - minColumn;
					if (data[iy][ix] == null) {
						continue;
					}
					data[iy][ix] = data[iy][ix].trim();
					if (data[iy][ix].isEmpty()) {
						GeoElement value0 = RelativeCopy.getValue(app, column,
								row);
						if (value0 != null) {
							value0.removeOrSetUndefinedIfHasFixedDescendent();
						}
					} else {
						GeoElement value0 = RelativeCopy.getValue(app, column,
								row);
						values[iy][ix] = relativeCopy
								.prepareAddingValueToTableNoStoringUndoInfo(
										data[iy][ix], value0, column, row, true);
						values[iy][ix].setAuxiliaryObject(true);

					}
				}
			}
			app.repaintSpreadsheet();

			success = true;
		} catch (Exception ex) {
			Log.debug(ex);
		} finally {
			app.setDefaultCursor();
		}

		return success;
	}
}
