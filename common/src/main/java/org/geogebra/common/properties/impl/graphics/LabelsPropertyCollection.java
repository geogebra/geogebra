package org.geogebra.common.properties.impl.graphics;

import java.util.ArrayList;

import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.impl.AbstractPropertyCollection;

/**
 * This collection groups properties that are related to labeling the axes.
 */
public class LabelsPropertyCollection extends AbstractPropertyCollection {

	/**
	 * Constructs a labels property collection.
	 * @param localization localization for the title
	 * @param euclidianSettings euclidian settings
	 */
	public LabelsPropertyCollection(Localization localization,
			EuclidianSettings euclidianSettings) {
		super(localization, "Labels");

		ArrayList<Property> properties = new ArrayList<>();
		properties.add(new AxesLabelsVisibilityProperty(localization, euclidianSettings));
		properties.add(new AxisLabelProperty(localization, euclidianSettings, "xAxis", 0));
		properties.add(new AxisLabelProperty(localization, euclidianSettings, "yAxis", 1));
		if (euclidianSettings.getDimension() > 2) {
			properties.add(new AxisLabelProperty(localization,
					euclidianSettings, "zAxis", 2));
		}

		setProperties(properties.toArray(new Property[0]));
	}
}
