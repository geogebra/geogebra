package org.geogebra.common.properties.impl.objects.delegate;

import org.geogebra.common.kernel.geos.GeoElement;

public class SegmentPropertyDelegate extends AbstractGeoElementDelegate {

	public SegmentPropertyDelegate(GeoElement element)
			throws NotApplicablePropertyException {
		super(element);
	}

	@Override
	protected boolean checkIsApplicable(GeoElement element) {
		return element.isGeoSegment();
	}
}