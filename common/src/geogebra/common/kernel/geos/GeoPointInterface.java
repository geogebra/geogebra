package geogebra.common.kernel.geos;

import geogebra.common.kernel.PathParameter;
import geogebra.common.kernel.Matrix.Coords;

public interface GeoPointInterface {

	public boolean movePoint(Coords rwTransVec, Coords endPosition);

	public PathParameter getPathParameter();

}
