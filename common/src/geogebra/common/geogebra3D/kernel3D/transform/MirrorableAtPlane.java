package geogebra.common.geogebra3D.kernel3D.transform;

import geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3D;
import geogebra.common.kernel.geos.Mirrorable;

/**
 * Interface for geos that can be mirrored at plane
 * @author mathieu
 *
 */
public interface MirrorableAtPlane extends Mirrorable{

	/**
	 * Mirror at plane
	 * @param plane plane
	 */
	public void mirror(GeoPlane3D plane);
}
