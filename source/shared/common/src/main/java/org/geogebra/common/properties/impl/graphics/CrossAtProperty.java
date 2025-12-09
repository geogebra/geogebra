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

import javax.annotation.CheckForNull;

import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.aliases.StringProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;

/**
 * This property controls the cross point of axis
 */
public class CrossAtProperty extends AbstractValuedProperty<String>
		implements StringProperty, SettingsDependentProperty {
	private final EuclidianSettings euclidianSettings;
	private final EuclidianViewInterfaceCommon euclidianView;
	private final int axis;

	/**
	 * Constructs an axis cross point property.
	 * @param localization localization for the title
	 * @param euclidianSettings euclidian settings
	 * @param euclidianView the active euclidian view
	 * @param axis the axis for the numbering distance will be set
	 */
	public CrossAtProperty(Localization localization, EuclidianSettings euclidianSettings,
			EuclidianViewInterfaceCommon euclidianView, int axis) {
		super(localization, "CrossAt");
		this.euclidianSettings = euclidianSettings;
		this.euclidianView = euclidianView;
		this.axis = axis;
	}

	@Override
	protected void doSetValue(String value) {
		String str = value;
		if ("".equals(str)) {
			str = "0";
		}
		double cross = Double.parseDouble(str);
		if (!(Double.isInfinite(cross) || Double.isNaN(cross))) {
			euclidianSettings.setAxisCross(axis, cross);
		}

		euclidianView.updateBackground();
	}

	@Override
	public String getValue() {
		return String.valueOf(euclidianSettings.getAxesCross()[axis]);
	}

	@Override
	public boolean isEnabled() {
		return !euclidianSettings.getDrawBorderAxes()[axis];
	}

	@Override
	public @CheckForNull String validateValue(String value) {
		return null;
	}

	@Override
	public AbstractSettings getSettings() {
		return euclidianSettings;
	}
}
