package org.geogebra.common.properties.impl.objects.delegate;

import org.geogebra.common.kernel.geos.GeoElement;

public class FillingStylePropertyDelegate extends AbstractGeoElementDelegate {

	public FillingStylePropertyDelegate(GeoElement element) throws NotApplicablePropertyException {
		super(element);
	}

	@Override
	protected boolean checkIsApplicable(GeoElement element) {
		return ColorPropertyType.forElement(element) == ColorPropertyType.WITH_OPACITY;
	}
}
