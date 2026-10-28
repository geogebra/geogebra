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

package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.StringProperty;
import org.geogebra.common.properties.impl.collections.AbstractPropertyCollection;
import org.jspecify.annotations.NonNull;

/** Start and end fields displayed together in the BarChart Data tab. */
public final class BarChartBoundsPropertyCollection
		extends AbstractPropertyCollection<StringProperty> {

	BarChartBoundsPropertyCollection(
			@NonNull Localization localization,
			@NonNull StringProperty start,
			@NonNull StringProperty end) {
		super(localization, "Interval");
		setProperties(new StringProperty[] {start, end});
	}
}
