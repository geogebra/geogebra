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

package org.geogebra.common.spreadsheet.kernel;

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementSpreadsheet;
import org.geogebra.common.ownership.NonOwning;

/**
 * Class to handle insert/delete operations of TabularData that has geos.
 * While geos in cells are labeled as cell position, column and row
 * operations have to rename the geos correspondingly.
 * <p>
 * For example, if you have 100 rows, and you insert a row at 30, all geos above the 30th row have
 * to be renamed (A100 to A101, A99 to A100,...A31 to A32, A30 to A31, for each column)
 * Same for inserting a column.
 * <p>
 * Delete renames the opposite way.
 * <p>
 * Note that the actual insert of row/column happens when a new GeoElement takes its place
 * in them, handled by {@link KernelTabularDataAdapter}.
 */
final class KernelTabularDataProcessor {

	@NonOwning
	private final KernelTabularDataAdapter adapter;

	KernelTabularDataProcessor(@Nonnull KernelTabularDataAdapter adapter) {
		this.adapter = adapter;
	}

	/**
	 *
	 * @param startRow to insert a new row above.
	 * See class documentation above.
	 */
	void insertRowAt(int startRow) {
		for (int row = adapter.numberOfRows(); row >= startRow; --row) {
			for (int column = 0; column <= adapter.numberOfColumns(); ++column) {
				renameCellGeo(row, column, row + 1, column);
			}
		}
	}

	private void renameCellGeo(int sourceRow, int sourceColumn, int targetRow, int targetColumn) {
		GeoElement geo = adapter.contentAt(sourceRow, sourceColumn);
		if (geo == null) {
			return;
		}
		geo.setLabel(getCellName(targetRow, targetColumn));
	}

	static String getCellName(int targetRow, int targetColumn) {
		return GeoElementSpreadsheet.getSpreadsheetCellName(targetColumn, targetRow);
	}

	/**
	 *
	 * @param rowToDelete delete that row.
	 * See class documentation above.
	 */
	void deleteRowAt(int rowToDelete) {
		for (int column = 0; column <= adapter.numberOfColumns(); ++column) {
			removeContentAt(rowToDelete, column);
		}

		for (int row = rowToDelete + 1; row <= adapter.numberOfRows(); ++row) {
			for (int column = 0; column <= adapter.numberOfColumns(); ++column) {
				renameCellGeo(row, column, row - 1, column);
			}
		}
	}

	void removeContentAt(int row, int column) {
		GeoElement geo = adapter.contentAt(row, column);
		if (geo != null) {
			geo.remove();
		}
	}

	/**
	 *
	 * @param startColumn to insert a new column to the left.
	 * See class documentation above.
	 */
	void insertColumnAt(int startColumn) {
		for (int column = adapter.numberOfColumns(); column >= startColumn; --column) {
			for (int row = 0; row <= adapter.numberOfRows(); ++row) {
				renameCellGeo(row, column, row, column + 1);
			}
		}
	}

	/**
	 *
	 * @param columnToDelete delete that column.
	 * See class documentation above.
	 */
	void deleteColumnAt(int columnToDelete) {
		for (int row = 0; row <= adapter.numberOfRows(); ++row) {
			removeContentAt(row, columnToDelete);
		}

		for (int column = columnToDelete; column <= adapter.numberOfColumns(); ++column) {
			for (int row = 0; row <= adapter.numberOfRows(); ++row) {
				renameCellGeo(row, column, row, column - 1);
			}
		}
	}
}
