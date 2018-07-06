package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.kernelND.GeoElementND;

/**
 * Interface for embedded content (videos, GGB applets)
 * 
 * @author Zbynek
 */
public interface GeoFrame extends GeoElementND {

	/**
	 * Mark as ready when focused.
	 */
	void setReady();

	/**
	 * @return whether this is in ready state.
	 */
	boolean isReady();
}
