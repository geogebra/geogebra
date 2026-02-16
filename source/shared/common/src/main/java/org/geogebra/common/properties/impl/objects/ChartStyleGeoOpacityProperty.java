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

import org.geogebra.common.kernel.geos.ChartStyleGeo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractRangeProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * {@code Property} responsible for setting the opacity of individual bars and slices
 * in bar and pie charts.
 */
public class ChartStyleGeoOpacityProperty extends AbstractRangeProperty<Integer>
		implements ChartSegmentSelectionDependentProperty {
	private final ChartStyleGeo chartStyleGeo;
	private final ChartSegmentSelection chartSegmentSelection;

	/**
	 * Constructs the opacity property for chart style geos
	 * @param localization localization for translating property names
	 * @param geoElement the element to create the property for
	 * @param chartSegmentSelection the selection from which to read the selected bar/slice index
	 * @throws NotApplicablePropertyException if the property is not applicable for the given element
	 */
	public ChartStyleGeoOpacityProperty(Localization localization, GeoElement geoElement,
			ChartSegmentSelection chartSegmentSelection)
			throws NotApplicablePropertyException {
		super(localization, "Opacity", 0, 100, 5);
		if (!(geoElement instanceof ChartStyleGeo chartStyleGeo)) {
			throw new NotApplicablePropertyException(geoElement);
		}
		this.chartStyleGeo = chartStyleGeo;
		this.chartSegmentSelection = chartSegmentSelection;
	}

	@Override
	protected void setValueSafe(Integer value) {
		chartSegmentSelection.forEachSelectedSegment(chartStyleGeo.getIntervals(),
				index -> chartStyleGeo.getStyle().setBarAlpha(value / 100d, index));
		((GeoElement) chartStyleGeo).getKernel().notifyRepaint();
	}

	@Override
	public Integer getValue() {
		return chartSegmentSelection.getFirstValue(chartStyleGeo.getIntervals(), this::getOpacity);
	}

	@Override
	public boolean isValueDisplayedAsPercentage() {
		return true;
	}

	@Override
	public ChartSegmentSelection getChartSegmentSelection() {
		return chartSegmentSelection;
	}

	private int getOpacity(int barIndex) {
		double internalAlpha = chartStyleGeo.getStyle().getBarAlpha(barIndex);
		if (internalAlpha == -1) {
			return 100;
		}
		return (int) Math.round(internalAlpha * 100);
	}
}
