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
import static org.junit.jupiter.api.Assertions.assertNull;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.kernel.geos.BarChartGeoNumeric;
import org.geogebra.common.kernel.statistics.GeoPieChart;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.Test;

public class ChartStyleGeoColorPropertyTests extends BaseAppTestSetup {
	@Test
	public void testSettingPieChartSliceColor() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoPieChart pieChart = evaluateGeoElement("PieChart({1, 2})");
		ChartSegmentSelection chartSegmentSelection = new ChartSegmentSelection();
		ChartStyleGeoColorProperty chartStyleGeoColorProperty = assertDoesNotThrow(() ->
				new ChartStyleGeoColorProperty(getLocalization(), pieChart,
						chartSegmentSelection));

		chartSegmentSelection.setIndex(2);
		GColor secondSliceDefaultColor = chartStyleGeoColorProperty.getValue();

		chartSegmentSelection.setIndex(1);
		chartStyleGeoColorProperty.setValue(GColor.BLACK);
		assertEquals(GColor.BLACK, chartStyleGeoColorProperty.getValue());
		assertEquals(GColor.BLACK, pieChart.getStyle().getBarColor(1));
		assertEquals(secondSliceDefaultColor, pieChart.getStyle().getBarColor(2));

		chartSegmentSelection.setIndex(1);
		chartStyleGeoColorProperty.setValue(GColor.YELLOW);
		assertEquals(GColor.YELLOW, chartStyleGeoColorProperty.getValue());
		assertEquals(GColor.YELLOW, pieChart.getStyle().getBarColor(1));
		assertEquals(secondSliceDefaultColor, pieChart.getStyle().getBarColor(2));

		chartSegmentSelection.setIndex(2);
		chartStyleGeoColorProperty.setValue(GColor.BLUE);
		assertEquals(GColor.BLUE, chartStyleGeoColorProperty.getValue());
		assertEquals(GColor.YELLOW, pieChart.getStyle().getBarColor(1));
		assertEquals(GColor.BLUE, pieChart.getStyle().getBarColor(2));
	}

	@Test
	public void testReadingColorValueWhenAllBarsMatch() {
		setupApp(SuiteSubApp.GRAPHING);
		BarChartGeoNumeric barChart = evaluateGeoElement("BarChart({1, 2, 3}, {1, 1, 2})");
		ChartSegmentSelection chartSegmentSelection = new ChartSegmentSelection();
		ChartStyleGeoColorProperty chartStyleGeoColorProperty = assertDoesNotThrow(() ->
				new ChartStyleGeoColorProperty(getLocalization(), barChart,
						chartSegmentSelection));

		chartSegmentSelection.setIndex(0);
		chartStyleGeoColorProperty.setValue(GColor.BLACK);
		assertEquals(GColor.BLACK, chartStyleGeoColorProperty.getValue());

		chartSegmentSelection.setIndex(1);
		chartStyleGeoColorProperty.setValue(GColor.BLUE);
		chartSegmentSelection.setIndex(2);
		chartStyleGeoColorProperty.setValue(GColor.BLUE);
		chartSegmentSelection.setIndex(3);
		chartStyleGeoColorProperty.setValue(GColor.BLUE);

		chartSegmentSelection.setIndex(1);
		assertEquals(GColor.BLUE, chartStyleGeoColorProperty.getValue());
		chartSegmentSelection.setIndex(2);
		assertEquals(GColor.BLUE, chartStyleGeoColorProperty.getValue());
		chartSegmentSelection.setIndex(3);
		assertEquals(GColor.BLUE, chartStyleGeoColorProperty.getValue());
		chartSegmentSelection.setIndex(0);
		assertEquals(GColor.BLUE, chartStyleGeoColorProperty.getValue());
	}

	@Test
	public void testReadingColorValueWhenNotAllBarsMatch() {
		setupApp(SuiteSubApp.GRAPHING);
		BarChartGeoNumeric barChart = evaluateGeoElement("BarChart({1, 2, 3}, {1, 1, 2})");
		ChartSegmentSelection chartSegmentSelection = new ChartSegmentSelection();
		ChartStyleGeoColorProperty chartStyleGeoColorProperty = assertDoesNotThrow(() ->
				new ChartStyleGeoColorProperty(getLocalization(), barChart,
						chartSegmentSelection));

		chartSegmentSelection.setIndex(1);
		chartStyleGeoColorProperty.setValue(GColor.BLACK);
		chartSegmentSelection.setIndex(2);
		chartStyleGeoColorProperty.setValue(GColor.BLACK);
		chartSegmentSelection.setIndex(3);
		chartStyleGeoColorProperty.setValue(GColor.YELLOW);

		chartSegmentSelection.setIndex(1);
		assertEquals(GColor.BLACK, chartStyleGeoColorProperty.getValue());
		chartSegmentSelection.setIndex(2);
		assertEquals(GColor.BLACK, chartStyleGeoColorProperty.getValue());
		chartSegmentSelection.setIndex(3);
		assertEquals(GColor.YELLOW, chartStyleGeoColorProperty.getValue());
		chartSegmentSelection.setIndex(0);
		assertNull(chartStyleGeoColorProperty.getValue());
	}
}
