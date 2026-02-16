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
import org.geogebra.common.kernel.geos.properties.FillType;
import org.geogebra.common.kernel.statistics.GeoPieChart;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.Test;

public class ChartSegmentFillCategoryPropertyTests extends BaseAppTestSetup {
	@Test
	public void testSettingDifferentFillCategories() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoPieChart pieChart = evaluateGeoElement("PieChart({1, 2, 3})");
		ChartSegmentSelection chartSegmentSelection = new ChartSegmentSelection();
		ChartSegmentFillCategoryProperty chartSegmentFillCategoryProperty =
				assertDoesNotThrow(() -> new ChartSegmentFillCategoryProperty(
						getLocalization(), pieChart, chartSegmentSelection));

		chartSegmentSelection.setIndex(1);
		chartSegmentFillCategoryProperty.setValue(FillCategory.IMAGE);
		chartSegmentSelection.setIndex(2);
		chartSegmentFillCategoryProperty.setValue(FillCategory.PATTERN);
		chartSegmentSelection.setIndex(3);
		chartSegmentFillCategoryProperty.setValue(FillCategory.SYMBOL);

		chartSegmentSelection.setIndex(0);
		assertNull(chartSegmentFillCategoryProperty.getValue());
		chartSegmentSelection.setIndex(1);
		assertEquals(FillCategory.IMAGE, chartSegmentFillCategoryProperty.getValue());
		assertEquals(FillType.IMAGE, pieChart.getStyle().getBarFillType(1));
		chartSegmentSelection.setIndex(2);
		assertEquals(FillCategory.PATTERN, chartSegmentFillCategoryProperty.getValue());
		assertEquals(FillType.STANDARD, pieChart.getStyle().getBarFillType(2));
		chartSegmentSelection.setIndex(3);
		assertEquals(FillCategory.SYMBOL, chartSegmentFillCategoryProperty.getValue());
		assertEquals(FillType.SYMBOLS, pieChart.getStyle().getBarFillType(3));
	}

	@Test
	public void testSettingAllFillCategories() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoPieChart pieChart = evaluateGeoElement("PieChart({1, 2, 3})");
		ChartSegmentSelection chartSegmentSelection = new ChartSegmentSelection();
		ChartSegmentFillCategoryProperty chartSegmentFillCategoryProperty =
				assertDoesNotThrow(() -> new ChartSegmentFillCategoryProperty(
						getLocalization(), pieChart, chartSegmentSelection));

		chartSegmentSelection.setIndex(0);
		chartSegmentFillCategoryProperty.setValue(FillCategory.IMAGE);
		assertEquals(FillCategory.IMAGE, chartSegmentFillCategoryProperty.getValue());
		chartSegmentSelection.setIndex(1);
		assertEquals(FillCategory.IMAGE, chartSegmentFillCategoryProperty.getValue());
		chartSegmentSelection.setIndex(2);
		assertEquals(FillCategory.IMAGE, chartSegmentFillCategoryProperty.getValue());
		chartSegmentSelection.setIndex(3);
		assertEquals(FillCategory.IMAGE, chartSegmentFillCategoryProperty.getValue());

		chartSegmentSelection.setIndex(0);
		chartSegmentFillCategoryProperty.setValue(FillCategory.SYMBOL);
		assertEquals(FillCategory.SYMBOL, chartSegmentFillCategoryProperty.getValue());
		chartSegmentSelection.setIndex(1);
		assertEquals(FillCategory.SYMBOL, chartSegmentFillCategoryProperty.getValue());
		chartSegmentSelection.setIndex(2);
		assertEquals(FillCategory.SYMBOL, chartSegmentFillCategoryProperty.getValue());
		chartSegmentSelection.setIndex(3);
		assertEquals(FillCategory.SYMBOL, chartSegmentFillCategoryProperty.getValue());
	}

	@Test
	public void testPreservingPreviousPatternFillTypes() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoPieChart pieChart = evaluateGeoElement("PieChart({1, 2, 3})");
		ChartSegmentSelection chartSegmentSelection = new ChartSegmentSelection();
		ChartSegmentFillCategoryProperty chartSegmentFillCategoryProperty =
				assertDoesNotThrow(() -> new ChartSegmentFillCategoryProperty(
						getLocalization(), pieChart, chartSegmentSelection));

		pieChart.getStyle().setBarFillType(FillType.DOTTED, 1);
		pieChart.getStyle().setBarFillType(FillType.BRICK, 2);
		pieChart.getStyle().setBarFillType(FillType.CROSSHATCHED, 3);

		chartSegmentSelection.setIndex(1);
		assertEquals(FillCategory.PATTERN, chartSegmentFillCategoryProperty.getValue());
		chartSegmentSelection.setIndex(2);
		assertEquals(FillCategory.PATTERN, chartSegmentFillCategoryProperty.getValue());
		chartSegmentSelection.setIndex(3);
		assertEquals(FillCategory.PATTERN, chartSegmentFillCategoryProperty.getValue());

		chartSegmentSelection.setIndex(1);
		chartSegmentFillCategoryProperty.setValue(FillCategory.SYMBOL);
		chartSegmentSelection.setIndex(2);
		chartSegmentFillCategoryProperty.setValue(FillCategory.SYMBOL);
		chartSegmentSelection.setIndex(3);
		chartSegmentFillCategoryProperty.setValue(FillCategory.IMAGE);

		chartSegmentSelection.setIndex(0);
		chartSegmentFillCategoryProperty.setValue(FillCategory.PATTERN);

		assertEquals(FillType.DOTTED, pieChart.getStyle().getBarFillType(1));
		assertEquals(FillType.BRICK, pieChart.getStyle().getBarFillType(2));
		assertEquals(FillType.CROSSHATCHED, pieChart.getStyle().getBarFillType(3));
	}
}
