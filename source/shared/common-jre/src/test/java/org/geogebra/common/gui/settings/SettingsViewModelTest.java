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

package org.geogebra.common.gui.settings;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.gui.colorpicker.ColorHistory;
import org.geogebra.common.gui.colorpicker.ColorPickerModel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.properties.aliases.ColorProperty;
import org.geogebra.common.properties.impl.DefaultColorValues;
import org.geogebra.common.properties.impl.undo.UndoSavingPropertyObserver;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SettingsViewModelTest extends BaseAppTestSetup {
	private static final GColor C1 = GColor.newColorRGB(0x670000);
	private static final GColor C2 = GColor.newColorRGB(0x9E4848);

	private ColorHistory history;
	private SettingsViewModel viewModel;
	private GeoElement a;
	private GeoElement b;

	@BeforeEach
	void setup() {
		setupApp(SuiteSubApp.GRAPHING);
		getKernel().setUndoActive(true);
		getKernel().initUndoInfo();
		a = evaluateGeoElement("A = (1, 2)");
		b = evaluateGeoElement("B = (3, 4)");
		b.setObjColor(C2);
		getApp().storeUndoInfo();
		history = suiteScope.colorHistory;
		viewModel = new SettingsViewModel(getApp());
	}

	private List<SettingsPage> stack() {
		return viewModel.getNavigationStack().get();
	}

	private SettingsPage top() {
		List<SettingsPage> stack = stack();
		return stack.get(stack.size() - 1);
	}

	private int undoHistorySize() {
		return getApp().getUndoManager().getHistorySize();
	}

	private ColorPickerModel openColorPicker(GeoElement element) {
		ColorProperty property = suiteScope.geoElementPropertiesFactory.createObjectColorProperty(
				getLocalization(), List.of(element), DefaultColorValues.BRIGHT);
		assertNotNull(property);
		property.addValueObserver(new UndoSavingPropertyObserver(getApp().getUndoManager()));
		viewModel.openColorPicker(property);
		return ((ColorPickerPage) top()).getModel();
	}

	private ColorPickerModel openColorPickerFromObjectSettings(GeoElement element) {
		viewModel.showObjectSettings(List.of(element));
		return openColorPicker(element);
	}

	@Test
	void showAppSettingsShowsSinglePropertiesPage() {
		viewModel.showAppSettings(false);
		assertEquals(1, stack().size());
		assertInstanceOf(PropertiesPage.class, top());
	}

	@Test
	void showObjectSettingsWithEmptySelectionKeepsCurrentPages() {
		viewModel.showAppSettings(false);
		List<SettingsPage> before = stack();
		viewModel.showObjectSettings(List.of());
		assertEquals(before, stack());
	}

	@Test
	void openColorPickerFromSettingsPushesPage() {
		ColorPickerModel model = openColorPickerFromObjectSettings(a);
		assertEquals(2, stack().size());
		assertTrue(((ColorPickerPage) top()).isBackNavigable());
		assertEquals(model.getTitle(), top().getTitle());
	}

	@Test
	void openColorPickerReplacesPickerOnTopAndFinishesItsSession() {
		openColorPickerFromObjectSettings(a).selectColor(C1);
		openColorPicker(b);
		assertEquals(2, stack().size());
		assertTrue(((ColorPickerPage) top()).isBackNavigable());
		assertFalse(history.hasLiveSlot());
	}

	@Test
	void navigateBackFinishesSessionWithOneUndoPoint() {
		int historySize = undoHistorySize();
		ColorPickerModel model = openColorPickerFromObjectSettings(a);
		model.selectColor(C1);
		model.selectColor(C2);
		viewModel.navigateBack();
		assertEquals(1, stack().size());
		assertInstanceOf(PropertiesPage.class, top());
		assertFalse(history.hasLiveSlot());
		assertEquals(historySize + 1, undoHistorySize());
	}

	@Test
	void closeFinishesSessionAndHides() {
		openColorPickerFromObjectSettings(a).selectColor(C1);
		viewModel.close();
		assertEquals(List.of(), stack());
		assertFalse(history.hasLiveSlot());
	}

	@Test
	void directEntryHasNoBack() {
		openColorPicker(a);
		assertEquals(1, stack().size());
		assertFalse(((ColorPickerPage) top()).isBackNavigable());
		viewModel.navigateBack();
		assertEquals(List.of(), stack());
	}

	@Test
	void selectionChangeReplacesPickerAndStoresUndoForPreviousObjectOnly() {
		GColor originalA = a.getObjectColor();
		openColorPickerFromObjectSettings(a).selectColor(C1);
		viewModel.showObjectSettings(List.of(b));
		assertEquals(1, stack().size());
		assertInstanceOf(PropertiesPage.class, top());
		assertFalse(history.hasLiveSlot());
		getKernel().undo();
		assertEquals(originalA, lookup("A").getObjectColor());
		assertEquals(C2, lookup("B").getObjectColor());
	}
}
