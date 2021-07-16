package org.geogebra.common.geogebra3D.euclidian3D.draw;

import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Extends z-picking functionality for surfaces and curves
 */
public interface HasZPick {

	/**
	 * @param zNear
	 *            front pick position
	 * @param zFar
	 *            far pick position
	 * @param discardPositive
	 *            whether to discard hits behind the eye
	 * @param positionOnHitting
	 *            position on hitting ray
	 */
	void setZPickIfBetter(double zNear, double zFar, boolean discardPositive,
			double positionOnHitting);

	/**
	 * @return drawn element
	 */
	GeoElement getGeoElement();

}
