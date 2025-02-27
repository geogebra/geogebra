package org.geogebra.common.properties.impl.objects.delegate;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFormula;
import org.geogebra.common.kernel.geos.GeoLocusStroke;
import org.geogebra.common.kernel.geos.GeoWidget;
import org.geogebra.common.kernel.geos.TextStyle;
import org.geogebra.common.kernel.statistics.GeoPieChart;

public enum ColorPropertyType {
	DEFAULT, WITH_OPACITY, OPAQUE, TEXT;

	/**
	 * @param element construction element
	 * @return type of color button to be shown in the UI
	 */
	public static ColorPropertyType forElement(GeoElement element) {
		if (element instanceof GeoWidget
				|| element instanceof GeoPieChart || element.isGeoImage()) {
			return DEFAULT;
		}
		if ((element instanceof TextStyle && !element.isGeoInputBox())
				|| element instanceof GeoFormula) {
			return TEXT;
		}
		if (element.isFillable()
				&& !element.isMask()
				&& !(element instanceof GeoLocusStroke)) {
			return WITH_OPACITY;
		}
		return OPAQUE;
	}
}
