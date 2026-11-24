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

import static java.util.Map.entry;

import java.util.List;

import org.geogebra.common.kernel.geos.ChartStyleGeo;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.properties.FillType;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * {@code Property} responsible for setting the fill category for pie and bar charts.
 */
public final class ChartSegmentFillCategoryProperty
		extends AbstractNamedEnumeratedProperty<FillCategory>
		implements ChartSegmentSelectionDependentProperty {
	private final ChartStyleGeo chartStyleGeo;
	private final ElementSettingsData elementSettingsData;

	/**
	 * Constructs the property.
	 * @param localization localization for translating property names
	 * @param geoElement the element to create the property for
	 * @param elementSettingsData temporary data retained by the settings context
	 * @throws NotApplicablePropertyException if the property is not applicable for the given element
	 */
	public ChartSegmentFillCategoryProperty(
			Localization localization, GeoElement geoElement, ElementSettingsData elementSettingsData)
			throws NotApplicablePropertyException {
		super(localization, "");
		if (!(geoElement instanceof ChartStyleGeo chartStyleGeo)) {
			throw new NotApplicablePropertyException(geoElement);
		}
		this.chartStyleGeo = chartStyleGeo;
		this.elementSettingsData = elementSettingsData;
		elementSettingsData
				.previousSegmentPatterns
				.keySet()
				.removeIf(index -> index > chartStyleGeo.getIntervals());
		setNamedValues(List.of(
				entry(FillCategory.PATTERN, "Filling.Pattern"),
				entry(FillCategory.SYMBOL, "Filling.Symbol"),
				entry(FillCategory.IMAGE, "Filling.Image")));
	}

	@Override
	protected void doSetValue(FillCategory fillCategory) {
		elementSettingsData.fillingSelection.forEachSelectedSegment(
				chartStyleGeo.getIntervals(), index -> setFillCategory(index, fillCategory));
		((GeoElement) chartStyleGeo).updateVisualStyleRepaint(GProperty.HATCHING);
	}

	private void setFillCategory(int index, FillCategory newFillCategory) {
		FillType previousFillType = chartStyleGeo.getStyle().getBarFillType(index);
		FillCategory previousFillCategory = FillCategory.fromFillType(previousFillType);
		if (previousFillCategory == FillCategory.PATTERN) {
			elementSettingsData.previousSegmentPatterns.put(index, previousFillType);
		}
		FillType newFillType = newFillCategory.toFillType(
				elementSettingsData.previousSegmentPatterns.getOrDefault(index, FillType.STANDARD));
		chartStyleGeo.getStyle().setBarFillType(newFillType, index);
	}

	@Override
	public FillCategory getValue() {
		return elementSettingsData.fillingSelection.getUniformValueOrNull(
				chartStyleGeo.getIntervals(),
				index -> FillCategory.fromFillType(chartStyleGeo.getStyle().getBarFillType(index)));
	}

	@Override
	public ChartSegmentSelection getChartSegmentSelection() {
		return elementSettingsData.fillingSelection;
	}
}
