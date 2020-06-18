package org.geogebra.common.properties.impl.objects.delegate;

import org.geogebra.common.kernel.geos.GeoElement;

public class NamePropertyDelegate extends AbstractGeoElementDelegate {

	public NamePropertyDelegate(GeoElement element) throws NotApplicablePropertyException {
		super(element);
	}

	@Override
	protected boolean checkIsApplicable(GeoElement element) {
		String label = element.isAlgebraLabelVisible() ? element.getLabelSimple() : "";
		return label != null;
	}
}
