package org.geogebra.common.properties.impl.objects.delegate;

import org.geogebra.common.kernel.geos.GeoElement;

public interface GeoElementDelegate {

	GeoElement getElement();

	void checkIsApplicable() throws NotApplicablePropertyException;

	boolean isEnabled();
}
