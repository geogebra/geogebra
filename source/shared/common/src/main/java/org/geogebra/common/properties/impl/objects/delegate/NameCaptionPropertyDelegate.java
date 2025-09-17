package org.geogebra.common.properties.impl.objects.delegate;

import org.geogebra.common.kernel.geos.GeoElement;

public class NameCaptionPropertyDelegate extends AbstractGeoElementDelegate {

	public NameCaptionPropertyDelegate(GeoElement element) throws NotApplicablePropertyException {
		super(element);
	}

	@Override
	protected boolean checkIsApplicable(GeoElement element) {
		return !element.isGeoText() && !element.isGeoImage();
	}
}
