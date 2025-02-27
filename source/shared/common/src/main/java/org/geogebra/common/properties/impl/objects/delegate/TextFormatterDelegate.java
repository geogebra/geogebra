package org.geogebra.common.properties.impl.objects.delegate;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.HasTextFormatter;

public class TextFormatterDelegate extends AbstractGeoElementDelegate {

	public TextFormatterDelegate(GeoElement element)
			throws NotApplicablePropertyException {
		super(element);
	}

	@Override
	protected boolean checkIsApplicable(GeoElement element) {
		return element instanceof HasTextFormatter;
	}
}