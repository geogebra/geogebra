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
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.impl.collections.AbstractPropertyCollection;

public class RulingPropertiesCollection extends AbstractPropertyCollection<Property>
	implements SettingsDependentProperty {

	private final EuclidianSettings evSettings;

	/**
	 * Creates a property collection for graphics ruling
	 * @param localization localization
	 * @param settings euclidian settings
	 * @param euclidianView euclidian view
	 */
	public RulingPropertiesCollection(Localization localization, EuclidianSettings settings,
			EuclidianView euclidianView) {
		super(localization, "Ruling");
		this.evSettings = settings;
		ArrayList<Property> properties = new ArrayList<>();
		properties.add(new RulingStyleProperty(localization, settings, euclidianView));
		properties.add(new RulingGridColorProperty(localization, settings, true));
		properties.add(new RulingGridLineStyleProperty(localization, settings, true));
		properties.add(new RulingGridBoldProperty(localization, settings));
		setProperties(properties.toArray(new Property[0]));
	}

	@Override
	public AbstractSettings getSettings() {
		return evSettings;
	}
}
