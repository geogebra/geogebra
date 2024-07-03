package org.geogebra.common.spreadsheet.core;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementSpreadsheet;

/**
 * It represents a copy/paste operation of a spreadsheet cell.
 * It contains the source cell, the destination cell
 * and an id (constructionIndex for GeoElements) of the source content.
 *
 * To perserve creation order at paste, first we collect these records,
 * and paste them to the designation cells in order of their ids.
 *
 */
final class CopyPasteCellOperation {
	private int id;
	private int sourceRow;
	private int sourceCol;
	private int destRow;
	private int destCol;

	CopyPasteCellOperation(int id, int sourceRow, int sourceCol, int destRow, int destCol) {
		this.id = id;
		this.sourceRow = sourceRow;
		this.destRow = destRow;
		this.sourceCol = sourceCol;
		this.destCol = destCol;
	}

	/**
	 * Executes the paste from buffer to tabularData.
	 * @param buffer to copy from.
	 * @param tabularData to paste to.
	 */
	void apply(TabularClipboard<GeoElement> buffer, TabularData<GeoElement> tabularData) {
		GeoElement value = buffer.contentAt(sourceRow, sourceCol);
		GeoElement copy = value.copy();
		copy.setLabel(GeoElementSpreadsheet.getSpreadsheetCellName(destCol, destRow));
		tabularData.setContent(destRow, destCol, copy);
	}

	int getId() {
		return id;
	}
}
