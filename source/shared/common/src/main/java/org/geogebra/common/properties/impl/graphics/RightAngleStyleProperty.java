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

import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.properties.IconsEnumeratedProperty;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.impl.AbstractEnumeratedProperty;

public class RightAngleStyleProperty extends AbstractEnumeratedProperty<Integer>
	implements IconsEnumeratedProperty<Integer> {
	private final App app;

	private PropertyResource[] icons = new PropertyResource[]{
			PropertyResource.ICON_RIGHT_ANGLE_STYLE_NONE,
			PropertyResource.ICON_RIGHT_ANGLE_STYLE_SQUARE,
			PropertyResource.ICON_RIGHT_ANGLE_STYLE_DOT,
			PropertyResource.ICON_RIGHT_ANGLE_STYLE_L};

	/**
	 * Create a right angle style icon property
	 * @param localization localization
	 * @param app application
	 */
	public RightAngleStyleProperty(Localization localization, App app) {
		super(localization, "RightAngleStyle");
		this.app = app;
		setValues(List.of(EuclidianStyleConstants.RIGHT_ANGLE_STYLE_NONE,
				EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE,
				EuclidianStyleConstants.RIGHT_ANGLE_STYLE_DOT,
				EuclidianStyleConstants.RIGHT_ANGLE_STYLE_L));
	}

	@Override
	protected void doSetValue(Integer value) {
		app.setRightAngleStyle(value);
		app.getEuclidianView1().updateAllDrawables(true);
	}

	@Override
	public Integer getValue() {
		return app.rightAngleStyle;
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
