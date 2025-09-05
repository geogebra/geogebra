package org.geogebra.common.properties.impl.graphics;

import java.util.ArrayList;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.main.settings.LabelVisibility;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.impl.collections.AbstractPropertyCollection;
import org.geogebra.common.properties.impl.general.LabelingProperty;

/**
 * Collection of advanced settings for 3d graphics view in apps.
 */
public class AdvancedApps3DPropertiesCollection extends AbstractPropertyCollection<Property> {
	/**
	 * @param localization localization
	 * @param settings euclidian settings
	 */
	public AdvancedApps3DPropertiesCollection(App app, Localization localization,
			EuclidianSettings settings) {
		super(localization, "Advanced");
		ArrayList<Property> properties = new ArrayList<>();

		properties.add(new BackgroundColorProperty(localization, settings));
		properties.add(new RightAngleStyleProperty(localization, app));
		properties.add(new PointCapturingProperty(localization, app.getEuclidianView3D()));
		properties.add(app.isUnbundledOrWhiteboard()
				? new LabelingProperty(app.getLocalization(), app.getSettings().getLabelSettings())
				: new LabelingProperty(app.getLocalization(), app.getSettings().getLabelSettings(),
				LabelVisibility.Automatic, LabelVisibility.AlwaysOn, LabelVisibility.AlwaysOff,
				LabelVisibility.PointsOnly));
		setProperties(properties.toArray(new Property[0]));
	}
}
