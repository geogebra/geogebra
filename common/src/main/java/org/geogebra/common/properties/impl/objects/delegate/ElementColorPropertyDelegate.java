package org.geogebra.common.properties.impl.objects.delegate;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.statistics.GeoPieChart;

public class ElementColorPropertyDelegate extends AbstractGeoElementDelegate {

	public ElementColorPropertyDelegate(GeoElement element) throws NotApplicablePropertyException {
		super(element);
	}

	@Override
	protected boolean checkIsApplicable(GeoElement element) {
		return !(element instanceof GeoPieChart || element instanceof GeoImage);
	}
}
