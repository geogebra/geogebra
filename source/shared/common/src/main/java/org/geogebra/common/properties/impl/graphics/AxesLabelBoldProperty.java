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

import static org.geogebra.common.properties.PropertyResource.ICON_BOLD;

import org.geogebra.common.awt.GFont;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.IconAssociatedProperty;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;

public class AxesLabelBoldProperty extends AbstractValuedProperty<Boolean>
		implements BooleanProperty, IconAssociatedProperty {
	private EuclidianSettings euclidianSettings;

	/**
	 * Create an axes label bold property.
	 * @param localization localization
	 * @param euclidianSettings EV settings
	 */
	public AxesLabelBoldProperty(Localization localization, EuclidianSettings euclidianSettings) {
		super(localization, "Bold");
		this.euclidianSettings = euclidianSettings;
	}

	@Override
	protected void doSetValue(Boolean value) {
		int style = euclidianSettings.getAxisFontStyle();
		if (value) {
			style = style | GFont.BOLD;
		} else {
			style = style & ~GFont.BOLD;
		}
		euclidianSettings.setAxisFontStyle(style);
	}

	@Override
	public Boolean getValue() {
		int style = euclidianSettings.getAxisFontStyle();
		return (style & GFont.BOLD) > 0;
	}

	@Override
	public PropertyResource getIcon() {
		return ICON_BOLD;
	}
}
