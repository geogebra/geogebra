package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.AbstractProperty;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.PropertyCollection;

import java.util.ArrayList;

/**
 * This collection groups properties that are related to labeling the axes.
 */
public class LabelsPropertyCollection extends AbstractProperty
		implements PropertyCollection {

	private Property[] collection;

	/**
	 * Constructs a labels property collection.
	 *
	 * @param app
	 *            application
	 * @param localization
	 *            localization for the title
	 * @param euclidianSettings
	 *            euclidian settings
	 */
	public LabelsPropertyCollection(App app, Localization localization,
									EuclidianSettings euclidianSettings) {
		super(localization, "Labels");

		ArrayList<Property> properties = new ArrayList<>();
		properties.add(new AxesLabelsVisibilityProperty(localization, euclidianSettings));
		properties.add(new AxisLabelProperty(localization, euclidianSettings, "xAxis", 0));
		properties.add(new AxisLabelProperty(localization, euclidianSettings, "yAxis", 1));
		if ("3D".equals(app.getVersion().getAppName())) {
            properties.add(new AxisLabelProperty(localization, euclidianSettings, "zAxis", 2));
        }

		collection = new Property[properties.size()];
		collection = properties.toArray(collection);
	}

	@Override
	public Property[] getProperties() {
		return collection;
	}
}
