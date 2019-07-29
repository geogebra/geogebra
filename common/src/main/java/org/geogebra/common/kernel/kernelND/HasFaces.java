package org.geogebra.common.kernel.kernelND;

import org.geogebra.common.kernel.geos.GeoPolygon;

/**
 * interface for geos that have faces
 *
 */
public interface HasFaces {

	/**
	 * @param index
	 *            face index
	 * @return face
	 */
	GeoPolygon getFace(int index);

	/**
	 * @return total number of faces
	 */
	int getFacesSize();

}
