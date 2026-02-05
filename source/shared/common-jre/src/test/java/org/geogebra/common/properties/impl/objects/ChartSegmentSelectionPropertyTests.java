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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.atomic.AtomicInteger;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.statistics.GeoPieChart;
import org.geogebra.common.properties.impl.objects.ChartSegmentSelectionProperty.ChartSegmentSelection;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class ChartSegmentSelectionPropertyTests extends BaseAppTestSetup {
	@ParameterizedTest
	@ValueSource(strings = {
			"BarChart({1, 2, 3, 4, 5}, {1, 1, 4, 3, 2})",
			"PieChart({1, 2, 3, 4, 5})",
	})
	public void testApplicableGeoElements(String expression) {
		setupApp(SuiteSubApp.GRAPHING);
		assertDoesNotThrow(() -> new ChartSegmentSelectionProperty(getLocalization(),
				evaluateGeoElement(expression), new ChartSegmentSelection()));
	}

	@Test
	public void testSettingChartSelection() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoPieChart geoPieChart = evaluateGeoElement("PieChart({1, 2, 3, 4, 5})");
		ChartSegmentSelection chartSegmentSelection = new ChartSegmentSelection();
		ChartSegmentSelectionProperty chartSegmentSelectionProperty = assertDoesNotThrow(() ->
				new ChartSegmentSelectionProperty(getLocalization(), geoPieChart,
						chartSegmentSelection));

		chartSegmentSelectionProperty.setValue(0);
		assertEquals(0, chartSegmentSelectionProperty.getValue());
		assertEquals(0, chartSegmentSelection.getIndex());

		chartSegmentSelectionProperty.setValue(2);
		assertEquals(2, chartSegmentSelectionProperty.getValue());
		assertEquals(2, chartSegmentSelection.getIndex());

		chartSegmentSelectionProperty.setValue(5);
		assertEquals(5, chartSegmentSelectionProperty.getValue());
		assertEquals(5, chartSegmentSelection.getIndex());
	}

	@Test
	public void testChartSelectionOwnerNotifications() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoElement geoElement = evaluateGeoElement("PieChart({1, 2, 3, 4, 5})");
		ChartSegmentSelection chartSegmentSelection = new ChartSegmentSelection();
		ChartSegmentSelectionProperty chartSegmentSelectionProperty = assertDoesNotThrow(() ->
				new ChartSegmentSelectionProperty(getLocalization(), geoElement,
						chartSegmentSelection));
		AtomicInteger listener1CalledCount = new AtomicInteger();
		ChartSegmentSelection.Listener listener1 = listener1CalledCount::incrementAndGet;
		AtomicInteger listener2CalledCount = new AtomicInteger();
		ChartSegmentSelection.Listener listener2 = listener2CalledCount::incrementAndGet;

		chartSegmentSelection.registerListener(listener1);
		chartSegmentSelection.registerListener(listener2);
		chartSegmentSelectionProperty.setValue(1);
		assertEquals(1, listener1CalledCount.get());
		assertEquals(1, listener2CalledCount.get());

		chartSegmentSelection.unregisterListener(listener1);
		chartSegmentSelectionProperty.setValue(2);
		assertEquals(1, listener1CalledCount.get());
		assertEquals(2, listener2CalledCount.get());
	}
}
