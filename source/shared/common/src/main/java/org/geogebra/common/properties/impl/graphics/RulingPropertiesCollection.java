package org.geogebra.common.properties.impl.graphics;

import java.util.ArrayList;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.impl.collections.AbstractPropertyCollection;

public class RulingPropertiesCollection extends AbstractPropertyCollection<Property> {

	/**
	 * Creates a property collection for graphics ruling
	 * @param localization localization
	 * @param settings euclidian settings
	 * @param euclidianView euclidian view
	 */
	public RulingPropertiesCollection(Localization localization, EuclidianSettings settings,
			EuclidianView euclidianView) {
		super(localization, "Ruling");
		ArrayList<Property> properties = new ArrayList<>();
		properties.add(new RulingStyleProperty(localization, settings, euclidianView));
		properties.add(new GridColorProperty(localization, settings));
		properties.add(new RulingLineStyleProperty(localization, settings));
		properties.add(new RulingBoldProperty(localization, settings));
		setProperties(properties.toArray(new Property[0]));
	}
}
