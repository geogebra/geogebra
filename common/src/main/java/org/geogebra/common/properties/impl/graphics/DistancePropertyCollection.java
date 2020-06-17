package org.geogebra.common.properties.impl.graphics;

import java.util.ArrayList;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.impl.AbstractPropertyCollection;

/**
 * This collection groups properties that are related to the distances of axes numbering.
 */
public class DistancePropertyCollection extends AbstractPropertyCollection {

	/**
	 * Constructs a numbering distances property collection.
	 * @param app application
	 * @param localization localization for the title
	 * @param euclidianSettings EV settings
	 */
	public DistancePropertyCollection(App app, Localization localization, EuclidianSettings
			euclidianSettings) {
		super(localization, "Distance");

		Kernel kernel = app.getKernel();
		EuclidianView euclidianView = app.getActiveEuclidianView();
		ArrayList<Property> properties = new ArrayList<>();

		properties.add(new AxesNumberingDistanceProperty(localization, euclidianSettings,
				euclidianView, kernel));
		properties.add(new AxisDistanceProperty(localization, euclidianSettings, euclidianView,
				kernel, "xAxis", 0));
		properties.add(new AxisDistanceProperty(localization, euclidianSettings, euclidianView,
				kernel, "yAxis", 1));
		if (euclidianSettings.getDimension() > 2) {
			properties.add(
					new AxisDistanceProperty(localization, euclidianSettings, euclidianView, kernel,
							"zAxis", 2));
		}

		setProperties(properties.toArray(new Property[0]));
	}
}
