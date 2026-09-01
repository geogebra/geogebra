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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.contextmenu.TableValuesContextMenuActionHandler.PlotActionHandler;
import org.geogebra.common.contextmenu.TableValuesContextMenuItem.Item;
import org.geogebra.common.gui.view.table.TableValues;
import org.geogebra.common.gui.view.table.dialog.StatisticGroup;
import org.geogebra.common.gui.view.table.regression.RegressionSpecification;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.util.AttributedString;
import org.geogebra.test.BaseAppTestSetup;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

class TableValuesContextMenuActionHandlerTests extends BaseAppTestSetup
		implements TableValuesContextMenuActionHandler.Delegate {
	private boolean showTableValueCreatingDialogCalled;
	private GeoElement editedGeoElement;

	@Test
	void testOneVariableStatisticsShowsSheet() {
		setupApp(SuiteSubApp.GRAPHING);
		TableValues tableValues = setupTableValues("x = {1, 2, 3, 4}");

		TableValuesContextMenuActionHandler handler = new TableValuesContextMenuActionHandler(
				0, tableValues, getApp(), this);
		handler.handleSelectedItem(Item.Statistics1.toContextMenuItem());

		assertNotNull(tableValues.getStatisticsViewModel().getContent().get());
	}

	@Test
	void testTwoVariableStatisticsShowsSheet() {
		setupApp(SuiteSubApp.GRAPHING);
		TableValues tableValues = setupTableValues("x = {1, 2, 3, 4}", "y = {5, 6, 7, 8}");

		TableValuesContextMenuActionHandler handler = new TableValuesContextMenuActionHandler(
				1, tableValues, getApp(), this);
		handler.handleSelectedItem(Item.Statistics2.toContextMenuItem());

		assertNotNull(tableValues.getStatisticsViewModel().getContent().get());
	}

	@Test
	void testRegressionShowsSheet() {
		setupApp(SuiteSubApp.GRAPHING);
		TableValues tableValues = setupTableValues("x = {1, 2, 3, 4}", "y = {5, 6, 7, 8}");

		TableValuesContextMenuActionHandler handler = new TableValuesContextMenuActionHandler(
				1, tableValues, getApp(), this);
		handler.handleSelectedItem(Item.Regression.toContextMenuItem());

		assertNotNull(tableValues.getStatisticsViewModel().getContent().get());
	}

	@Test
	void testEditOnFirstColumn() {
		setupApp(SuiteSubApp.GRAPHING);
		TableValues tableValues = setupTableValues("x = {0, 1, 2, 3, 4, 5}");

		TableValuesContextMenuActionHandler handler = new TableValuesContextMenuActionHandler(
				0, tableValues, getApp(), this);
		handler.handleSelectedItem(Item.Edit.toContextMenuItem());

		assertTrue(showTableValueCreatingDialogCalled);
		assertNull(editedGeoElement);
	}

	@Test
	void testEditOnFunctionColumn() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoFunction geoFunction = evaluateGeoElement("f(x) = x");
		TableValues tableValues =
				setupTableValues(evaluateGeoElement("x = {0, 1, 2, 3, 4, 5}"), geoFunction);

		TableValuesContextMenuActionHandler handler =  new TableValuesContextMenuActionHandler(
				1, tableValues, getApp(), this);
		handler.handleSelectedItem(Item.Edit.toContextMenuItem());

		assertFalse(showTableValueCreatingDialogCalled);
		assertEquals(geoFunction, editedGeoElement);
	}

	@Test
	void testClearColumn() {
		setupApp(SuiteSubApp.GRAPHING);
		TableValues tableValues = setupTableValues("x = {0, 1, 2, 3, 4, 5}");

		TableValuesContextMenuActionHandler handler = new TableValuesContextMenuActionHandler(
				0, tableValues, getApp(), this);
		handler.handleSelectedItem(Item.ClearColumn.toContextMenuItem());

		assertTrue(Double.isNaN(tableValues.getTableValuesModel().getValueAt(0, 0)));
	}

	@Test
	void testRemoveFunctionColumn() {
		setupApp(SuiteSubApp.GRAPHING);
		GeoFunction geoFunction = evaluateGeoElement("f(x) = x");
		TableValues tableValues =
				setupTableValues(evaluateGeoElement("x = {0, 1, 2, 3, 4, 5}"), geoFunction);

		TableValuesContextMenuActionHandler handler = new TableValuesContextMenuActionHandler(
				1, tableValues, getApp(), this);
		handler.handleSelectedItem(Item.RemoveColumn.toContextMenuItem());

		assertEquals(-1, tableValues.getColumn(geoFunction));
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
	public void showStatisticsDialog(@NonNull String title, @NonNull AttributedString header,
			@NonNull List<StatisticGroup> statisticGroups) {
		// not needed for tests
	}

	@Override
	public void showRegressionDialog(@NonNull String title, @NonNull AttributedString header,
			@NonNull Map<RegressionSpecification, List<StatisticGroup>> regressionGroups,
			@Nullable PlotActionHandler plotActionHandler) {
		// not needed for tests
	}

	@Override
	public void showErrorDialog(@NonNull String title, @NonNull AttributedString header,
			@NonNull String errorMessage) {
		// not needed for tests
	}
}
