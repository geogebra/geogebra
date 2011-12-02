package geogebra.common.kernel.geos;

import geogebra.common.kernel.PathParameter;
import geogebra.common.kernel.Matrix.Coords;

public interface GeoPointInterface {

	public boolean movePoint(Coords rwTransVec, Coords endPosition);

	public PathParameter getPathParameter();

	public double getInhomX();

	public double getInhomY();

	public double getX();

	public double getY();

}
