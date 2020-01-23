package org.geogebra.common.kernel.geos.output;

import org.geogebra.common.kernel.geos.GeoElement;

public class NoFilter implements GeoOutputFilter {

	@Override
	public boolean shouldFilterCaption(GeoElement element) {
		return false;
	}

	@Override
	public String filterCaption(GeoElement element) {
		return element.getLabelDescription();
	}
}
