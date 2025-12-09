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

import javax.annotation.Nonnull;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.background.BackgroundType;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.color.NeutralColorValues;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.aliases.ColorProperty;
import org.geogebra.common.properties.impl.AbstractEnumeratedProperty;

public class RulingGridColorProperty extends AbstractEnumeratedProperty<GColor>
		implements ColorProperty, SettingsDependentProperty {
	private final EuclidianSettings euclidianSettings;
	private final boolean isRuling;

	/**
	 * Creates a color property for grid lines
	 * @param loc localization
	 * @param euclidianSettings view settings
	 */
	public RulingGridColorProperty(Localization loc, EuclidianSettings euclidianSettings) {
		this(loc, euclidianSettings, false);
	}

	/**
	 * Creates a color property for ruling in notes
	 * @param loc localization
	 * @param euclidianSettings view settings
	 * @param isRuling ruling for notes
	 */
	public RulingGridColorProperty(Localization loc, EuclidianSettings euclidianSettings,
			boolean isRuling) {
		super(loc, "Color");
		this.euclidianSettings = euclidianSettings;
		this.isRuling = isRuling;
	}

	@Override
	protected void doSetValue(GColor value) {
		if (isRuling) {
			euclidianSettings.setBgRulerColor(value);
		} else {
			euclidianSettings.setGridColor(value);
		}
	}

	@Override
	public GColor getValue() {
		return isRuling ? euclidianSettings.getBgRulerColor() : euclidianSettings.getGridColor();
	}

	@Override
	public boolean isAvailable() {
		BackgroundType backgroundType = euclidianSettings.getBackgroundType();
		return backgroundType == BackgroundType.RULER || backgroundType
				== BackgroundType.SQUARE_SMALL || backgroundType == BackgroundType.SQUARE_BIG;
	}

	@Override
	public @Nonnull List<GColor> getValues() {
		return NeutralColorValues.values();
	}

	@Override
	public AbstractSettings getSettings() {
		return euclidianSettings;
	}
}
