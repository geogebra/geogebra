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

import org.geogebra.common.euclidian.background.BackgroundType;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;

public class RulingGridBoldProperty extends AbstractValuedProperty<Boolean>
		implements BooleanProperty, SettingsDependentProperty {
	private final EuclidianSettings euclidianSettings;

	/**
	 * Creates bold property for grid
	 * @param localization localization
	 * @param euclidianSettings euclidian settings
	 */
	public RulingGridBoldProperty(Localization localization, EuclidianSettings euclidianSettings) {
		super(localization, "Bold");
		this.euclidianSettings = euclidianSettings;
	}

	@Override
	protected void doSetValue(Boolean value) {
		if (euclidianSettings.getShowGrid()) {
			euclidianSettings.setGridIsBold(value);
		} else {
			euclidianSettings.setRulerBold(value);
		}
	}

	@Override
	public Boolean getValue() {
		return euclidianSettings.getShowGrid() ? euclidianSettings.getGridIsBold()
				: euclidianSettings.isRulerBold();
	}

	@Override
	public boolean isAvailable() {
		BackgroundType backgroundType = euclidianSettings.getBackgroundType();
		return backgroundType == BackgroundType.RULER
				|| backgroundType == BackgroundType.SQUARE_SMALL
				|| backgroundType == BackgroundType.SQUARE_BIG
				|| euclidianSettings.getShowGrid();
	}

	@Override
	public AbstractSettings getSettings() {
		return euclidianSettings;
	}
}
