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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;

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
