package geogebra.common.kernel.kernelND;

import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.ToGeoElement;

/**
 * geos that have a direction (lines, vectors, planes, ...)
 * @author mathieu
 *
 */
public interface GeoDirectionND extends ToGeoElement{
	
	/**
	 * 
	 * @return the direction in 3D
	 */
	public Coords getDirectionInD3();

}
