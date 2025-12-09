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

import java.util.ArrayList;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.impl.collections.AbstractPropertyCollection;

public class NavigationBarPropertiesCollection extends AbstractPropertyCollection<Property> {

	/**
	 * @param localization localization
	 * @param settings view settings
	 */
	public NavigationBarPropertiesCollection(Localization localization, App app, int viewID,
			EuclidianSettings settings) {
		super(localization, "");

		ArrayList<Property> properties = new ArrayList<>();
		properties.add(new NavigationBarProperty(localization, app, viewID));
		properties.add(new NavigationBarPlayButtonProperty(localization, app, viewID,
				settings));
		properties.add(new NavigationBarConstructionProtocolButtonProperty(localization, app,
				viewID, settings));
		setProperties(properties.toArray(new Property[0]));
	}
}
