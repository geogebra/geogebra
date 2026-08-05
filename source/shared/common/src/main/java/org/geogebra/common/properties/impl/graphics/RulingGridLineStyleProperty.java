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
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.properties.IconsEnumeratedProperty;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.impl.AbstractEnumeratedProperty;
import org.jspecify.annotations.Nullable;

/**
 * {@code Property} responsible for changing the line style of the ruling grid in Notes.
 * @apiNote For other apps {@link GridLineStyleProperty} is used instead.
 */
public class RulingGridLineStyleProperty extends AbstractEnumeratedProperty<Integer>
		implements IconsEnumeratedProperty<Integer>, SettingsDependentProperty {
	private final EuclidianSettings euclidianSettings;
	private static final PropertyResource[] icons =
			EuclidianStyleConstants.lineStyleIcons.toArray(new PropertyResource[0]);

	/**
	 * Constructs the property.
	 * @param localization localization
	 * @param euclidianSettings euclidian settings
	 */
	public RulingGridLineStyleProperty(Localization localization,
			EuclidianSettings euclidianSettings) {
		super(localization, "LineStyle");
		this.euclidianSettings = euclidianSettings;
		setValues(EuclidianStyleConstants.lineStyleList);
	}

	@Override
	public PropertyResource[] getValueIcons() {
		return icons;
	}

	@Override
	public @Nullable String[] getToolTipLabels() {
		return null;
	}

	@Override
	protected void doSetValue(Integer value) {
		euclidianSettings.setRulerLineStyle(value);
	}

	@Override
	public Integer getValue() {
		return euclidianSettings.getRulerLineStyle();
	}

	@Override
	public boolean isAvailable() {
		BackgroundType backgroundType = euclidianSettings.getBackgroundType();
		return backgroundType == BackgroundType.RULER
				|| backgroundType == BackgroundType.SQUARE_SMALL
				|| backgroundType == BackgroundType.SQUARE_BIG;
	}

	@Override
	public AbstractSettings<?> getSettings() {
		return euclidianSettings;
	}
}
