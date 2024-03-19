package org.geogebra.common.euclidian.measurement;

import org.geogebra.common.kernel.geos.GeoImage;

/**
 * Interface to create the tool image.
 */
public interface CreateToolImage {

	/**
	 * Create image belongs to mode and stored on internal name.
	 * @param mode of the tool
	 * @param internalName of the tool
	 * @return the image of the tool
	 */
	GeoImage create(int mode, String internalName);
}
