package org.geogebra.common.properties.impl.objects.delegate;

import org.geogebra.common.kernel.geos.GeoElement;

public class ObjectColorPropertyDelegate extends AbstractGeoElementDelegate {

	public ObjectColorPropertyDelegate(GeoElement element) throws NotApplicablePropertyException {
		super(element);
	}

	@Override
	protected boolean checkIsApplicable(GeoElement element) {
		return ColorPropertyType.forElement(element) == ColorPropertyType.OPAQUE;
	}
}
