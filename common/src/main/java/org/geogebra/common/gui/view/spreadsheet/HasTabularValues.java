package org.geogebra.common.gui.view.spreadsheet;

import org.geogebra.common.kernel.geos.GeoElement;

public interface HasTabularValues {
	GeoElement getGeoElement(int row, int column);

	int numberOfRows();

	int numberOfColumns();
}
