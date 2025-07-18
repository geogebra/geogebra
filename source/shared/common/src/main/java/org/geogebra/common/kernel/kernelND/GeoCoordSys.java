package org.geogebra.common.kernel.kernelND;

import org.geogebra.common.kernel.matrix.CoordSys;

/**
 * Simple interface for elements that have a coord sys
 * 
 * @author Mathieu
 *
 */
public interface GeoCoordSys extends GeoDirectionND {

	/**
	 * return the coordinate system
	 * 
	 * @return the coordinate system
	 */
	CoordSys getCoordSys();

}
