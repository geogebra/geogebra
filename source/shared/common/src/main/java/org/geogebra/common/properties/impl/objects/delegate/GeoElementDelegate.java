package org.geogebra.common.properties.impl.objects.delegate;

import org.geogebra.common.annotation.MissingDoc;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Construction element delegate.
 */
public interface GeoElementDelegate {

	@MissingDoc
	GeoElement getElement();

	@MissingDoc
	void checkIsApplicable() throws NotApplicablePropertyException;

}
