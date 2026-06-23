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
import org.geogebra.common.properties.impl.collections.AbstractPropertyCollection;

/**
 * Collection of dimension properties of a graphics view.
 */
public class Dimension3DPropertiesCollection
		extends AbstractPropertyCollection<DimensionMinMaxProperty> {

	/**
	 * @param app application
	 * @param localization localization
	 * @param euclidianView euclidian view
	 */
	public Dimension3DPropertiesCollection(App app, Localization localization,
			EuclidianViewInterfaceCommon euclidianView) {
		super(localization, "Dimensions");

		ArrayList<DimensionMinMaxProperty> properties = new ArrayList<>();
		properties.add(new DimensionMinMaxProperty(app, localization, "xmin", euclidianView,
				EuclidianOptionsModel.MinMaxType.minX));
		properties.add(new DimensionMinMaxProperty(app, localization, "xmax", euclidianView,
				EuclidianOptionsModel.MinMaxType.maxX));
		properties.add(new DimensionMinMaxProperty(app, localization, "ymin", euclidianView,
				EuclidianOptionsModel.MinMaxType.minY));
		properties.add(new DimensionMinMaxProperty(app, localization, "ymax", euclidianView,
				EuclidianOptionsModel.MinMaxType.maxY));
		properties.add(new DimensionMinMaxProperty(app, localization, "zmin", euclidianView,
				EuclidianOptionsModel.MinMaxType.minZ));
		properties.add(new DimensionMinMaxProperty(app, localization, "zmax", euclidianView,
				EuclidianOptionsModel.MinMaxType.maxZ));
		setProperties(properties.toArray(new DimensionMinMaxProperty[0]));
	}
}
