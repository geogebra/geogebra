package org.geogebra.common.kernel.construction;

import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * Creates default GeoElement objects
 */
public interface GeoElementFactory {

	/**
	 * Creates GeoNumeric object
	 * @return new numeric
	 */
	GeoNumeric createNumeric();

	/**
	 * Creates GeoAngle object
	 * @return new angle
	 */
	GeoAngle createAngle();

	/**
	 * @return GeoElementPropertyInitializer
	 */
	GeoElementPropertyInitializer getPropertyInitializer();
}
