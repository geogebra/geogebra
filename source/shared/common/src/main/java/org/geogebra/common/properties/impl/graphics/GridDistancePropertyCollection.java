package org.geogebra.common.properties.impl.graphics;

import java.util.ArrayList;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.Kernel;
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
	 */
	public GridDistancePropertyCollection(App app, Localization localization, EuclidianSettings
			euclidianSettings) {
		super(localization, "Distance");

		Kernel kernel = app.getKernel();
		EuclidianView euclidianView = app.getActiveEuclidianView();

		ArrayList<Property> properties = new ArrayList<>();
		properties.add(new GridFixedDistanceProperty(localization, euclidianSettings));
		properties.add(new AxisDistanceProperty(localization, euclidianSettings, euclidianView,
				kernel, "x", 0));
		properties.add(new AxisDistanceProperty(localization, euclidianSettings, euclidianView,
				kernel, "y", 1));
		setProperties(properties.toArray(new Property[0]));
	}
}
