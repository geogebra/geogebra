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
 * This property controls the visibility of the axis labels.
 */
public class AxesLabelsVisibilityProperty extends AbstractValuedProperty<Boolean>
		implements BooleanProperty {

	private EuclidianSettings euclidianSettings;

	/**
	 * Constructs an Axes visibility property.
	 * @param localization localization for the title
	 * @param euclidianSettings euclidian settings
	 */
	public AxesLabelsVisibilityProperty(Localization localization,
			EuclidianSettings euclidianSettings) {
		super(localization, "Show");
		this.euclidianSettings = euclidianSettings;
	}

	@Override
	public Boolean getValue() {
		String[] axesLabels = euclidianSettings.getAxesLabels();
		boolean value = false;
		for (int i = 0; i < euclidianSettings.getDimension(); i++) {
			value |= axesLabels[i] != null;
		}
		return value;
	}

	@Override
	public void doSetValue(Boolean value) {
		int length = euclidianSettings.getDimension();
		for (int i = 0; i < length; i++) {
			euclidianSettings.setAxisLabel(i,
					value ? EuclidianSettings.getDefaultAxisLabel(i) : null,
					i == length - 1);
		}
	}
}
