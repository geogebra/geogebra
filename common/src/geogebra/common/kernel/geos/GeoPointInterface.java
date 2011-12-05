package geogebra.common.kernel.geos;

import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.PathParameter;
import geogebra.common.kernel.Matrix.Coords;

public interface GeoPointInterface extends GeoElementInterface, GeoPointND {

	public boolean movePoint(Coords rwTransVec, Coords endPosition);

	public PathParameter getPathParameter();

	public double getInhomX();

	public double getInhomY();

	public double getX();

	public double getY();

	// below: temporary interface methods while porting
	public void translate(Coords v);
	public void setSpreadsheetTrace(boolean traceFlag);
}
