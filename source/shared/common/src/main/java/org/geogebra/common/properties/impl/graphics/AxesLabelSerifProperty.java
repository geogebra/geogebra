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

import static org.geogebra.common.properties.PropertyResource.ICON_SERIF;

import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.ToggleableIconProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;

public class AxesLabelSerifProperty extends AbstractValuedProperty<Boolean>
		implements ToggleableIconProperty {
	private EuclidianSettings euclidianSettings;

	/**
	 * Create an axes label serif property.
	 * @param localization localization
	 * @param euclidianSettings EV settings
	 */
	public AxesLabelSerifProperty(Localization localization, EuclidianSettings euclidianSettings) {
		super(localization, "Serif");
		this.euclidianSettings = euclidianSettings;
	}

	@Override
	protected void doSetValue(Boolean value) {
		euclidianSettings.setAxesLabelsSerif(value);
	}

	@Override
	public Boolean getValue() {
		return euclidianSettings.getAxesLabelsSerif();
	}

	@Override
	public PropertyResource getIcon() {
		return ICON_SERIF;
	}
}
