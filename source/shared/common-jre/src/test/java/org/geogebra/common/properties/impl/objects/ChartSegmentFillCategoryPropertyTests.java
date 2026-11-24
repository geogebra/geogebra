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
import org.geogebra.common.properties.ObjectSettingsSession;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.Test;

class ChartSegmentFillCategoryPropertyTests extends BaseAppTestSetup {
	@Test
	void testSettingDifferentFillCategories() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoPieChart pieChart = evaluateGeoElement("PieChart({1, 2, 3})");
		ElementSettingsData data = new ElementSettingsData();
		ChartSegmentFillCategoryProperty chartSegmentFillCategoryProperty = assertDoesNotThrow(
				() -> new ChartSegmentFillCategoryProperty(getLocalization(), pieChart, data));

		data.fillingSelection.setIndex(1);
		chartSegmentFillCategoryProperty.setValue(FillCategory.IMAGE);
		data.fillingSelection.setIndex(2);
		chartSegmentFillCategoryProperty.setValue(FillCategory.PATTERN);
		data.fillingSelection.setIndex(3);
		chartSegmentFillCategoryProperty.setValue(FillCategory.SYMBOL);

		data.fillingSelection.setIndex(0);
		assertNull(chartSegmentFillCategoryProperty.getValue());
		data.fillingSelection.setIndex(1);
		assertEquals(FillCategory.IMAGE, chartSegmentFillCategoryProperty.getValue());
		assertEquals(FillType.IMAGE, pieChart.getStyle().getBarFillType(1));
		data.fillingSelection.setIndex(2);
		assertEquals(FillCategory.PATTERN, chartSegmentFillCategoryProperty.getValue());
		assertEquals(FillType.STANDARD, pieChart.getStyle().getBarFillType(2));
		data.fillingSelection.setIndex(3);
		assertEquals(FillCategory.SYMBOL, chartSegmentFillCategoryProperty.getValue());
		assertEquals(FillType.SYMBOLS, pieChart.getStyle().getBarFillType(3));
	}

	@Test
	void testSettingAllFillCategories() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoPieChart pieChart = evaluateGeoElement("PieChart({1, 2, 3})");
		ElementSettingsData data = new ElementSettingsData();
		ChartSegmentFillCategoryProperty chartSegmentFillCategoryProperty = assertDoesNotThrow(
				() -> new ChartSegmentFillCategoryProperty(getLocalization(), pieChart, data));

		data.fillingSelection.setIndex(0);
		chartSegmentFillCategoryProperty.setValue(FillCategory.IMAGE);
		assertEquals(FillCategory.IMAGE, chartSegmentFillCategoryProperty.getValue());
		data.fillingSelection.setIndex(1);
		assertEquals(FillCategory.IMAGE, chartSegmentFillCategoryProperty.getValue());
		data.fillingSelection.setIndex(2);
		assertEquals(FillCategory.IMAGE, chartSegmentFillCategoryProperty.getValue());
		data.fillingSelection.setIndex(3);
		assertEquals(FillCategory.IMAGE, chartSegmentFillCategoryProperty.getValue());

		data.fillingSelection.setIndex(0);
		chartSegmentFillCategoryProperty.setValue(FillCategory.SYMBOL);
		assertEquals(FillCategory.SYMBOL, chartSegmentFillCategoryProperty.getValue());
		data.fillingSelection.setIndex(1);
		assertEquals(FillCategory.SYMBOL, chartSegmentFillCategoryProperty.getValue());
		data.fillingSelection.setIndex(2);
		assertEquals(FillCategory.SYMBOL, chartSegmentFillCategoryProperty.getValue());
		data.fillingSelection.setIndex(3);
		assertEquals(FillCategory.SYMBOL, chartSegmentFillCategoryProperty.getValue());
	}

	@Test
	void testPreservingPreviousPatternFillTypes() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoPieChart pieChart = evaluateGeoElement("PieChart({1, 2, 3})");
		ElementSettingsData data = new ElementSettingsData();
		ChartSegmentFillCategoryProperty chartSegmentFillCategoryProperty = assertDoesNotThrow(
				() -> new ChartSegmentFillCategoryProperty(getLocalization(), pieChart, data));

		pieChart.getStyle().setBarFillType(FillType.DOTTED, 1);
		pieChart.getStyle().setBarFillType(FillType.BRICK, 2);
		pieChart.getStyle().setBarFillType(FillType.CROSSHATCHED, 3);

		data.fillingSelection.setIndex(1);
		assertEquals(FillCategory.PATTERN, chartSegmentFillCategoryProperty.getValue());
		data.fillingSelection.setIndex(2);
		assertEquals(FillCategory.PATTERN, chartSegmentFillCategoryProperty.getValue());
		data.fillingSelection.setIndex(3);
		assertEquals(FillCategory.PATTERN, chartSegmentFillCategoryProperty.getValue());

		data.fillingSelection.setIndex(1);
		chartSegmentFillCategoryProperty.setValue(FillCategory.SYMBOL);
		data.fillingSelection.setIndex(2);
		chartSegmentFillCategoryProperty.setValue(FillCategory.SYMBOL);
		data.fillingSelection.setIndex(3);
		chartSegmentFillCategoryProperty.setValue(FillCategory.IMAGE);

		data.fillingSelection.setIndex(0);
		chartSegmentFillCategoryProperty.setValue(FillCategory.PATTERN);

		assertEquals(FillType.DOTTED, pieChart.getStyle().getBarFillType(1));
		assertEquals(FillType.BRICK, pieChart.getStyle().getBarFillType(2));
		assertEquals(FillType.CROSSHATCHED, pieChart.getStyle().getBarFillType(3));
	}

	@Test
	void growingChartKeepsPatternsAndInitializesNewSegments() throws NotApplicablePropertyException {
		setupApp(SuiteSubApp.GRAPHING);
		GeoPieChart chart = evaluateGeoElement("p = PieChart({1, 2, 3})");
		ObjectSettingsSession.Context context = new ObjectSettingsSession.Context();
		chart.getStyle().setBarFillType(FillType.DOTTED, 1);
		chart.getStyle().setBarFillType(FillType.BRICK, 2);
		new ChartSegmentFillCategoryProperty(getLocalization(), chart, context.forElement(chart))
				.setValue(FillCategory.SYMBOL);
		editGeoElement(chart, "PieChart({1, 2, 3, 4})");
		GeoPieChart replacement = (GeoPieChart) lookup("p");
		context.replace(chart, replacement);
		ChartSegmentFillCategoryProperty reconstructed = new ChartSegmentFillCategoryProperty(
				getLocalization(), replacement, context.forElement(replacement));
		reconstructed.setValue(FillCategory.SYMBOL);
		reconstructed.setValue(FillCategory.PATTERN);

		assertEquals(FillType.DOTTED, replacement.getStyle().getBarFillType(1));
		assertEquals(FillType.BRICK, replacement.getStyle().getBarFillType(2));
		assertEquals(FillType.STANDARD, replacement.getStyle().getBarFillType(4));
	}
}
