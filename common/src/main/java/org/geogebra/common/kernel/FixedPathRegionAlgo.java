package org.geogebra.common.kernel;

import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Algo whose output is on path
 */
public interface FixedPathRegionAlgo {

	/**
	 * Returns true iff the output can be moved
	 * 
	 * @return true iff the output can be moved
	 */
	public boolean isChangeable(GeoElement out);

}
