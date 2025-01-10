package org.geogebra.common.properties.impl.objects.delegate;

import org.geogebra.common.kernel.geos.GeoElement;

public class CaptionStyleDelegate extends AbstractGeoElementDelegate {

	public CaptionStyleDelegate(GeoElement element) throws NotApplicablePropertyException {
		super(element);
	}

	@Override
	protected boolean checkIsApplicable(GeoElement element) {
		return !isTextOrInput(element);
	}
}
