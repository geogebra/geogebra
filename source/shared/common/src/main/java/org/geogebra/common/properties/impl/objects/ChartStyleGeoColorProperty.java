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

package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.kernel.geos.ChartStyleGeo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.ColorProperty;
import org.geogebra.common.properties.impl.AbstractEnumeratedProperty;
import org.geogebra.common.properties.impl.DefaultColorValues;
import org.geogebra.common.properties.impl.objects.ChartSegmentSelectionProperty.ChartSegmentSelection;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * {@code Property} responsible for setting the color of individual bars and slices
 * in bar and pie charts.
 */
public class ChartStyleGeoColorProperty extends AbstractEnumeratedProperty<GColor>
		implements ColorProperty, ChartSegmentSelectionDependentProperty {
	private final ChartStyleGeo chartStyleGeo;
	private final ChartSegmentSelection chartSegmentSelection;

	/**
	 * Constructs the color property for chart style geos.
	 * @param localization localization for translating property names
	 * @param geoElement the element to create the property for
	 * @param chartSegmentSelection the selection owner from which to read the selected bar/slice
	 * @throws NotApplicablePropertyException if the property is not applicable for the given element
	 */
	public ChartStyleGeoColorProperty(Localization localization, GeoElement geoElement,
			ChartSegmentSelection chartSegmentSelection)
			throws NotApplicablePropertyException {
		super(localization, "Color");
		if (!(geoElement instanceof ChartStyleGeo chartStyleGeo)) {
			throw new NotApplicablePropertyException(geoElement);
		}
		this.chartStyleGeo = chartStyleGeo;
		this.chartSegmentSelection = chartSegmentSelection;
		setValues(DefaultColorValues.BRIGHT);
	}

	@Override
	protected void doSetValue(GColor value) {
		if (chartSegmentSelection.getIndex() == 0) {
			for (int selection = 1; selection < chartStyleGeo.getIntervals() + 1; selection++) {
				chartStyleGeo.getStyle().setBarColor(value, selection);
			}
		} else {
			chartStyleGeo.getStyle().setBarColor(value, chartSegmentSelection.getIndex());
		}
		((GeoElement) chartStyleGeo).getKernel().notifyRepaint();
	}

	@Override
	public GColor getValue() {
		if (chartSegmentSelection.getIndex() == 0) {
			GColor firstColor = chartStyleGeo.getStyle().getBarColor(1);
			for (int selection = 2; selection < chartStyleGeo.getIntervals() + 1; selection++) {
				if (chartStyleGeo.getStyle().getBarColor(selection) != firstColor) {
					return null;
				}
			}
			return firstColor;
		}
		return chartStyleGeo.getStyle().getBarColor(chartSegmentSelection.getIndex());
	}

	@Override
	public ChartSegmentSelection getChartSegmentSelection() {
		return chartSegmentSelection;
	}
}
