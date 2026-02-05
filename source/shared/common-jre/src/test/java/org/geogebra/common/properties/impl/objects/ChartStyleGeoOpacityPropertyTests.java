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

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.statistics.GeoPieChart;
import org.geogebra.common.properties.impl.objects.ChartSegmentSelectionProperty.ChartSegmentSelection;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.Test;

public class ChartStyleGeoOpacityPropertyTests extends BaseAppTestSetup {
	@Test
	public void testSettingPieChartSliceOpacity() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoPieChart pieChart = evaluateGeoElement("PieChart({1, 2})");
		ChartSegmentSelection chartSegmentSelection = new ChartSegmentSelection();
		ChartStyleGeoOpacityProperty chartStyleGeoOpacityProperty = assertDoesNotThrow(() ->
				new ChartStyleGeoOpacityProperty(getLocalization(), pieChart,
						chartSegmentSelection));

		chartSegmentSelection.setIndex(1);
		chartStyleGeoOpacityProperty.setValue(50);
		assertEquals(50, chartStyleGeoOpacityProperty.getValue());
		assertEquals(0.5, pieChart.getStyle().getBarAlpha(1));
		assertEquals(-1.0, pieChart.getStyle().getBarAlpha(2));

		chartSegmentSelection.setIndex(2);
		chartStyleGeoOpacityProperty.setValue(70);
		assertEquals(70, chartStyleGeoOpacityProperty.getValue());
		assertEquals(0.5, pieChart.getStyle().getBarAlpha(1));
		assertEquals(0.7, pieChart.getStyle().getBarAlpha(2));

		chartSegmentSelection.setIndex(1);
		chartStyleGeoOpacityProperty.setValue(5);
		assertEquals(5, chartStyleGeoOpacityProperty.getValue());
		assertEquals(0.05, pieChart.getStyle().getBarAlpha(1));
		assertEquals(0.7, pieChart.getStyle().getBarAlpha(2));
	}

	@Test
	public void testReadingDefaultOpacity() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoPieChart pieChart = evaluateGeoElement("PieChart({1, 2})");
		ChartSegmentSelection chartSegmentSelection = new ChartSegmentSelection();
		ChartStyleGeoOpacityProperty chartStyleGeoOpacityProperty = assertDoesNotThrow(() ->
				new ChartStyleGeoOpacityProperty(getLocalization(), pieChart,
						chartSegmentSelection));

		chartSegmentSelection.setIndex(1);
		assertEquals(-1, pieChart.getStyle().getBarAlpha(1));
		assertEquals(100, chartStyleGeoOpacityProperty.getValue());
	}

	@Test
	public void testReadingOpacityValueWhenAllBarsMatch() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoPieChart pieChart = evaluateGeoElement("PieChart({1, 2, 3})");
		ChartSegmentSelection chartSegmentSelection = new ChartSegmentSelection();
		ChartStyleGeoOpacityProperty chartStyleGeoOpacityProperty = assertDoesNotThrow(() ->
				new ChartStyleGeoOpacityProperty(getLocalization(), pieChart,
						chartSegmentSelection));

		chartSegmentSelection.setIndex(0);
		chartStyleGeoOpacityProperty.setValue(20);
		assertEquals(20, chartStyleGeoOpacityProperty.getValue());

		chartSegmentSelection.setIndex(1);
		chartStyleGeoOpacityProperty.setValue(40);
		chartSegmentSelection.setIndex(2);
		chartStyleGeoOpacityProperty.setValue(40);
		chartSegmentSelection.setIndex(3);
		chartStyleGeoOpacityProperty.setValue(40);

		chartSegmentSelection.setIndex(1);
		assertEquals(40, chartStyleGeoOpacityProperty.getValue());
		chartSegmentSelection.setIndex(2);
		assertEquals(40, chartStyleGeoOpacityProperty.getValue());
		chartSegmentSelection.setIndex(3);
		assertEquals(40, chartStyleGeoOpacityProperty.getValue());
		chartSegmentSelection.setIndex(0);
		assertEquals(40, chartStyleGeoOpacityProperty.getValue());
	}

	@Test
	public void testReadingOpacityValueWhenNotAllBarsMatch() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoPieChart pieChart = evaluateGeoElement("PieChart({1, 2, 3})");
		ChartSegmentSelection chartSegmentSelection = new ChartSegmentSelection();
		ChartStyleGeoOpacityProperty chartStyleGeoOpacityProperty = assertDoesNotThrow(() ->
				new ChartStyleGeoOpacityProperty(getLocalization(), pieChart,
						chartSegmentSelection));

		chartSegmentSelection.setIndex(1);
		chartStyleGeoOpacityProperty.setValue(30);

		chartSegmentSelection.setIndex(2);
		chartStyleGeoOpacityProperty.setValue(50);

		chartSegmentSelection.setIndex(3);
		chartStyleGeoOpacityProperty.setValue(80);

		chartSegmentSelection.setIndex(1);
		assertEquals(30, chartStyleGeoOpacityProperty.getValue());
		chartSegmentSelection.setIndex(2);
		assertEquals(50, chartStyleGeoOpacityProperty.getValue());
		chartSegmentSelection.setIndex(3);
		assertEquals(80, chartStyleGeoOpacityProperty.getValue());
		chartSegmentSelection.setIndex(0);
		assertEquals(30, chartStyleGeoOpacityProperty.getValue());
	}
}
