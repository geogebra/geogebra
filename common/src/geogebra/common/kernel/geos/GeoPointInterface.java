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

	public double getZ();

	// below: temporary interface methods while porting
	public void translate(Coords v);
	public void setSpreadsheetTrace(boolean traceFlag);

	public void setCoords(GeoVec3D startPoint);

	public void setX(double d);
	public void setY(double d);
	public void setZ(double d);

}
