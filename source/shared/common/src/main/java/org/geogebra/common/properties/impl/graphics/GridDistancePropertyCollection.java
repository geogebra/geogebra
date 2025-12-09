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

import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.impl.collections.AbstractPropertyCollection;

/**
 * This collection groups properties that are related to the distances of grid.
 */
public class GridDistancePropertyCollection extends AbstractPropertyCollection<Property> {

	/**
	 * Constructs a grid distance property collection.
	 * @param app application
	 * @param localization localization for the title
	 * @param euclidianSettings EV settings
	 * @param euclidianView euclidian view
	 */
	public GridDistancePropertyCollection(App app, Localization localization, EuclidianSettings
			euclidianSettings, EuclidianViewInterfaceCommon euclidianView) {
		super(localization, "Distance");

		ArrayList<Property> properties = new ArrayList<>();
		AlgebraProcessor ap = app.getKernel().getAlgebraProcessor();
		properties.add(new GridFixedDistanceProperty(localization, euclidianSettings));
		properties.add(new GridDistanceProperty(ap, localization, euclidianView, "x", 0));
		properties.add(new GridDistanceProperty(ap, localization, euclidianView, "y", 1));
		properties.add(new GridDistanceProperty(ap, localization, euclidianView, "r", 0));
		properties.add(new GridAngleProperty(ap, localization, euclidianView));
		setProperties(properties.toArray(new Property[0]));
	}
}
