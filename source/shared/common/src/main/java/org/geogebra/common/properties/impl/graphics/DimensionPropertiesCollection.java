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
		if (settings.getDimension() > 2) {
			properties.add(new DimensionMinMaxProperty(app, localization, "zmin", settings,
					EuclidianOptionsModel.MinMaxType.minZ));
			properties.add(new DimensionMinMaxProperty(app, localization, "zmax", settings,
					EuclidianOptionsModel.MinMaxType.maxZ));
		}
		setProperties(properties.toArray(new Property[0]));
	}
}
