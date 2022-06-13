package org.geogebra.common.kernel;

import org.geogebra.common.kernel.kernelND.GeoElementND;

/**
 * Algo whose output is on path
 */
public interface FixedPathRegionAlgo {

	/**
	 * Returns true iff the output can be moved
	 * 
	 * @param out
	 *            output object
	 * 
	 * @return true iff the output can be moved
	 */
	boolean isChangeable(GeoElementND out);

}
