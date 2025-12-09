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

import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;

public class AxisFixedDistanceProperty extends AbstractValuedProperty<Boolean>
		implements BooleanProperty {
	private EuclidianSettings euclidianSettings;
	private final int axis;
	private EuclidianViewInterfaceCommon euclidianView;

	/** Creates an axis fixed distance property.
	 * @param localization localization
	 * @param euclidianSettings EV settings
	 * @param axis axis index
	 * @param euclidianView euclidian view
	 */
	public AxisFixedDistanceProperty(Localization localization,
			EuclidianSettings euclidianSettings, int axis,
			EuclidianViewInterfaceCommon euclidianView) {
		super(localization, "SettingsView.FixedDistance");
		this.euclidianSettings = euclidianSettings;
		this.axis = axis;
		this.euclidianView = euclidianView;
	}

	@Override
	protected void doSetValue(Boolean value) {
		euclidianSettings.setAutomaticAxesNumberingDistance(!value, axis, true);
		euclidianView.updateBackground();
	}

	@Override
	public Boolean getValue() {
		return !euclidianSettings.getAutomaticAxesNumberingDistance(axis);
	}
}
