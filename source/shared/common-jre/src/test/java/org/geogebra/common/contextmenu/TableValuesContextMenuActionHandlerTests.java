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

package org.geogebra.common.contextmenu;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.contextmenu.TableValuesContextMenuActionHandler.PlotActionHandler;
import org.geogebra.common.gui.view.table.TableValues;
import org.geogebra.common.gui.view.table.dialog.StatisticGroup;
import org.geogebra.common.gui.view.table.regression.RegressionSpecification;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.restrictions.FeatureRestriction;
import org.geogebra.common.util.AttributedString;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.Test;

class TableValuesContextMenuActionHandlerTests extends BaseAppTestSetup
		implements TableValuesContextMenuActionHandler.Delegate {
	private boolean showTableValueCreatingDialogCalled = false;
	private GeoElement editedGeoElement = null;
	private String capturedRegressionTitle;
	private AttributedString capturedRegressionHeader;
	private Map<RegressionSpecification, List<StatisticGroup>> capturedRegressionGroups;
	private PlotActionHandler capturedPlotActionHandler;
	private String capturedErrorTitle;
	private AttributedString capturedErrorHeader;
	private String capturedErrorMessage;
	private String capturedStatisticsTitle;
	private AttributedString capturedStatisticsHeader;
	private List<StatisticGroup> capturedStatisticsGroups;

	@Test
	void testEditOnFirstColumn() {
		setupApp(SuiteSubApp.GRAPHING);
		TableValues tableValues = setupTableValues("x = {0, 1, 2, 3, 4, 5}");

		TableValuesContextMenuActionHandler handler = new TableValuesContextMenuActionHandler(
				0, tableValues, getApp(), getApp().getLocalization(), this);
		handler.handleSelectedItem(TableValuesContextMenuItem.Item.Edit.toContextMenuItem());

		assertTrue(showTableValueCreatingDialogCalled);
		assertNull(editedGeoElement);
	}

	@Test
	void testEditOnFunctionColumn() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoFunction geoFunction = evaluateGeoElement("f(x) = x");
		TableValues tableValues =
				setupTableValues(evaluateGeoElement("x = {0, 1, 2, 3, 4, 5}"), geoFunction);

		TableValuesContextMenuActionHandler handler = new TableValuesContextMenuActionHandler(
				1, tableValues, getApp(), getLocalization(), this);
		handler.handleSelectedItem(TableValuesContextMenuItem.Item.Edit.toContextMenuItem());

		assertFalse(showTableValueCreatingDialogCalled);
		assertEquals(geoFunction, editedGeoElement);
	}

	@Test
	void testClearColumn() {
		setupApp(SuiteSubApp.GRAPHING);
		TableValues tableValues = setupTableValues("x = {0, 1, 2, 3, 4, 5}");
		assertEquals(0.0, tableValues.getTableValuesModel().getValueAt(0, 0));

		TableValuesContextMenuActionHandler handler = new TableValuesContextMenuActionHandler(
				0, tableValues, getApp(), getLocalization(), this);
		handler.handleSelectedItem(TableValuesContextMenuItem.Item.ClearColumn.toContextMenuItem());

		assertTrue(Double.isNaN(tableValues.getTableValuesModel().getValueAt(0, 0)));
	}

	@Test
	void testRemoveFunctionColumn() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoFunction geoFunction = evaluateGeoElement("f(x) = x");
		TableValues tableValues =
				setupTableValues(evaluateGeoElement("x = {0, 1, 2, 3, 4, 5}"), geoFunction);

		TableValuesContextMenuActionHandler handler = new TableValuesContextMenuActionHandler(
				1, tableValues, getApp(), getLocalization(), this);
		handler.handleSelectedItem(TableValuesContextMenuItem.Item
				.RemoveColumn.toContextMenuItem());

		assertEquals(-1, tableValues.getColumn(geoFunction));
	}

	@Test
	void testOneVariableStatisticsShowsStatisticsDialog() {
		setupApp(SuiteSubApp.GRAPHING);
		TableValues tableValues = setupTableValues("x = {1, 2, 3, 4}", "y_1 = {5, 6, 7, 8}");
		TableValuesContextMenuActionHandler handler = new TableValuesContextMenuActionHandler(
				1, tableValues, getApp(), getLocalization(), this);
		handler.handleSelectedItem(TableValuesContextMenuItem.Item.Statistics1.toContextMenuItem());

		assertEquals(getLocalization().getMenu("1VariableStatistics"), capturedStatisticsTitle);
		assertEquals("Column y1", capturedStatisticsHeader.getRawValue());
		assertFalse(capturedStatisticsGroups.isEmpty());
	}

	@Test
	void testOneVariableStatisticsShowsErrorDialogWhenInsufficientData() {
		setupApp(SuiteSubApp.GRAPHING);
		TableValues tableValues = setupTableValues("x = {1, 2, 3, 4}", "y_1 = {5}");
		TableValuesContextMenuActionHandler handler = new TableValuesContextMenuActionHandler(
				1, tableValues, getApp(), getLocalization(), this);
		handler.handleSelectedItem(TableValuesContextMenuItem.Item.Statistics1.toContextMenuItem());

		assertEquals(getLocalization().getMenu("1VariableStatistics"), capturedErrorTitle);
		assertEquals("Column y1", capturedErrorHeader.getRawValue());
		assertEquals(getLocalization().getMenu("StatsDialog.NoDataMsg1VarStats"),
				capturedErrorMessage);
	}

	@Test
	void testTwoVariableStatisticsShowsStatisticsDialog() {
		setupApp(SuiteSubApp.GRAPHING);
		TableValues tableValues = setupTableValues("x = {1, 2, 3, 4}", "y_1 = {5, 6, 7, 8}");
		TableValuesContextMenuActionHandler handler = new TableValuesContextMenuActionHandler(
				1, tableValues, getApp(), getLocalization(), this);
		handler.handleSelectedItem(TableValuesContextMenuItem.Item.Statistics2.toContextMenuItem());

		assertEquals(getLocalization().getMenu("2VariableStatistics"), capturedStatisticsTitle);
		assertEquals("Column x y1", capturedStatisticsHeader.getRawValue());
		assertFalse(capturedStatisticsGroups.isEmpty());
	}

	@Test
	void testTwoVariableStatisticsShowsErrorDialogWhenInsufficientData() {
		setupApp(SuiteSubApp.GRAPHING);
		TableValues tableValues = setupTableValues("x = {1}", "y_1 = {5}");
		TableValuesContextMenuActionHandler handler = new TableValuesContextMenuActionHandler(
				1, tableValues, getApp(), getLocalization(), this);
		handler.handleSelectedItem(TableValuesContextMenuItem.Item.Statistics2.toContextMenuItem());

		assertEquals(getLocalization().getMenu("2VariableStatistics"), capturedErrorTitle);
		assertEquals("Column x y1", capturedErrorHeader.getRawValue());
		assertEquals(getLocalization().getMenu("StatsDialog.NoDataMsg2VarStats"),
				capturedErrorMessage);
	}

	@Test
	void testRegressionShowsRegressionDialog() {
		setupApp(SuiteSubApp.GRAPHING);
		TableValues tableValues = setupTableValues("x = {1, 2, 3, 4}", "y_1 = {5, 6, 7, 8}");
		TableValuesContextMenuActionHandler handler = new TableValuesContextMenuActionHandler(
				1, tableValues, getApp(), getLocalization(), this);
		handler.handleSelectedItem(TableValuesContextMenuItem.Item.Regression.toContextMenuItem());

		assertEquals(getLocalization().getMenu("Regression"), capturedRegressionTitle);
		assertEquals("Column y1", capturedRegressionHeader.getRawValue());
		assertFalse(capturedRegressionGroups.isEmpty());
		capturedRegressionGroups.values().forEach(groups -> assertFalse(groups.isEmpty()));
	}

	@Test
	void testRegressionShowsErrorDialogWhenInsufficientData() {
		setupApp(SuiteSubApp.GRAPHING);
		TableValues tableValues = setupTableValues("x = {1}", "y_1 = {5}");
		TableValuesContextMenuActionHandler handler = new TableValuesContextMenuActionHandler(
				1, tableValues, getApp(), getLocalization(), this);
		handler.handleSelectedItem(TableValuesContextMenuItem.Item.Regression.toContextMenuItem());

		assertEquals(getLocalization().getMenu("Regression"), capturedErrorTitle);
		assertEquals("Column y1", capturedErrorHeader.getRawValue());
		assertEquals(getLocalization().getMenu("StatsDialog.NoDataMsgRegression"),
				capturedErrorMessage);
	}

	@Test
	void testRegressionPlotAction() {
		setupApp(SuiteSubApp.GRAPHING);
		TableValues tableValues = setupTableValues("x = {1, 2, 3, 4}", "y_1 = {5, 6, 7, 8}");
		TableValuesContextMenuActionHandler handler = new TableValuesContextMenuActionHandler(
				1, tableValues, getApp(), getLocalization(), this);
		handler.handleSelectedItem(TableValuesContextMenuItem.Item.Regression.toContextMenuItem());

		int constructionSizeBefore =
				getKernel().getConstruction().getGeoSetConstructionOrder().size();
		RegressionSpecification spec = capturedRegressionGroups.keySet().iterator().next();
		capturedPlotActionHandler.onPlotButtonPressed(spec);
		int constructionSizeAfter =
				getKernel().getConstruction().getGeoSetConstructionOrder().size();

		assertTrue(constructionSizeAfter > constructionSizeBefore);
	}

	@Test
	void testRegressionPlotActionIsNullInMmsMode() {
		setupApp(SuiteSubApp.GRAPHING);
		getApp().getRegressionSpecBuilder().applyRestrictions(
				Set.of(FeatureRestriction.CUSTOM_MMS_REGRESSION_MODELS));
		TableValues tableValues = setupTableValues("x = {1, 2, 3, 4}", "y_1 = {5, 6, 7, 8}");
		TableValuesContextMenuActionHandler handler = new TableValuesContextMenuActionHandler(
				1, tableValues, getApp(), getLocalization(), this);
		handler.handleSelectedItem(TableValuesContextMenuItem.Item.Regression.toContextMenuItem());

		assertNull(capturedPlotActionHandler);
	}

	@Override
	public void showTableValuesDialog() {
		showTableValueCreatingDialogCalled = true;
	}

	@Override
	public void startEditingAlgebraViewItem(GeoElement geoElement) {
		editedGeoElement = geoElement;
	}

	@Override
	public void startDataImport() {
		// not needed for tests
	}

	@Override
	public void showStatisticsDialog(@Nonnull String title, @Nonnull AttributedString header,
			@Nonnull List<StatisticGroup> statisticGroups) {
		capturedStatisticsTitle = title;
		capturedStatisticsHeader = header;
		capturedStatisticsGroups = statisticGroups;
	}

	@Override
	public void showRegressionDialog(@Nonnull String title, @Nonnull AttributedString header,
			@Nonnull Map<RegressionSpecification, List<StatisticGroup>> regressionGroups,
			@CheckForNull PlotActionHandler plotActionHandler) {
		capturedRegressionTitle = title;
		capturedRegressionHeader = header;
		capturedRegressionGroups = regressionGroups;
		capturedPlotActionHandler = plotActionHandler;
	}

	@Override
	public void showErrorDialog(@Nonnull String title, @Nonnull AttributedString header,
			@Nonnull String errorMessage) {
		capturedErrorTitle = title;
		capturedErrorHeader = header;
		capturedErrorMessage = errorMessage;
	}
}
