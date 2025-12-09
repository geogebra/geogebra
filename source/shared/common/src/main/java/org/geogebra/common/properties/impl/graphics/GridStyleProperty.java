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

import static java.util.Map.entry;

import java.util.List;

import javax.annotation.CheckForNull;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.IconsEnumeratedProperty;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;

/**
 * This property controls the style of the grid.
 */
public class GridStyleProperty extends AbstractNamedEnumeratedProperty<Integer>
		implements IconsEnumeratedProperty<Integer> {

	private final EuclidianSettings euclidianSettings;

	private static final PropertyResource[] icons = new PropertyResource[]{
			PropertyResource.ICON_CARTESIAN,
			PropertyResource.ICON_CARTESIAN_MINOR, PropertyResource.ICON_POLAR,
			PropertyResource.ICON_ISOMETRIC, PropertyResource.ICON_DOTS};

	/**
	 * Controls a grid style property.
	 * @param localization localization for the title
	 * @param euclidianSettings euclidian settings.
	 */
	public GridStyleProperty(Localization localization, EuclidianSettings euclidianSettings) {
		super(localization, "GridType");
		this.euclidianSettings = euclidianSettings;
		setNamedValues(List.of(
				entry(EuclidianView.GRID_CARTESIAN, "Grid.Major"),
				entry(EuclidianView.GRID_CARTESIAN_WITH_SUBGRID, "Grid.MajorAndMinor"),
				entry(EuclidianView.GRID_POLAR, "Polar"),
				entry(EuclidianView.GRID_ISOMETRIC, "Isometric"),
				entry(EuclidianView.GRID_DOTS, "Dots")));
	}

	@Override
	public Integer getValue() {
		return euclidianSettings.getGridType();
	}

	@Override
	protected void doSetValue(Integer value) {
		euclidianSettings.setGridType(value);
	}

	@Override
	public boolean isEnabled() {
		return euclidianSettings.getShowGrid();
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
