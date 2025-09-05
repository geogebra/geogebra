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
public class DimensionPropertiesCollection extends AbstractPropertyCollection<Property> {

	/**
	 * @param localization localization
	 */
	public DimensionPropertiesCollection(App app, Localization localization,
			EuclidianSettings settings, EuclidianViewInterfaceCommon view) {
		super(localization, "Dimension");

		ArrayList<Property> properties = new ArrayList<>();
		properties.add(new DimensionRatioProperty(localization, view));
		properties.add(new DimensionMinMaxProperty(app, localization, "xMin", settings,
				EuclidianOptionsModel.MinMaxType.minX));
		properties.add(new DimensionMinMaxProperty(app, localization, "xMax", settings,
				EuclidianOptionsModel.MinMaxType.maxX));
		properties.add(new DimensionMinMaxProperty(app, localization, "yMin", settings,
				EuclidianOptionsModel.MinMaxType.minY));
		properties.add(new DimensionMinMaxProperty(app, localization, "yMax", settings,
				EuclidianOptionsModel.MinMaxType.maxY));
		if (settings.getDimension() > 2) {
			properties.add(new DimensionMinMaxProperty(app, localization, "zMin", settings,
					EuclidianOptionsModel.MinMaxType.minZ));
			properties.add(new DimensionMinMaxProperty(app, localization, "zMax", settings,
					EuclidianOptionsModel.MinMaxType.maxZ));
		}
		setProperties(properties.toArray(new Property[0]));
	}
}
