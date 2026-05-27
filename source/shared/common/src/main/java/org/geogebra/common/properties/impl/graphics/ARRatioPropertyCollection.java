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

package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.euclidian3D.EuclidianView3DInterface;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.impl.collections.AbstractPropertyCollection;

/** {@code PropertyCollection} of AR ratio related {@code Property}s. */
public final class ARRatioPropertyCollection extends AbstractPropertyCollection<Property> {
	/**
	 * Constructs the property collection.
	 * @param localization localization for the label translations
	 * @param euclidianView3D the 3D euclidian view
	 */
	public ARRatioPropertyCollection(Localization localization,
			EuclidianView3DInterface euclidianView3D) {
		super(localization, "");
		setProperties(new Property[] {
				new ShowARRatioProperty(localization, euclidianView3D),
				new ARRatioUnitProperty(localization, euclidianView3D),
		});
	}
}
