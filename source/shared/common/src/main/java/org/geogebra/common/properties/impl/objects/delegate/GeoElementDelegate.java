package org.geogebra.common.properties.impl.objects.delegate;

import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Construction element delegate.
 */
public interface GeoElementDelegate {

	/**
	 * @return related construction element
	 */
	GeoElement getElement();

	/**
	 * Check whether the property is applicable to the related element.
	 * @throws NotApplicablePropertyException if it's not applicable
	 */
	void checkIsApplicable() throws NotApplicablePropertyException;

}
