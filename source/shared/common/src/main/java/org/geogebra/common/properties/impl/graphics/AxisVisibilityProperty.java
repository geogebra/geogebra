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

import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;

/**
 * This property controls the visibility of a single axis.
 */
public class AxisVisibilityProperty extends AbstractValuedProperty<Boolean>
		implements BooleanProperty, SettingsDependentProperty {

	private final int axisIndex;
	private final EuclidianSettings euclidianSettings;

	/**
	 * Constructs an AxesVisibility property.
	 * @param localization localization for the name
	 * @param euclidianSettings euclidian settings
	 * @param index The index of the associated axis
	 * @param label The label used for this property
	 */
	public AxisVisibilityProperty(Localization localization,
			EuclidianSettings euclidianSettings, int index, String label) {
		super(localization, label);
		this.axisIndex = index;
		this.euclidianSettings = euclidianSettings;
	}

	@Override
	public Boolean getValue() {
		return euclidianSettings.getShowAxes()[axisIndex];
	}

	@Override
	public void doSetValue(Boolean value) {
		euclidianSettings.setShowAxis(axisIndex, value);
	}

	@Override
	public EuclidianSettings getSettings() {
		return euclidianSettings;
	}
}
