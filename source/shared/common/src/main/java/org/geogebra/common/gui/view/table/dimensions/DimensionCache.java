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
