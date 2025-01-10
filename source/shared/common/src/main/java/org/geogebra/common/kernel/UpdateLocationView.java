package org.geogebra.common.kernel;

import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Interface for views that react to change of location
 * 
 * @author zbynek
 *
 */
public interface UpdateLocationView extends View {
	/**
	 * Notify this view about change of location
	 * 
	 * @param geo
	 *            geo whose location changed
	 */
	void updateLocation(GeoElement geo);

}
