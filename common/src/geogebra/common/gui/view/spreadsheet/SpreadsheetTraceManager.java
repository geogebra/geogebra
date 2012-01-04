package geogebra.common.gui.view.spreadsheet;

import geogebra.common.kernel.geos.GeoElement;

public abstract class SpreadsheetTraceManager {

	public abstract boolean isTraceGeo(GeoElement recordObject);

	public abstract void handleColumnDelete(int column1, int column2);
}
