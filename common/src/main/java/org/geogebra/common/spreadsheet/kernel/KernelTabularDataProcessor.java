package org.geogebra.common.spreadsheet.kernel;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementSpreadsheet;

/**
 * Class to handle insert/delete operations of TabularData that has geos
 * While geos in cells are labeled as cell position, column and row
 * operations has to rename the geos correspondingly.
 *
 * For example if you have 100 rows, and you insert a row at 30, all geos bellow the 30. row has to
 * be renamed (A100 to A101, A99 to A100,...A31 to A32, A30 to A31, for each column)
 * Same for inserting a column.
 *
 * Delete renames the opposite way.
 *
 * Note that the actual insert of row/column happens when a new GeoElement takes its place
 * in them, handled by {@link KernelTabularDataAdapter}.
 */
public class KernelTabularDataProcessor {
	private final KernelTabularDataAdapter adapter;

	public KernelTabularDataProcessor(KernelTabularDataAdapter adapter) {
		this.adapter = adapter;
	}

	/**
	 *
	 * @param startRow to insert under.
	 * See class documentation above.
	 */
	void insertRowAt(int startRow) {
		for (int row = adapter.numberOfRows(); row >= startRow; --row) {
			for (int column = 0; column <= adapter.numberOfColumns(); ++column) {
				rename(adapter.contentAt(row, column), getCellName(row + 1, column));
			}
		};
	}

	private void rename(GeoElement geo, String newLabel) {
		if (geo == null) {
			return;
		}
		geo.setLabel(newLabel);
	}

	private String getCellName(int row, int column) {
		return GeoElementSpreadsheet.getSpreadsheetCellName(column, row);
	}

	/**
	 *
	 * @param rowToDelete delete that row.
	 * See class documentation above.
	 */
	public void deleteRowAt(int rowToDelete) {
		for (int row = rowToDelete; row <= adapter.numberOfRows(); ++row) {
			for (int column = 0; column <= adapter.numberOfColumns(); ++column) {
				rename(adapter.contentAt(row, column), getCellName(row - 1, column));
			}
		};
	}

	/**
	 *
	 * @param startColumn to insert a new column to the left.
	 * See class documentation above.
	 */
	public void insertColumnAt(int startColumn) {
		for (int column = adapter.numberOfColumns(); column >= startColumn; --column) {
			for (int row = 0; row <= adapter.numberOfRows(); ++row) {
				rename(adapter.contentAt(row, column), getCellName(row, column + 1));
			}
		}
	}

	/**
	 *
	 * @param columnToDelete delete that column.
	 * See class documentation above.
	 */
	public void deleteColumnAt(int columnToDelete) {
		for (int column = columnToDelete; column <= adapter.numberOfColumns(); ++column) {
			for (int row = 0; row <= adapter.numberOfRows(); ++row) {
				rename(adapter.contentAt(row, column), getCellName(row, column - 1));
			}
		}
	}
}
