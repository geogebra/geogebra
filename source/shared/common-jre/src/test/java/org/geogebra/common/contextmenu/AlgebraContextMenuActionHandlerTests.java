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

import javax.annotation.Nonnull;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.gui.view.table.InvalidValuesException;
import org.geogebra.common.gui.view.table.TableValues;
import org.geogebra.common.gui.view.table.TableValuesView;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.main.PreviewFeature;
import org.geogebra.common.properties.PropertyView;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AlgebraContextMenuActionHandlerTests extends BaseAppTestSetup
		implements AlgebraContextMenuActionHandler.Delegate {
	private boolean clearAlgebraInputCalled = false;
	private boolean showTableValuesViewCalled = false;
	private boolean showTableValueCreatingDialogCalled = false;
	private int scrolledToColumnIndex = -1;
	private boolean showObjectPropertiesCalled = false;
	private boolean showOldObjectPropertiesCalled = false;

	@BeforeEach
	void setup() {
		setupApp(SuiteSubApp.GRAPHING);
	}

	@Test
	void testRemovingInvalidAlgebraViewInput() {
		AlgebraContextMenuActionHandler contextMenuActionHandler =
				new AlgebraContextMenuActionHandler(getApp(), new TableValuesView(getKernel()),
						null, this);
		contextMenuActionHandler.handleSelectedItem(AlgebraContextMenuItem.Delete);
		assertTrue(clearAlgebraInputCalled);
	}

	@Test
	void testRemovingValidAlgebraViewItem() {
		GeoElement geoElement = evaluateGeoElement("A = (1, 2)");
		AlgebraContextMenuActionHandler contextMenuActionHandler =
				new AlgebraContextMenuActionHandler(getApp(), new TableValuesView(getKernel()),
						geoElement, this);
		assertNotNull(lookup("A"));
		contextMenuActionHandler.handleSelectedItem(AlgebraContextMenuItem.Delete);
		assertFalse(clearAlgebraInputCalled);
		assertNull(lookup("A"));
	}

	@Test
	void testCreatingTableValuesForFunctionWithEmptyTableValuesView() {
		TableValues tableValues = new TableValuesView(getKernel());
		GeoFunction geoFunction = evaluateGeoElement("f(x) = x");
		AlgebraContextMenuActionHandler contextMenuActionHandler =
				new AlgebraContextMenuActionHandler(getApp(), tableValues, geoFunction, this);
		contextMenuActionHandler.handleSelectedItem(AlgebraContextMenuItem.CreateTableValues);
		assertTrue(showTableValuesViewCalled);
		assertTrue(showTableValueCreatingDialogCalled);
	}

	@Test
	void testCreatingTableValuesForFunctionWithPreviouslyCreatedTableValues()
			throws InvalidValuesException {
		TableValues tableValues = new TableValuesView(getKernel());
		getKernel().attach(tableValues);
		tableValues.setValues(0, 2, 1);
		GeoFunction geoFunction = evaluateGeoElement("f(x) = x^2");

		AlgebraContextMenuActionHandler contextMenuActionHandler =
				new AlgebraContextMenuActionHandler(getApp(), tableValues, geoFunction, this);
		contextMenuActionHandler.handleSelectedItem(AlgebraContextMenuItem.CreateTableValues);

		assertEquals(0.0, tableValues.getTableValuesModel().getValueAt(0, 1));
		assertEquals(1.0, tableValues.getTableValuesModel().getValueAt(1, 1));
		assertEquals(4.0, tableValues.getTableValuesModel().getValueAt(2, 1));
		assertTrue(showTableValuesViewCalled);
		assertFalse(showTableValueCreatingDialogCalled);
	}

	@Test
	void testCreatingTableValuesScrollsToColumnAlreadyInTable()
			throws InvalidValuesException {
		TableValues tableValues = new TableValuesView(getKernel());
		getKernel().attach(tableValues);
		tableValues.setValues(0, 5, 1);
		GeoFunction geoFunction = evaluateGeoElement("f(x) = x");
		tableValues.showColumn(geoFunction);

		AlgebraContextMenuActionHandler handler =
				new AlgebraContextMenuActionHandler(getApp(), tableValues, geoFunction, this);
		handler.handleSelectedItem(AlgebraContextMenuItem.CreateTableValues);

		assertTrue(showTableValuesViewCalled);
		assertFalse(showTableValueCreatingDialogCalled);
		assertEquals(1, scrolledToColumnIndex);
	}

	@Test
	void testSettingsShowsOldObjectPropertiesWithPreviewFeaturesDisabled() {
		GeoElement geoElement = evaluateGeoElement("A = (1, 2)");
		AlgebraContextMenuActionHandler handler = new AlgebraContextMenuActionHandler(getApp(),
				new TableValuesView(getKernel()), geoElement, this);
		handler.handleSelectedItem(AlgebraContextMenuItem.Settings);
		assertTrue(showOldObjectPropertiesCalled);
		assertFalse(showObjectPropertiesCalled);
	}

	@Test
	void testSettingsShowsNewObjectPropertiesWithPreviewFeaturesEnabled() {
		PreviewFeature.setPreviewFeaturesEnabled(true);
		GeoElement geoElement = evaluateGeoElement("A = (1, 2)");
		AlgebraContextMenuActionHandler handler = new AlgebraContextMenuActionHandler(
				getApp(), new TableValuesView(getKernel()), geoElement, this);
		handler.handleSelectedItem(AlgebraContextMenuItem.Settings);
		assertFalse(showOldObjectPropertiesCalled);
		assertTrue(showObjectPropertiesCalled);
		PreviewFeature.setPreviewFeaturesEnabled(false);
	}

	@Test
	void testUndoPointAddedForAddLabelItem() {
		setupApp(SuiteSubApp.CAS);
		mockedCasGiac.memorize("Evaluate(5)", "5");
		mockedCasGiac.memorize("Round(5, 13)", "5.0");
		getApp().setUndoActive(true);
		assertTrue(getKernel().getConstruction().isEmpty());
		GeoElement geoElement = evaluateGeoElement("5");
		AlgebraContextMenuActionHandler handler = new AlgebraContextMenuActionHandler(
				getApp(), new TableValuesView(getKernel()), geoElement, this);

		assertNull(lookup("a"));
		assertFalse(getKernel().getConstruction().isEmpty());

		handler.handleSelectedItem(AlgebraContextMenuItem.AddLabel);
		assertNotNull(lookup("a"));
		assertFalse(getKernel().getConstruction().isEmpty());

		getKernel().undo();
		assertNull(lookup("a"));
		assertFalse(getKernel().getConstruction().isEmpty());
	}

	@Override
	public void clearAlgebraInput() {
		clearAlgebraInputCalled = true;
	}

	@Override
	public void showTableValuesDialog(GeoElement geoElement) {
		showTableValueCreatingDialogCalled = true;
	}

	@Override
	public void scrollToTableValuesColumn(int columnIndex) {
		scrolledToColumnIndex = columnIndex;
	}

	@Override
	public void showTableValuesView() {
		showTableValuesViewCalled = true;
	}

	@Override
	public void addFormulaToAlgebraView(@Nonnull String formula) {
		// not needed for tests
	}

	@Override
	public void showOldObjectProperties() {
		showOldObjectPropertiesCalled = true;
	}

	@Override
	public void showObjectProperties(@Nonnull PropertyView.TabbedPageSelector tabbedPageSelector) {
		showObjectPropertiesCalled = true;
	}
}
