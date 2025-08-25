package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.kernelND.GeoConicND;

/**
 * Represents geos that can be mirrored at a line or point
 * 
 */
public interface ConicMirrorable {

	/**
	 * Mirror at circle
	 * @param conic mirror circle
	 */
	public void mirror(GeoConicND conic);

}
