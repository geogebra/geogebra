package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.kernelND.GeoElementND;

/**
 * Interface for objects with symbolic AV display
 * 
 * @author Zbynek
 *
 */
public interface HasSymbolicMode extends GeoElementND {

	/**
	 * Sets the symbolic mode to the default
	 */
	void initSymbolicMode();

	/**
	 * @param mode
	 *            symbolic mode
	 * @param updateParent
	 *            whether parent algo needs updating
	 **/
	void setSymbolicMode(boolean mode, boolean updateParent);

	/** @return Whether this has symbolic mode, eg. fractions for numbers */
	boolean isSymbolicMode();
}
