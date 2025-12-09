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
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.impl.collections.AbstractPropertyCollection;

public class ARRatioPropertyCollection extends AbstractPropertyCollection<Property> {

	/**
	 * Constructs a ar ratio property collection.
	 * @param app application
	 * @param localization localization for the title
	 */
	public ARRatioPropertyCollection(App app, Localization localization) {
		super(localization, "Settings.ArRatio");

		EuclidianView3DInterface view3D = app.getEuclidianView3D();
		Property arRatio = new ARRatioProperty(localization, view3D);
		Property ratioUnit = new RatioUnitProperty(localization, view3D);
		setProperties(new Property[]{arRatio, ratioUnit});
	}
}
