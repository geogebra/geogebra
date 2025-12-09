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

package org.geogebra.common.euclidian.plot.implicit;

import java.util.List;

import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;

/**
 * Cell grid.
 * @param <T> grid cell type
 */
public interface CellGrid<T> {

	/**
	 * Resize the grid to view size.
	 * @param bounds view bounds
	 */
	void resize(EuclidianViewBounds bounds);

	/**
	 * @return All cells of the grid as a flattened list
	 */
	List<BernsteinPlotCell> toList();

	/**
	 * Add a cell to the grid.
	 * @param cell cell
	 * @param row row index
	 * @param column column index
	 */
	void put(T cell, int row, int column);
}
