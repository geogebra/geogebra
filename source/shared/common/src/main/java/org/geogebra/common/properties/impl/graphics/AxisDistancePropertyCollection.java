package org.geogebra.common.properties.impl.graphics;

import java.util.ArrayList;

import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.impl.collections.AbstractPropertyCollection;

/**
 * This collection groups properties that are related to the distances of axis.
 */
public class AxisDistancePropertyCollection extends AbstractPropertyCollection<Property> {

	/**
	 * Constructs an axis distance property collection.
	 * @param app application
	 * @param localization localization for the title
	 * @param euclidianSettings EV settings
	 * @param axis axis number
	 * @param euclidianView euclidian view
	 */
	public AxisDistancePropertyCollection(App app, Localization localization, EuclidianSettings
			euclidianSettings, int axis, EuclidianViewInterfaceCommon euclidianView) {
		super(localization, "Distance");

		ArrayList<Property> properties = new ArrayList<>();
		properties.add(new AxisFixedDistanceProperty(localization, euclidianSettings, axis,
				euclidianView));
		properties.add(new AxisDistanceProperty(localization, euclidianSettings, euclidianView,
				app.getKernel(), "Distance", axis));
		setProperties(properties.toArray(new Property[0]));
	}
}
