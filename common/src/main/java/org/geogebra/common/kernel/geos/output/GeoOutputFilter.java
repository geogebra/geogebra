package org.geogebra.common.kernel.geos.output;

import org.geogebra.common.kernel.geos.GeoElement;

public interface GeoOutputFilter {

	boolean shouldFilterCaption(GeoElement element);
	String filterCaption(GeoElement element);
}
