/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.properties.impl.objects.delegate;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFormula;
import org.geogebra.common.kernel.geos.GeoLocusStroke;
import org.geogebra.common.kernel.geos.GeoWidget;
import org.geogebra.common.kernel.geos.TextStyle;
import org.geogebra.common.kernel.statistics.GeoPieChart;

/**
 * Foreground color property type.
 */
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
		if (element instanceof TextStyle
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
