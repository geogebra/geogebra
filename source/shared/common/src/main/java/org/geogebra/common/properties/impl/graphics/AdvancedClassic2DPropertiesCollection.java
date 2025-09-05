package org.geogebra.common.properties.impl.graphics;

import java.util.ArrayList;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.main.settings.LabelVisibility;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.impl.collections.AbstractPropertyCollection;
import org.geogebra.common.properties.impl.general.LabelingProperty;

/**
 * Collection of advanced settings for 3d graphics view in classic.
 */
public class AdvancedClassic2DPropertiesCollection extends AbstractPropertyCollection<Property> {
	/**
	 * @param localization localization
	 * @param settings euclidian settings
	 * @param euclidianView euclidian view
	 */
	public AdvancedClassic2DPropertiesCollection(App app, Localization localization,
			EuclidianSettings settings, EuclidianView euclidianView) {
		super(localization, "Advanced");

		ArrayList<Property> properties = new ArrayList<>();
		properties.add(new BackgroundColorProperty(localization, settings));
		properties.add(new RightAngleStyleProperty(localization, app));
		properties.add(new PointCapturingProperty(localization, euclidianView));
		properties.add(app.isUnbundledOrWhiteboard()
				? new LabelingProperty(app.getLocalization(), app.getSettings().getLabelSettings())
				: new LabelingProperty(app.getLocalization(), app.getSettings().getLabelSettings(),
				LabelVisibility.Automatic, LabelVisibility.AlwaysOn, LabelVisibility.AlwaysOff,
				LabelVisibility.PointsOnly));
		properties.add(new TooltipProperty(localization, settings, euclidianView));
		properties.add(new ShowMouseCoordinatesProperty(localization, settings));
		properties.add(new NavigationBarPropertiesCollection(localization, app,
				euclidianView.getViewID()));
		setProperties(properties.toArray(new Property[0]));
	}
}
