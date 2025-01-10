package org.geogebra.common.geogebra3D.kernel3D.transform;

import org.geogebra.common.kernel.geos.Mirrorable;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;

/**
 * Interface for geos that can be mirrored at plane
 * 
 * @author mathieu
 *
 */
public interface MirrorableAtPlane extends Mirrorable {

	/**
	 * Mirror at plane
	 * 
	 * @param plane
	 *            plane
	 */
	public void mirror(GeoCoordSys2D plane);
}
