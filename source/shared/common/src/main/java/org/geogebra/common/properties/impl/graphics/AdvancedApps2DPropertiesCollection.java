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

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.main.settings.LabelVisibility;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.impl.collections.AbstractPropertyCollection;
import org.geogebra.common.properties.impl.general.LabelingProperty;

/**
 * Collection of advanced settings for 2d graphics view in apps.
 */
public class AdvancedApps2DPropertiesCollection extends AbstractPropertyCollection<Property> {
	/**
	 * @param localization localization
	 * @param settings euclidian settings
	 */
	public AdvancedApps2DPropertiesCollection(App app, Localization localization,
			EuclidianSettings settings, EuclidianView view) {
		super(localization, "Advanced");

		ArrayList<Property> properties = new ArrayList<>();
		properties.add(new BackgroundColorProperty(localization, settings));
		properties.add(new RightAngleStyleProperty(localization, app));
		properties.add(new PointCapturingProperty(localization, view));
		properties.add(app.isUnbundledOrWhiteboard()
				? new LabelingProperty(app.getLocalization(), app.getSettings().getLabelSettings())
				: new LabelingProperty(app.getLocalization(), app.getSettings().getLabelSettings(),
				LabelVisibility.Automatic, LabelVisibility.AlwaysOn, LabelVisibility.AlwaysOff,
				LabelVisibility.PointsOnly));
		setProperties(properties.toArray(new Property[0]));
	}
}
