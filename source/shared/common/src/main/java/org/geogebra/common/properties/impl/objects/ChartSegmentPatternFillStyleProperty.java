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

import static org.geogebra.common.properties.impl.objects.PatternFillStyleProperty.patternFillTypeIcons;
import static org.geogebra.common.properties.impl.objects.PatternFillStyleProperty.patternFillTypes;

import javax.annotation.CheckForNull;

import org.geogebra.common.kernel.geos.ChartStyleGeo;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.properties.FillType;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.IconsEnumeratedProperty;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.impl.AbstractEnumeratedProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * {@code Property} responsible for setting the pattern
 * used for pattern-style filling in pie and bar charts.
 */
public final class ChartSegmentPatternFillStyleProperty extends AbstractEnumeratedProperty<FillType>
		implements IconsEnumeratedProperty<FillType>, ChartSegmentSelectionDependentProperty,
		GeoElementDependentProperty {
	private final ChartStyleGeo chartStyleGeo;
	private final ChartSegmentSelection chartSegmentSelection;

	/**
	 * Constructs the property.
	 * @param localization localization for translating property names
	 * @param geoElement the element to create the property for
	 * @param chartSegmentSelection the selection from which to read the selected bar/slice index
	 * @throws NotApplicablePropertyException if the property is not applicable for the given element
	 */
	public ChartSegmentPatternFillStyleProperty(Localization localization, GeoElement geoElement,
			ChartSegmentSelection chartSegmentSelection) throws NotApplicablePropertyException {
		super(localization, "Filling.Pattern");
		if (!(geoElement instanceof ChartStyleGeo chartStyleGeo)) {
			throw new NotApplicablePropertyException(geoElement);
		}
		this.chartStyleGeo = chartStyleGeo;
		this.chartSegmentSelection = chartSegmentSelection;
		setValues(patternFillTypes);
	}

	@Override
	public PropertyResource[] getValueIcons() {
		return patternFillTypeIcons.stream().toArray(PropertyResource[]::new);
	}

	@Override
	public @CheckForNull String[] getToolTipLabels() {
		return null;
	}

	@Override
	protected void doSetValue(FillType value) {
		chartSegmentSelection.forEachSelectedSegment(chartStyleGeo.getIntervals(),
				index -> chartStyleGeo.getStyle().setBarFillType(value, index));
		((GeoElement) chartStyleGeo).updateVisualStyleRepaint(GProperty.HATCHING);
	}

	@Override
	public FillType getValue() {
		return chartSegmentSelection.getUniformValueOrNull(chartStyleGeo.getIntervals(),
				index -> chartStyleGeo.getStyle().getBarFillType(index));
	}

	@Override
	public boolean isAvailable() {
		return chartSegmentSelection.getUniformValueOrNull(chartStyleGeo.getIntervals(),
				index -> FillCategory.fromFillType(chartStyleGeo.getStyle().getBarFillType(index)))
				== FillCategory.PATTERN;
	}

	@Override
	public ChartSegmentSelection getChartSegmentSelection() {
		return chartSegmentSelection;
	}

	@Override
	public GeoElement getGeoElement() {
		return (GeoElement) chartStyleGeo;
	}
}
