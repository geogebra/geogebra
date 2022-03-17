package org.geogebra.common.gui.view.table.dimensions;

import java.util.LinkedList;

/**
 * Class holding the cache values for the dimensions object.
 */
class DimensionCache {

	private TableValuesViewDimensions dimensions;
	private LinkedList<Integer> widths;

	/**
	 * Construct a cache object.
	 * @param dimensions dimensions
	 */
	DimensionCache(TableValuesViewDimensions dimensions) {
		this.dimensions = dimensions;
		resetCache();
	}

	/**
	 * Reset the cache.
	 */
	void resetCache() {
		widths = new LinkedList<>();
		for (int i = 0; i < dimensions.tableModel.getColumnCount(); i++) {
			widths.add(null);
		}
	}

	/**
	 * Get width from the cache.
	 * @param index column
	 * @return width
	 */
	int getWidth(int index) {
		return getExactWidth(index);
	}

	private int getExactWidth(int index) {
		Integer width = widths.get(index);
		if (width == null) {
			width = calculateExactWidth(index);
			widths.set(index, width);
		}
		return width;
	}

	private int calculateExactWidth(int column) {
		return dimensions.calculateExactColumnWidth(column);
	}

	/**
	 * Remove a column from cache.
	 * @param column column
	 */
	void removeColumn(int column) {
		widths.remove(column);
	}

	/**
	 * Add a column to the cache.
	 * @param column column
	 */
	void addColumn(int column) {
		widths.add(column, null);
	}

	/**
	 * Update a column of the cache
	 * @param column column
	 */
	void updateColumn(int column) {
		widths.set(column, null);
	}
}
