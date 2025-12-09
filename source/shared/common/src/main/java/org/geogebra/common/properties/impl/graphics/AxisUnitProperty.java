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

import java.util.Arrays;
import java.util.List;

import javax.annotation.CheckForNull;

import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.impl.AbstractValuedProperty;
import org.geogebra.common.properties.util.StringPropertyWithSuggestions;
import org.geogebra.editor.share.util.Unicode;

/**
 * This property controls the unit of axis
 */
public class AxisUnitProperty extends AbstractValuedProperty<String>
		implements StringPropertyWithSuggestions, SettingsDependentProperty {
	private final EuclidianSettings euclidianSettings;
	private final EuclidianViewInterfaceCommon euclidianView;
	private final int axis;

	/**
	 * Constructs an axis unit property.
	 * @param localization localization for the title
	 * @param euclidianSettings euclidian settings
	 * @param euclidianView the active euclidian view
	 * @param axis the axis for the numbering distance will be set
	 */
	public AxisUnitProperty(Localization localization, EuclidianSettings euclidianSettings,
			EuclidianViewInterfaceCommon euclidianView, int axis) {
		super(localization, "AxisUnitLabel");
		this.euclidianSettings = euclidianSettings;
		this.euclidianView = euclidianView;
		this.axis = axis;
	}

	@Override
	public @CheckForNull String validateValue(String value) {
		return null;
	}

	@Override
	protected void doSetValue(String value) {
		euclidianSettings.setAxisUnitLabel(axis, value);
	}

	@Override
	public String getValue() {
		return euclidianSettings.getAxesUnitLabels()[axis];
	}

	@Override
	public boolean isEnabled() {
		return euclidianView.getShowAxesNumbers()[axis];
	}

	@Override
	public List<String> getSuggestions() {
		return Arrays.asList("",
				Unicode.DEGREE_STRING, // degrees
				Unicode.PI_STRING, // pi
				"mm",
				"cm",
				"m",
				"km",
				Unicode.CURRENCY_DOLLAR + "");
	}

	@Override
	public AbstractSettings getSettings() {
		return euclidianSettings;
	}
}
