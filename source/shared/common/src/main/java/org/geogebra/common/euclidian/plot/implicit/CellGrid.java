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
