package org.geogebra.common.geogebra3D.euclidian3D.draw;

import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Caption factory.
 */
@FunctionalInterface
public interface CaptionFactory {
	/**
	 * @param geo construction element
	 * @return 3D caption
	 */
	CaptionText createStaticCaption3D(GeoElement geo);
}
