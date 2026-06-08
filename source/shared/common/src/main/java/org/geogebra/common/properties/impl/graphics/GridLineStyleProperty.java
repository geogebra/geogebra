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

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.properties.IconsEnumeratedProperty;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.impl.AbstractEnumeratedProperty;

/**
 * {@code Property} responsible for changing the line style of the euclidian view's grid.
 * @apiNote For Notes {@link RulingGridLineStyleProperty} is used instead.
 */
public class GridLineStyleProperty extends AbstractEnumeratedProperty<Integer>
		implements IconsEnumeratedProperty<Integer>, SettingsDependentProperty {
	private final EuclidianSettings euclidianSettings;
	private static final PropertyResource[] icons =
			EuclidianStyleConstants.lineStyleIcons.toArray(new PropertyResource[0]);

	/**
	 * Creates a line style property for grid lines.
	 * @param localization localization
	 * @param euclidianSettings euclidian settings
	 */
	public GridLineStyleProperty(Localization localization, EuclidianSettings euclidianSettings) {
		super(localization, "LineStyle");
		this.euclidianSettings = euclidianSettings;
		setValues(EuclidianStyleConstants.lineStyleList);
	}

	@Override
	public PropertyResource[] getValueIcons() {
		return icons;
	}

	@Override
	public @CheckForNull String[] getToolTipLabels() {
		return null;
	}

	@Override
	protected void doSetValue(Integer value) {
		euclidianSettings.setGridLineStyle(value);
	}

	@Override
	public Integer getValue() {
		return euclidianSettings.getGridLineStyle();
	}

	@Override
	public boolean isAvailable() {
		return euclidianSettings.getGridType() != EuclidianView.GRID_DOTS;
	}

	@Override
	public AbstractSettings<?> getSettings() {
		return euclidianSettings;
	}
}
