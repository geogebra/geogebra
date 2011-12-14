package geogebra.common.kernel.geos;

import geogebra.common.awt.Point;
import geogebra.common.kernel.Construction;
import geogebra.common.main.AbstractApplication;


public abstract class AbstractGeoElementSpreadsheet {

	public abstract boolean doisSpreadsheetLabel(String label1);

	public abstract Point dogetSpreadsheetCoordsForLabel(String label1);

	public abstract Object dospreadsheetIndices(String labelPrefix);

	public abstract String dogetSpreadsheetCellName(int i, int row);

	public abstract String dogetSpreadsheetColumnName(String label);
	
	public abstract String dogetSpreadsheetColumnName(int index);
	
	public abstract void setSpreadsheetCell(AbstractApplication app, int row, int col, GeoElementInterface cellGeo);

	public abstract GeoElement autoCreate(String label,Construction cons);

}
