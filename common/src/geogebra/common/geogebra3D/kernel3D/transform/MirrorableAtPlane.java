package geogebra.common.geogebra3D.kernel3D.transform;

import geogebra.common.kernel.geos.Mirrorable;
import geogebra.common.kernel.kernelND.GeoCoordSys2D;

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
