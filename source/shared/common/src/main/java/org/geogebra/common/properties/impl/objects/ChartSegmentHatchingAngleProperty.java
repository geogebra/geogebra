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

import static org.geogebra.common.properties.impl.objects.HatchingAngleProperty.getHatchingAngleStep;
import static org.geogebra.common.properties.impl.objects.HatchingAngleProperty.getMaxHatchingAngleValue;
import static org.geogebra.common.properties.impl.objects.HatchingAngleProperty.supportedHatchingAngleFillTypes;

import java.util.Objects;

import javax.annotation.CheckForNull;

import org.geogebra.common.kernel.geos.ChartStyleGeo;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractRangeProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * {@code Property} responsible for setting the hatching angle
 * used for pattern-style fillings that support hatching in pie and bar charts.
 */
public final class ChartSegmentHatchingAngleProperty extends AbstractRangeProperty<Integer>
		implements GeoElementDependentProperty, ChartSegmentSelectionDependentProperty {
	private final ChartStyleGeo chartStyleGeo;
	private final ChartSegmentSelection chartSegmentSelection;

	/**
	 * Constructs the property.
	 * @param localization localization for translating property names
	 * @param geoElement the element to create the property for
	 * @param chartSegmentSelection the selection from which to read the selected bar/slice index
	 * @throws NotApplicablePropertyException if the property is not applicable for the given element
	 */
	public ChartSegmentHatchingAngleProperty(Localization localization, GeoElement geoElement,
			ChartSegmentSelection chartSegmentSelection) throws NotApplicablePropertyException {
		super(localization, "Angle", 0, null, null);
		if (!(geoElement instanceof ChartStyleGeo chartStyleGeo)) {
			throw new NotApplicablePropertyException(geoElement);
		}
		this.chartStyleGeo = chartStyleGeo;
		this.chartSegmentSelection = chartSegmentSelection;
	}

	@Override
	protected void setValueSafe(Integer value) {
		chartSegmentSelection.forEachSelectedSegment(chartStyleGeo.getIntervals(),
				index -> chartStyleGeo.getStyle().setBarHatchAngle(value, index));
		((GeoElement) chartStyleGeo).updateVisualStyleRepaint(GProperty.HATCHING);
	}

	@Override
	public Integer getValue() {
		return chartSegmentSelection.getFirstValue(chartStyleGeo.getIntervals(),
				this::getSegmentHatchingAngle);
	}

	private int getSegmentHatchingAngle(int index) {
		int rawAngle = chartStyleGeo.getStyle().getBarHatchAngle(index);
		if (rawAngle < 0 || rawAngle > 180) {
			return 0;
		}
		return rawAngle;
	}

	@Override
	public boolean isAvailable() {
		return  Boolean.TRUE.equals(chartSegmentSelection.getUniformValueOrNull(
				chartStyleGeo.getIntervals(), index -> supportedHatchingAngleFillTypes.contains(
						chartStyleGeo.getStyle().getBarFillType(index))));
	}

	@Override
	public Integer getMax() {
		return chartSegmentSelection.mapSelectedSegments(chartStyleGeo.getIntervals(),
				index -> getMaxHatchingAngleValue(chartStyleGeo.getStyle().getBarFillType(index)))
				.filter(Objects::nonNull)
				.min(Integer::compareTo).orElse(null);
	}

	@Override
	public @CheckForNull Integer getStep() {
		return chartSegmentSelection.mapSelectedSegments(chartStyleGeo.getIntervals(),
				index -> getHatchingAngleStep(chartStyleGeo.getStyle().getBarFillType(index)))
				.filter(Objects::nonNull)
				.max(Integer::compareTo).orElse(null);
	}

	@Override
	public GeoElement getGeoElement() {
		return (GeoElement) chartStyleGeo;
	}

	@Override
	public ChartSegmentSelection getChartSegmentSelection() {
		return chartSegmentSelection;
	}
}
