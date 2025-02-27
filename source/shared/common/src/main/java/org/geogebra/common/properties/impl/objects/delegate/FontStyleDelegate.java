package org.geogebra.common.properties.impl.objects.delegate;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.HasTextFormatter;
import org.geogebra.common.kernel.geos.TextProperties;

public class FontStyleDelegate extends AbstractGeoElementDelegate {

	public FontStyleDelegate(GeoElement element) throws NotApplicablePropertyException {
		super(element);
	}

	@Override
	protected boolean checkIsApplicable(GeoElement element) {
		return element instanceof HasTextFormatter || element instanceof TextProperties;
	}
}
