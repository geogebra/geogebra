package org.geogebra.common.kernel.kernelND;

import org.geogebra.common.kernel.Matrix.Coords;

/**
 * geos that have a direction (lines, vectors, planes, ...)
 * @author mathieu
 *
 */
public interface GeoDirectionND extends GeoElementND{
	
	/**
	 * 
	 * @return the direction in 3D
	 */
	public Coords getDirectionInD3();
	

}
