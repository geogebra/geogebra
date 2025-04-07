package org.geogebra.common.spreadsheet.kernel;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.spreadsheet.core.TabularData;

/**
 * It represents a copy/paste operation of a spreadsheet cell.
 * It contains the source cell, the destination cell
 * and an id (constructionIndex for GeoElements) of the source content.
 *
 * To preserve creation order at paste, first we collect these records,
 * and paste them to the designation cells in order of their ids.
 *
 */
final class CopyPasteCellOperation {
	private GeoElement geoToCopy;
	private int destRow;
	private int destCol;

	CopyPasteCellOperation(GeoElement geoToCopy, int destRow, int destCol) {
		this.geoToCopy = geoToCopy;
		this.destRow = destRow;
		this.destCol = destCol;
	}

	/**
	 * Executes the paste from buffer to tabularData.
	 * @param tabularData to paste to.
	 */
	void apply(TabularData<GeoElement> tabularData) {
		KernelTabularDataAdapter.setEuclidianVisibilityAndAuxiliaryFlag(geoToCopy);
		tabularData.setContent(destRow, destCol, geoToCopy);
	}

	int getId() {
		return geoToCopy.getConstructionIndex();
	}
}
