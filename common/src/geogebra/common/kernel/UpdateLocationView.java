package geogebra.common.kernel;

import geogebra.common.kernel.geos.GeoElement;

/**
 * Interface for views that react to change of location
 * @author zbynek
 *
 */
public interface UpdateLocationView extends View {
	/**
	 * Notify this view about change of location
	 * @param geo geo whose location changed
	 */
	void updateLocation(GeoElement geo);

}
