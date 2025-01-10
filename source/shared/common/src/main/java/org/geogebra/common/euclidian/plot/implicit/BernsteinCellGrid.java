package org.geogebra.common.euclidian.plot.implicit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;

@SuppressWarnings("unused")
/**
 * Grid representation will be needed to fix the glitches.
 */
public class BernsteinCellGrid implements CellGrid<BernsteinPlotCell> {
	private BernsteinPlotCell[][] cells;

	@Override
	public void resize(EuclidianViewBounds bounds) {
		// TODO - implement
		cells = new BernsteinPlotCell[1][1];
	}

	/**
	 * @return All cells of the grid as a flattened list
	 */
	@Override
	public final List<BernsteinPlotCell> toList() {
		if (cells == null) {
			return Collections.emptyList();
		}

		List<BernsteinPlotCell> list = new ArrayList<>();
		for (int row = 0; row < cells.length; row++) {
			BernsteinPlotCell[] arow = cells[row];
			if (arow == null) {
				continue;
			}
			for (int col = 0; col < arow.length; col++) {
				BernsteinPlotCell cell = arow[col];
				if (cell != null) {
					list.add(cell);
				}
			}
		}
		return list;
	}

	/**
	 * @param cell the content
	 * @param row index to put
	 * @param column index to put
	 */
	@Override
	public void put(BernsteinPlotCell cell, int row, int column) {
		cells[row][column] = cell;
	}
}
