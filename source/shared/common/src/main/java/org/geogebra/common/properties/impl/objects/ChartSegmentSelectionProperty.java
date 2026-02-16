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

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.geos.ChartStyleGeo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.statistics.GeoPieChart;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * {@code Property} responsible for selecting a bar or a slice for a bar chart or pie chart
 * respectively (to be used for independent settings).
 */
public class ChartSegmentSelectionProperty extends AbstractNamedEnumeratedProperty<Integer> {
	private final GeoElement geoElement;
	private final ChartSegmentSelection chartSegmentSelection;

	/**
	 * Constructs the property for the given element.
	 * @param localization localization for translating property names
	 * @param geoElement the element to create the property for
	 * @throws NotApplicablePropertyException if the property is not applicable for the given element
	 */
	public ChartSegmentSelectionProperty(Localization localization, GeoElement geoElement,
			ChartSegmentSelection chartSegmentSelection)
			throws NotApplicablePropertyException {
		super(localization, "Selection");
		if (!(geoElement instanceof ChartStyleGeo)) {
			throw new NotApplicablePropertyException(geoElement);
		}
		this.geoElement = geoElement;
		this.chartSegmentSelection = chartSegmentSelection;
	}

	@Override
	public @Nonnull List<Integer> getValues() {
		int numberOfIntervals = ((ChartStyleGeo) geoElement).getIntervals();
		return IntStream.range(0, numberOfIntervals + 1).boxed().collect(Collectors.toList());
	}

	@Override
	public String[] getValueNames() {
		int numberOfIntervals = ((ChartStyleGeo) geoElement).getIntervals();
		String firstValueTransKey = geoElement instanceof GeoPieChart ? "AllSlices" : "AllBars";
		String valueTransKey = geoElement instanceof GeoPieChart ? "SliceA" : "BarA";
		return Stream.concat(
				Stream.of(getLocalization().getMenu(firstValueTransKey)),
				IntStream.rangeClosed(1, numberOfIntervals).mapToObj(index ->
						getLocalization().getPlain(valueTransKey, String.valueOf(index)))
		).toArray(String[]::new);
	}

	@Override
	protected void doSetValue(Integer value) {
		chartSegmentSelection.setIndex(value);
	}

	@Override
	public Integer getValue() {
		return chartSegmentSelection.getIndex();
	}
}
