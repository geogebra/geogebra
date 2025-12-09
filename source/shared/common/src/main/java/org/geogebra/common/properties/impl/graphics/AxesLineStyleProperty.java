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

import java.util.List;

import javax.annotation.CheckForNull;

import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.properties.IconsEnumeratedProperty;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.impl.AbstractEnumeratedProperty;

public class AxesLineStyleProperty extends AbstractEnumeratedProperty<Integer>
		implements IconsEnumeratedProperty<Integer> {
	private EuclidianSettings euclidianSettings;
	private PropertyResource[] icons = new PropertyResource[]{
			PropertyResource.ICON_AXES_LINE_TYPE_ARROW,
			PropertyResource.ICON_AXES_LINE_TYPE_ARROW_FILLED,
			PropertyResource.ICON_AXES_LINE_TYPE_TWO_ARROWS,
			PropertyResource.ICON_AXES_LINE_TYPE_TWO_ARROWS_FILLED,
			PropertyResource.ICON_AXES_LINE_TYPE_FULL};

	/**
	 * Creates a property for axes line style
	 * @param localization localization
	 * @param euclidianSettings euclidian settings
	 */
	public AxesLineStyleProperty(Localization localization, EuclidianSettings euclidianSettings) {
		super(localization, "LineStyle");
		this.euclidianSettings = euclidianSettings;
		setValues(List.of(EuclidianStyleConstants.AXES_LINE_TYPE_ARROW,
				EuclidianStyleConstants.AXES_LINE_TYPE_ARROW_FILLED,
				EuclidianStyleConstants.AXES_LINE_TYPE_TWO_ARROWS,
				EuclidianStyleConstants.AXES_LINE_TYPE_TWO_ARROWS_FILLED,
				EuclidianStyleConstants.AXES_LINE_TYPE_FULL));
	}

	@Override
	public PropertyResource[] getValueIcons() {
		return icons;
	}

	@Override
	protected void doSetValue(Integer value) {
		euclidianSettings.setAxesLineStyle(value);
	}

	@Override
	public Integer getValue() {
		return euclidianSettings.getAxesLineStyle();
	}

	@Override
	public @CheckForNull String[] getToolTipLabels() {
		return null;
	}
}
