/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.properties.impl.graphics;

import java.util.ArrayList;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.main.settings.LabelVisibility;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.impl.collections.AbstractPropertyCollection;
import org.geogebra.common.properties.impl.general.LabelingProperty;

/**
 * Collection of advanced settings for a graphics view.
 */
public class AdvancedClassic3DPropertiesCollection extends AbstractPropertyCollection<Property> {
	/**
	 * @param localization localization
	 * @param settings euclidian settings
	 * @param euclidianView euclidian view
	 */
	public AdvancedClassic3DPropertiesCollection(App app, Localization localization,
			EuclidianSettings settings, EuclidianView3D euclidianView) {
		super(localization, "Advanced");
		ArrayList<Property> properties = new ArrayList<>();

		properties.add(new BackgroundColorProperty(localization, settings));
		properties.add(new PointCapturingProperty(localization,
				app.getEuclidianView3D()));
		properties.add(app.isUnbundledOrWhiteboard()
				? new LabelingProperty(app.getLocalization(), app.getSettings().getLabelSettings())
				: new LabelingProperty(app.getLocalization(), app.getSettings().getLabelSettings(),
				LabelVisibility.Automatic, LabelVisibility.AlwaysOn, LabelVisibility.AlwaysOff,
				LabelVisibility.PointsOnly));
		properties.add(new ViewDirectionProperty(localization, euclidianView));
		properties.add(new ClippingPropertyCollection(localization, euclidianView));
		properties.add(new Dimension3DPropertiesCollection(app, localization, settings));
		properties.add(new PlaneVisibilityProperty(localization, euclidianView.getSettings()));
		properties.add(new UseLightingBooleanProperty(localization, euclidianView.getSettings(),
				euclidianView));
		properties.add(new NavigationBarPropertiesCollection(localization, app,
				euclidianView.getViewID(), settings));
		setProperties(properties.toArray(new Property[0]));
	}
}
