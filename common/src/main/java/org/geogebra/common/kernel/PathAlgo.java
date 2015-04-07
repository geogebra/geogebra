package org.geogebra.common.kernel;

/**
 * Algo whose output is on path
 */
public interface PathAlgo {

	/**
	 * Returns true iff the output can be moved
	 * 
	 * @return true iff the output can be moved
	 */
	public boolean isChangeable();

}
