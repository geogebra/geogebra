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

import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;

/**
 * {@code Property} responsible for applying bold style to the euclidian view's grid.
 * @apiNote For Notes {@link RulingGridBoldProperty} is used instead.
 */
public class GridBoldProperty extends AbstractValuedProperty<Boolean>
		implements BooleanProperty, SettingsDependentProperty {
	private final EuclidianSettings euclidianSettings;

	/**
	 * Constructs the property.
	 * @param localization localization for translating label names
	 * @param euclidianSettings the euclidian settings
	 */
	public GridBoldProperty(Localization localization, EuclidianSettings euclidianSettings) {
		super(localization, "Bold");
		this.euclidianSettings = euclidianSettings;
	}

	@Override
	protected void doSetValue(Boolean value) {
		euclidianSettings.setGridIsBold(value);
	}

	@Override
	public Boolean getValue() {
		return euclidianSettings.getGridIsBold();
	}

	@Override
	public AbstractSettings<?> getSettings() {
		return euclidianSettings;
	}
}
