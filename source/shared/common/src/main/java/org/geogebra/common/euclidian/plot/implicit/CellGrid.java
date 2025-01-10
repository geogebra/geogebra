package org.geogebra.common.euclidian.plot.implicit;

import java.util.List;

import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;

public interface CellGrid<T> {
	void resize(EuclidianViewBounds bounds);

	List<BernsteinPlotCell> toList();

	void put(T cell, int row, int column);
}
