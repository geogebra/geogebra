package org.geogebra.common.euclidian.draw;

import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Drawable that can be used as part of other drawable: allowing to change the
 * represented geo.
 */
public abstract class SetDrawable extends Drawable {

	/**
	 * @param geo
	 *            referenced geo
	 */
	public abstract void setGeoElement(GeoElement geo);
}
