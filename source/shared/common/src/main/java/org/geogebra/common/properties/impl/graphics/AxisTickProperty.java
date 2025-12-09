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

import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.properties.IconsEnumeratedProperty;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.impl.AbstractEnumeratedProperty;

/**
 * This property controls the tick style of axis.
 */
public class AxisTickProperty extends AbstractEnumeratedProperty<Integer>
		implements IconsEnumeratedProperty<Integer> {
	private EuclidianSettings euclidianSettings;
	private final int axis;
	private EuclidianViewInterfaceCommon euclidianView;

	private PropertyResource[] icons = new PropertyResource[]{
			PropertyResource.ICON_AXIS_TICK_MAJOR, PropertyResource.ICON_AXIS_TICK_MAJOR_AND_MINOR,
			PropertyResource.ICON_AXIS_TICK_OFF};

	/**
	 * Controls a tick style property.
	 * @param localization localization for the title
	 * @param euclidianSettings euclidian settings
	 * @param axis axis index
	 * @param euclidianView euclidian view
	 */
	public AxisTickProperty(Localization localization, EuclidianSettings euclidianSettings,
			int axis, EuclidianViewInterfaceCommon euclidianView) {
		super(localization, "AxisTicks");
		this.euclidianSettings = euclidianSettings;
		this.euclidianView = euclidianView;
		this.axis = axis;
		setValues(List.of(EuclidianStyleConstants.AXES_TICK_STYLE_MAJOR,
				EuclidianStyleConstants.AXES_TICK_STYLE_MAJOR_MINOR,
				EuclidianStyleConstants.AXES_TICK_STYLE_NONE));
	}

	@Override
	public Integer getValue() {
		return euclidianSettings.getAxesTickStyles()[axis];
	}

	@Override
	protected void doSetValue(Integer value) {
		euclidianSettings.setAxisTickStyle(axis, value);
		euclidianView.updateBackground();
	}

	@Override
	public PropertyResource[] getValueIcons() {
		return icons;
	}

	@Override
	public @CheckForNull String[] getToolTipLabels() {
		return null;
	}
}
