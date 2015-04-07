package org.geogebra.common.kernel.kernelND;

import org.geogebra.common.kernel.Matrix.CoordSys;

/** Simple interface for elements that have a coord sys
 * @author matthieu
 *
 */
public interface GeoCoordSys {
	
	/** set the coordinate system
	 * @param cs the coordinate system
	 */
	 //public void setCoordSys(CoordSys cs);
	 
	/** return the coordinate system
	 * @return the coordinate system
	 */
	public CoordSys getCoordSys();

}
