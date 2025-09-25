package org.geogebra.common.properties.impl.graphics;

import java.util.ArrayList;

import org.geogebra.common.gui.dialog.options.model.EuclidianOptionsModel;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.impl.collections.AbstractPropertyCollection;

/**
 * Collection of dimension properties of a graphics view.
 */
public class Dimension3DPropertiesCollection
		extends AbstractPropertyCollection<DimensionMinMaxProperty> {

	/**
	 * @param app application
	 * @param localization localization
	 * @param settings euclidian settings
	 */
	public Dimension3DPropertiesCollection(App app, Localization localization,
			EuclidianSettings settings) {
		super(localization, "Dimensions");

		ArrayList<DimensionMinMaxProperty> properties = new ArrayList<>();
		properties.add(new DimensionMinMaxProperty(app, localization, "xmin", settings,
				EuclidianOptionsModel.MinMaxType.minX));
		properties.add(new DimensionMinMaxProperty(app, localization, "xmax", settings,
				EuclidianOptionsModel.MinMaxType.maxX));
		properties.add(new DimensionMinMaxProperty(app, localization, "ymin", settings,
				EuclidianOptionsModel.MinMaxType.minY));
		properties.add(new DimensionMinMaxProperty(app, localization, "ymax", settings,
				EuclidianOptionsModel.MinMaxType.maxY));
		properties.add(new DimensionMinMaxProperty(app, localization, "zmin", settings,
				EuclidianOptionsModel.MinMaxType.minZ));
		properties.add(new DimensionMinMaxProperty(app, localization, "zmax", settings,
				EuclidianOptionsModel.MinMaxType.maxZ));
		setProperties(properties.toArray(new DimensionMinMaxProperty[0]));
	}
}
