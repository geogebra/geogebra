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

import org.geogebra.common.euclidian3D.EuclidianView3DInterface;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.main.settings.EuclidianSettings3D;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.impl.collections.AbstractPropertyCollection;

public class ProjectionPropertyCollection extends AbstractPropertyCollection<Property>
	implements SettingsDependentProperty {

	private final EuclidianSettings3D euclidianSettings;

	/**
	 * Constructs a projection property collection.
	 * @param app application
	 * @param localization localization for the title
	 * @param euclidianSettings EV settings
	 */
	public ProjectionPropertyCollection(App app, Localization localization, EuclidianSettings3D
			euclidianSettings) {
		super(localization, "Projection");
		this.euclidianSettings = euclidianSettings;

		EuclidianView3DInterface euclidianView = app.getEuclidianView3D();
		ArrayList<Property> properties = new ArrayList<>();

		properties.add(new ProjectionsProperty(localization, euclidianView, euclidianSettings));
		properties.add(new DistanceFromScreenProperty(localization, euclidianSettings));
		properties.add(new DistanceBetweenEyesProperty(localization, euclidianSettings));
		properties.add(new GrayScaleProperty(localization, euclidianView));
		properties.add(new OmitGreenChannelProperty(localization, euclidianView));
		properties.add(new ObliqueAngleProperty(localization, euclidianView));
		properties.add(new ObliqueFactorProperty(localization, euclidianView));
		setProperties(properties.toArray(new Property[0]));
	}

	@Override
	public AbstractSettings getSettings() {
		return euclidianSettings;
	}
}
