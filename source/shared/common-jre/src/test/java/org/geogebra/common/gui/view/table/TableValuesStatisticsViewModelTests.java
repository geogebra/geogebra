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

package org.geogebra.common.gui.view.table;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.gui.view.table.TableValuesStatisticsViewModel.Content;
import org.geogebra.common.gui.view.table.TableValuesStatisticsViewModel.Mode;
import org.geogebra.common.gui.view.table.dialog.StatisticGroup;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.restrictions.FeatureRestriction;
import org.geogebra.common.states.State.Subscription;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.Test;

class TableValuesStatisticsViewModelTests extends BaseAppTestSetup {
	@Test
	void testShowOneVariableStatistics() {
		setupApp(SuiteSubApp.GRAPHING);
		TableValues tableValues = setupTableValues("x = {1, 2, 3, 4}", "y_1 = {5, 6, 7, 8}");
		tableValues.getStatisticsViewModel().show(Mode.ONE_VARIABLE, 1);

		Content.Statistics content = assertInstanceOf(Content.Statistics.class,
				tableValues.getStatisticsViewModel().getContent().get());
		assertEquals(getLocalization().getMenu("1VariableStatistics"), content.title());
		assertEquals("Column y1", content.header().getRawValue());
		assertFalse(content.groups().get().isEmpty());
	}

	@Test
	void testShowOneVariableStatisticsWithInsufficientData() {
		setupApp(SuiteSubApp.GRAPHING);
		TableValues tableValues = setupTableValues("x = {1, 2, 3, 4}", "y_1 = {5}");
		tableValues.getStatisticsViewModel().show(Mode.ONE_VARIABLE, 1);

		Content.Error content = assertInstanceOf(Content.Error.class,
				tableValues.getStatisticsViewModel().getContent().get());
		assertEquals(getLocalization().getMenu("1VariableStatistics"), content.title());
		assertEquals("Column y1", content.header().getRawValue());
		assertEquals(getLocalization().getMenu("StatsDialog.NoDataMsg1VarStats"),
				content.message());
	}

	@Test
	void testShowTwoVariableStatistics() {
		setupApp(SuiteSubApp.GRAPHING);
		TableValues tableValues = setupTableValues("x = {1, 2, 3, 4}", "y_1 = {5, 6, 7, 8}");
		tableValues.getStatisticsViewModel().show(Mode.TWO_VARIABLE, 1);

		Content.Statistics content = assertInstanceOf(Content.Statistics.class,
				tableValues.getStatisticsViewModel().getContent().get());
		assertEquals(getLocalization().getMenu("2VariableStatistics"), content.title());
		assertEquals("Column x y1", content.header().getRawValue());
		assertFalse(content.groups().get().isEmpty());
	}

	@Test
	void testShowTwoVariableStatisticsWithInsufficientData() {
		setupApp(SuiteSubApp.GRAPHING);
		TableValues tableValues = setupTableValues("x = {1}", "y_1 = {5}");
		tableValues.getStatisticsViewModel().show(Mode.TWO_VARIABLE, 1);

		Content.Error content = assertInstanceOf(Content.Error.class,
				tableValues.getStatisticsViewModel().getContent().get());
		assertEquals(getLocalization().getMenu("2VariableStatistics"), content.title());
		assertEquals("Column x y1", content.header().getRawValue());
		assertEquals(getLocalization().getMenu("StatsDialog.NoDataMsg2VarStats"),
				content.message());
	}

	@Test
	void testShowRegression() {
		setupApp(SuiteSubApp.GRAPHING);
		TableValues tableValues = setupTableValues("x = {1, 2, 3, 4}", "y_1 = {5, 6, 7, 8}");
		tableValues.getStatisticsViewModel().show(Mode.REGRESSION, 1);

		Content.Regression content = assertInstanceOf(Content.Regression.class,
				tableValues.getStatisticsViewModel().getContent().get());
		assertEquals(getLocalization().getMenu("Regression"), content.title());
		assertEquals("Column y1", content.header().getRawValue());
		assertEquals(0, content.selectedRegressionIndex().get());
		assertFalse(content.regressionModels().get().isEmpty());
		assertFalse(content.groups().get().isEmpty());
		assertNotNull(content.plotAction());
	}

