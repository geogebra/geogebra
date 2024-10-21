package org.geogebra.common.spreadsheet.core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Sortable list of copy/paste cell operations.
 */
final class CopyPasteCellOperationList {
	private List<CopyPasteCellOperation> list = new ArrayList<>();

	void clear() {
		list.clear();
	}

	/**
	 * Adds a copy/paste cell operation.
	 *
	 * @param geoToCopy Geo to be copy-pasted
	 * @param destRow to paste to.
	 * @param destCol to paste to.
	 */
	void add(GeoElement geoToCopy, int destRow, int destCol) {
		list.add(new CopyPasteCellOperation(geoToCopy, destRow, destCol));
	}

	/**
	 * Apply all the operations in list between two data sources.
	 * @param tabularData TabularData where content is pasted to
	 */
	void apply(TabularData<GeoElement> tabularData) {
		for (CopyPasteCellOperation operation: list) {
			operation.apply(tabularData);
		}
	}

	/**
	 * Sort the list by id (construction order)
	 */
	void sort() {
		list.sort(Comparator.comparing(CopyPasteCellOperation::getId));
	}
}
