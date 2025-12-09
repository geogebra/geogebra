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
import org.geogebra.common.gui.dialog.options.model.EuclidianOptionsModel;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.impl.collections.AbstractPropertyCollection;

/**
 * Collection of dimension properties of a graphics view.
 */
public class Dimension2DPropertiesCollection extends AbstractPropertyCollection<Property> {

	/**
	 * @param localization localization
	 */
	public Dimension2DPropertiesCollection(App app, Localization localization,
			EuclidianSettings settings, EuclidianViewInterfaceCommon view) {
		super(localization, "Dimensions");

		ArrayList<Property> properties = new ArrayList<>();
		properties.add(new DimensionRatioProperty(localization, view));
		properties.add(new DimensionMinMaxProperty(app, localization, "xmin", settings,
				EuclidianOptionsModel.MinMaxType.minX));
		properties.add(new DimensionMinMaxProperty(app, localization, "xmax", settings,
				EuclidianOptionsModel.MinMaxType.maxX));
		properties.add(new DimensionMinMaxProperty(app, localization, "ymin", settings,
				EuclidianOptionsModel.MinMaxType.minY));
		properties.add(new DimensionMinMaxProperty(app, localization, "ymax", settings,
				EuclidianOptionsModel.MinMaxType.maxY));
		setProperties(properties.toArray(new Property[0]));
	}
}