	@Test
	void testSelectingRegressionSpecificationUpdatesContent() {
		setupApp(SuiteSubApp.GRAPHING);
		TableValues tableValues = setupTableValues("x = {1, 2, 3, 4}", "y_1 = {5, 6, 7, 8}");
		tableValues.getStatisticsViewModel().show(Mode.REGRESSION, 1);
		Content.Regression initialContent = assertInstanceOf(Content.Regression.class,
				tableValues.getStatisticsViewModel().getContent().get());
		List<StatisticGroup> groups = initialContent.groups().get();

		tableValues.getStatisticsViewModel().selectedRegressionIndexChanged(3);
		Content.Regression content = assertInstanceOf(Content.Regression.class,
				tableValues.getStatisticsViewModel().getContent().get());
		assertSame(initialContent, content);
		assertEquals(3, content.selectedRegressionIndex().get());
		assertNotEquals(groups, content.groups().get());
	}

	@Test
	void testPlotRegressionAddsConstructionElement() {
		setupApp(SuiteSubApp.GRAPHING);
		TableValues tableValues = setupTableValues("x = {1, 2, 3, 4}", "y_1 = {5, 6, 7, 8}");
		tableValues.getStatisticsViewModel().show(Mode.REGRESSION, 1);

		Content.Regression content = assertInstanceOf(Content.Regression.class,
				tableValues.getStatisticsViewModel().getContent().get());
		assertNull(lookup("f"));
		content.plotAction().run();
		assertNotNull(lookup("f"));
	}

	@Test
	void testShowRegressionWithInsufficientData() {
		setupApp(SuiteSubApp.GRAPHING);
		TableValues tableValues = setupTableValues("x = {1}", "y_1 = {5}");
		tableValues.getStatisticsViewModel().show(Mode.REGRESSION, 1);

		Content.Error content = assertInstanceOf(Content.Error.class,
				tableValues.getStatisticsViewModel().getContent().get());
		assertEquals(getLocalization().getMenu("Regression"), content.title());
		assertEquals("Column y1", content.header().getRawValue());
		assertEquals(getLocalization().getMenu("StatsDialog.NoDataMsgRegression"),
				content.message());
	}

	@Test
	void testShowRegressionInMmsModeDoesNotExposePlotAction() {
		setupApp(SuiteSubApp.GRAPHING);
		getApp().getRegressionSpecBuilder().applyRestrictions(
				Set.of(FeatureRestriction.CUSTOM_MMS_REGRESSION_MODELS));
		TableValues tableValues = setupTableValues("x = {1, 2, 3, 4}", "y_1 = {5, 6, 7, 8}");
		tableValues.getStatisticsViewModel().show(Mode.REGRESSION, 1);

		Content.Regression content = assertInstanceOf(Content.Regression.class,
				tableValues.getStatisticsViewModel().getContent().get());
		assertNull(content.plotAction());
	}

	@Test
	void testRefreshesContentWhenTableValuesChange() {
		setupApp(SuiteSubApp.GRAPHING);
		TableValues tableValues = setupTableValues("x = {1, 2, 3, 4}");
		tableValues.getStatisticsViewModel().show(Mode.ONE_VARIABLE, 0);
		Content.Statistics content = assertInstanceOf(Content.Statistics.class,
				tableValues.getStatisticsViewModel().getContent().get());
		List<StatisticGroup> groups = content.groups().get();

		tableValues.getTableValuesModel().set(
				tableValues.getTableValuesModel().createValue(5), tableValues.getValues(), 0);
		Content.Statistics refreshedContent = assertInstanceOf(Content.Statistics.class,
				tableValues.getStatisticsViewModel().getContent().get());
		assertSame(content, refreshedContent);
		assertNotEquals(groups, refreshedContent.groups().get());
	}

