package org.geogebra.common.spreadsheet.core;

import java.util.Comparator;

import org.geogebra.common.kernel.geos.GeoElement;

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

	public CopyPasteCellOperation(int id, int sourceRow, int sourceCol, int destRow, int destCol) {
		this.id = id;
		this.sourceRow = sourceRow;
		this.destRow = destRow;
		this.sourceCol = sourceCol;
		this.destCol = destCol;
	}

	public int getSourceRow() {
		return sourceRow;
	}

	public int getDestRow() {
		return destRow;
	}

	public int getSourceCol() {
		return sourceCol;
	}

	public int getDestCol() {
		return destCol;
	}

	public int getId() {
		return id;
	}

	/**
	 * Executes the paste from buffer to tabularData.
	 *
	 * @param buffer to copy from.
	 * @param tabularData to paste to.
	 */
	void apply(TabularBuffer<GeoElement> buffer, TabularData<GeoElement> tabularData) {
		GeoElement copy = copyGeo(buffer.contentAt(sourceRow, sourceCol));
		tabularData.setContent(destRow, destCol, copy);
	}

	private GeoElement copyGeo(GeoElement value) {
		return value.copy();
	}
}
