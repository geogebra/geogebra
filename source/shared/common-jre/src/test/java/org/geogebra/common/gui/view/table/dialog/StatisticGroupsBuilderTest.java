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

package org.geogebra.common.gui.view.table.dialog;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.gui.view.table.TableUtil;
import org.geogebra.common.gui.view.table.TableValuesModel;
import org.geogebra.common.gui.view.table.TableValuesProcessor;
import org.geogebra.common.gui.view.table.TableValuesView;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.statistics.Statistic;
import org.geogebra.common.util.AttributedString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StatisticGroupsBuilderTest extends BaseUnitTest {

	protected TableValuesView view;
	protected TableValuesModel model;
	protected TableValuesProcessor processor;

	@BeforeEach
	void setupTest() {
		view = new TableValuesView(getKernel());
		getKernel().attach(view);
		model = view.getTableValuesModel();
		view.clearView();
		processor = view.getProcessor();
	}

	@Test
	void testBugAPPS3753() {
		processor.processInput("1", view.getValues(), 0);
		processor.processInput("2", view.getValues(), 1);
		processor.processInput("1", null, 0);
		GeoList column = (GeoList) view.getEvaluatable(1);
		processor.processInput("2", column, 1);
		processor.processInput("", column, 1);
		try {
			view.getStatistics1Var(1);
		} catch (Exception e) {
			throw new AssertionError("Should not throw an exception", e);
		}
	}

	@Test
	void testFiltering() {
		StatisticGroupsBuilder builder = new StatisticGroupsBuilder();
		GeoList list = add("{1, 2, 3, 4, 5}");
		builder.setStatisticsFilter(statistic -> statistic == Statistic.MEAN);

		assertEquals(
				List.of(new StatisticGroup(null, List.of(row(Statistic.MEAN, "x", "3", "3")))),
				builder.buildOneVariableStatistics(list, "x"));
	}

	@Test
	void testOneVariableGrouping() {
		StatisticGroupsBuilder builder = new StatisticGroupsBuilder();
		GeoList x = add("{1, 2, 3, 4, 5}");

		assertEquals(
				List.of(new StatisticGroup(
						null,
						List.of(
								row(Statistic.MEAN, "x", "3", "3"),
								row(Statistic.SUM, "x", "15", "15"),
								row(Statistic.SIGMAXX, "x", "55", "55"),
								row(Statistic.SAMPLE_SD, "x", "1.58", "1.58113883008419"),
								row(Statistic.SD, "x", "1.41", "1.414213562373095"),
								row(Statistic.LENGTH, "x", "5", "5"),
								row(Statistic.MIN, "x", "1", "1"),
								row(Statistic.Q1, "x", "1.5", "1.5"),
								row(Statistic.MEDIAN, "x", "3", "3"),
								row(Statistic.Q3, "x", "4.5", "4.5"),
								row(Statistic.MAX, "x", "5", "5")))),
				builder.buildOneVariableStatistics(x, "x"));
	}

	@Test
	void testTwoVariableGrouping() {
		StatisticGroupsBuilder builder = new StatisticGroupsBuilder();
		GeoList x = add("{1, 2, 3, 4, 5}");
		GeoList y = add("{2, 4, 6, 8, 10}");

		assertEquals(
				List.of(
						new StatisticGroup(
								new AttributedString("X Y Statistics"),
								List.of(
										row(Statistic.SIGMAXY, "XY", "110", "110"),
										row(Statistic.PMCC, "XY", "1", "1"),
										row(Statistic.COVARIANCE, "XY", "4", "4"),
										row(Statistic.LENGTH, "X", "5", "5"))),
						new StatisticGroup(
								new AttributedString("X Statistics"),
								List.of(
										row(Statistic.MEAN, "X", "3", "3"),
										row(Statistic.SUM, "X", "15", "15"),
										row(Statistic.SIGMAXX, "X", "55", "55"),
										row(Statistic.SAMPLE_SD, "X", "1.58", "1.58113883008419"),
										row(Statistic.SD, "X", "1.41", "1.414213562373095"),
										row(Statistic.MIN, "X", "1", "1"),
										row(Statistic.MAX, "X", "5", "5"))),
						new StatisticGroup(
								new AttributedString("Y Statistics"),
								List.of(
										row(Statistic.MEAN, "Y", "6", "6"),
										row(Statistic.SUM, "Y", "30", "30"),
										row(Statistic.SIGMAXX, "Y", "220", "220"),
										row(Statistic.SAMPLE_SD, "Y", "3.16", "3.16227766016838"),
										row(Statistic.SD, "Y", "2.83", "2.82842712474619"),
										row(Statistic.MIN, "Y", "2", "2"),
										row(Statistic.MAX, "Y", "10", "10")))),
				builder.buildTwoVariableStatistics(x, "X", y, "Y"));
	}

	@Test
	void testTwoVariableGroupingWithSubscript() {
		StatisticGroupsBuilder builder = new StatisticGroupsBuilder();
		GeoList x = add("{1, 2, 3, 4, 5}");
		GeoList y = add("{2, 4, 6, 8, 10}");

		assertEquals(
				TableUtil.getStatisticsHeading("x y_{1}", getLocalization()),
				builder.buildTwoVariableStatistics(x, "x", y, "y_{1}").get(0).heading());
	}

	private StatisticGroup.Row row(
			Statistic statistic, String variableName, String value, String copyValue) {
		return new StatisticGroup.Row(
				getLocalization().getMenu(statistic.getMenuLocalizationKey()),
				statistic.getLHS(getLocalization(), variableName) + " = " + value,
				true,
				copyValue);
	}
}