	@Test
	void testModeChangeReplacesContentWithoutNull() {
		setupApp(SuiteSubApp.GRAPHING);
		TableValues tableValues = setupTableValues("x = {1, 2, 3, 4}", "y = {5, 6, 7, 8}");
		tableValues.getStatisticsViewModel().show(Mode.ONE_VARIABLE, 1);
		AtomicInteger updates = new AtomicInteger();
		Subscription subscription = tableValues.getStatisticsViewModel().getContent()
				.subscribe(view -> {
					assertNotNull(view);
					updates.incrementAndGet();
				});

		tableValues.getStatisticsViewModel().show(Mode.TWO_VARIABLE, 1);

		assertEquals(1, updates.get());

		subscription.cancel();
	}

	@Test
	void testDifferentColumnReplacesContent() {
		setupApp(SuiteSubApp.GRAPHING);
		TableValues tableValues = setupTableValues("x = {1, 2, 3, 4}",
				"y = {5, 6, 7, 8}", "z = {9, 10, 11, 12}");
		tableValues.getStatisticsViewModel().show(Mode.ONE_VARIABLE, 1);
		Content initialContent = tableValues.getStatisticsViewModel().getContent().get();

		tableValues.getStatisticsViewModel().show(Mode.ONE_VARIABLE, 2);

		Content refreshedContent = tableValues.getStatisticsViewModel().getContent().get();
		assertNotSame(initialContent, refreshedContent);
		assertEquals("Column z1", refreshedContent.header().getRawValue());
	}

	@Test
	void testRegressionModelsUpdateFirstToKeepIndexValid() {
		setupApp(SuiteSubApp.GRAPHING);
		TableValues tableValues = setupTableValues("x = {1, 2}", "y = {3, 4}");
		tableValues.getStatisticsViewModel().show(Mode.REGRESSION, 1);
		Content.Regression content = assertInstanceOf(Content.Regression.class,
				tableValues.getStatisticsViewModel().getContent().get());
		tableValues.getStatisticsViewModel().selectedRegressionIndexChanged(4);
		AtomicInteger updates = new AtomicInteger();
		Subscription regressionModelsSubscription = content.regressionModels()
				.subscribe(models -> {
					updates.incrementAndGet();
					assertTrue(content.selectedRegressionIndex().get() < models.size());
				});
		Subscription selectedRegressionIndexSubscription = content.selectedRegressionIndex()
				.subscribe(index -> {
					updates.incrementAndGet();
					assertTrue(index >= 0 && index < content.regressionModels().get().size());
				});

		TableValuesModel model = tableValues.getTableValuesModel();
		model.set(model.createValue(5), tableValues.getValues(), 2);
		model.set(model.createValue(6), (GeoList) tableValues.getEvaluatable(1), 2);

		assertEquals(2, updates.get());

		regressionModelsSubscription.cancel();
		selectedRegressionIndexSubscription.cancel();
	}

	@Test
	void testRegressionIndexUpdatesFirstToKeepIndexValid() {
		setupApp(SuiteSubApp.GRAPHING);
		TableValues tableValues = setupTableValues("x = {1, 2, 3}", "y = {4, 5, 6}");
		tableValues.getStatisticsViewModel().show(Mode.REGRESSION, 1);
		Content.Regression content = assertInstanceOf(Content.Regression.class,
				tableValues.getStatisticsViewModel().getContent().get());
		tableValues.getStatisticsViewModel().selectedRegressionIndexChanged(6);
		AtomicInteger updates = new AtomicInteger();
		Subscription selectedRegressionIndexSubscription = content.selectedRegressionIndex()
				.subscribe(index -> {
					updates.incrementAndGet();
					assertTrue(index >= 0 && index < content.regressionModels().get().size());
				});
		Subscription regressionModelsSubscription = content.regressionModels()
				.subscribe(models -> {
					updates.incrementAndGet();
					assertTrue(content.selectedRegressionIndex().get() < models.size());
				});

		TableValuesModel model = tableValues.getTableValuesModel();
		model.set(model.createEmptyValue(), (GeoList) tableValues.getEvaluatable(1), 2);

		assertEquals(2, updates.get());

		selectedRegressionIndexSubscription.cancel();
		regressionModelsSubscription.cancel();
	}

	@Test
	void testRegressionModelChangeKeepsOuterView() {
		setupApp(SuiteSubApp.GRAPHING);
		TableValues tableValues = setupTableValues("x = {1, 2}", "y = {3, 4}");
		tableValues.getStatisticsViewModel().show(Mode.REGRESSION, 1);
		Content.Regression content = assertInstanceOf(Content.Regression.class,
				tableValues.getStatisticsViewModel().getContent().get());
		List<String> regressionModels = content.regressionModels().get();

		tableValues.getTableValuesModel().set(tableValues.getTableValuesModel().createValue(5),
				tableValues.getValues(), 2);
		tableValues.getTableValuesModel().set(tableValues.getTableValuesModel().createValue(6),
				(GeoList) tableValues.getEvaluatable(1), 2);

		assertSame(content, tableValues.getStatisticsViewModel().getContent().get());
		assertNotEquals(regressionModels, content.regressionModels().get());
	}

	@Test
	void testIrrelevantColumnChangeDoesNotRefresh() {
		setupApp(SuiteSubApp.GRAPHING);
		TableValues tableValues = setupTableValues("x = {1, 2}", "y = {3, 4}", "z = {5, 6}");
		tableValues.getStatisticsViewModel().show(Mode.ONE_VARIABLE, 1);
		Content.Statistics content = assertInstanceOf(Content.Statistics.class,
				tableValues.getStatisticsViewModel().getContent().get());
		AtomicInteger updates = new AtomicInteger();
		Subscription subscription = content.groups().subscribe(groups -> updates.incrementAndGet());

		tableValues.getTableValuesModel().set(tableValues.getTableValuesModel().createValue(7),
				(GeoList) tableValues.getEvaluatable(2), 0);

		assertEquals(0, updates.get());

		subscription.cancel();
	}

	@Test
	void testCloseClearsContent() {
		setupApp(SuiteSubApp.GRAPHING);
		TableValues tableValues = setupTableValues("x = {1, 2, 3, 4}");
		tableValues.getStatisticsViewModel().show(Mode.ONE_VARIABLE, 0);

		tableValues.getStatisticsViewModel().close();
		assertNull(tableValues.getStatisticsViewModel().getContent().get());
	}

	@Test
	void testRecoversFromInsufficientData() {
		setupApp(SuiteSubApp.GRAPHING);
		TableValues tableValues = setupTableValues("x = {1}");
		tableValues.getStatisticsViewModel().show(Mode.ONE_VARIABLE, 0);

		assertInstanceOf(Content.Error.class,
				tableValues.getStatisticsViewModel().getContent().get());
		tableValues.getTableValuesModel().set(tableValues.getTableValuesModel().createValue(2),
				tableValues.getValues(), 1);
		assertInstanceOf(Content.Statistics.class,
				tableValues.getStatisticsViewModel().getContent().get());
	}

	@Test
	void testClearingValuesShowsError() {
		setupApp(SuiteSubApp.GRAPHING);
		TableValues tableValues = setupTableValues("x = {1, 2}");
		tableValues.getStatisticsViewModel().show(Mode.ONE_VARIABLE, 0);

		tableValues.clearValues();

		assertInstanceOf(Content.Error.class,
				tableValues.getStatisticsViewModel().getContent().get());
	}

	@Test
	void testClosesWhenSelectedColumnIsRemoved() {
		setupApp(SuiteSubApp.GRAPHING);
		TableValues tableValues = setupTableValues("x = {1, 2}", "y = {3, 4}");
		tableValues.getStatisticsViewModel().show(Mode.ONE_VARIABLE, 1);
		tableValues.hideColumn(tableValues.getEvaluatable(1));

		assertNull(tableValues.getStatisticsViewModel().getContent().get());
	}
}
