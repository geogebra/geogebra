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
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.gui.colorpicker.ColorHistory;
import org.geogebra.common.gui.colorpicker.ColorPickerModel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.properties.PropertyView;
import org.geogebra.common.properties.aliases.ColorProperty;
import org.geogebra.common.properties.impl.DefaultColorValues;
import org.geogebra.common.properties.impl.undo.UndoSavingPropertyObserver;
import org.geogebra.common.util.ImageManagerCommon;
import org.geogebra.test.BaseAppTestSetup;
import org.geogebra.test.annotation.Issue;
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
		getApp().setImageManager(new ImageManagerCommon());
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

	private PropertyView.TabbedPageSelector properties() {
		return ((PropertiesPage) top()).getTabbedPageSelectorState().get();
	}

	private List<PropertyView> dataViews() {
		PropertyView.TabbedPageSelector root = properties();
		int tab = root.getTabTitles().indexOf(getLocalization().getMenu("Data"));
		return ((PropertyView.RelatedPropertyViewCollection)
						root.getPageContents(tab).get(0))
				.getPropertyViews();
	}

	@Test
	@Issue("APPS-8062")
	void histogramInputTypeReconstructsOtherViews() {
		GeoElement histogram = evaluateGeoElement("h = Histogram({0, 1, 2, 3}, {2, 3, 1})");
		viewModel.showObjectSettings(List.of(histogram));
		PropertyView rawDataView = dataViews().get(3);
		((PropertyView.Dropdown) dataViews().get(0)).setSelectedItemIndex(0);

		assertNotSame(rawDataView, dataViews().get(3));
		assertTrue(dataViews().get(3).isVisible());
	}

	@Test
	void switchingHistogramBackToHeightsReconstructsHiddenRawDataView() {
		GeoElement histogram = evaluateGeoElement("h = Histogram({0, 1, 2, 3}, {2, 3, 1}, false)");
		viewModel.showObjectSettings(List.of(histogram));
		((PropertyView.TextField) dataViews().get(3)).setValue("{1, 2}");
		((PropertyView.Dropdown) dataViews().get(0)).setSelectedItemIndex(1);

		assertFalse(dataViews().get(3).isVisible());
		assertEquals(
				"Histogram({0, 1, 2, 3}, {1, 2})", lookup("h").getDefinition(StringTemplate.testTemplate));
	}

	@Test
	void xmlReconstructionSupportsIntermediateMixedChartSelection() {
		GeoElement first = evaluateGeoElement("c1 = BarChart({1, 2}, {3, 4})");
		GeoElement second = evaluateGeoElement("c2 = BarChart({1, 2}, {5, 6})");
		evaluateGeoElement("d = c1 + 1");
		viewModel.showObjectSettings(List.of(first, second));
		editGeoElement(first, "BoxPlot(1, 2, {1, 2, 3}, false)");

		assertNotSame(first, lookup("c1"));
		assertNotSame(second, lookup("c2"));
		assertFalse(properties().getTabTitles().contains(getLocalization().getMenu("Data")));
	}

	@Test
	void replacementPublishesNewTreeWithoutReplacingNavigationStack() {
		viewModel.showObjectSettings(List.of(a));
		PropertiesPage page = (PropertiesPage) top();
		List<SettingsPage> originalStack = stack();
		AtomicInteger updates = new AtomicInteger();
		page.getTabbedPageSelectorState().subscribe(root -> updates.incrementAndGet());
		evaluateGeoElement("A = true");

		assertEquals(1, updates.get());
		assertSame(originalStack, stack());
		assertSame(page, top());
	}

	@Test
	void reconstructedStyleChangesReplacement() {
		viewModel.showObjectSettings(List.of(a));
		evaluateGeoElement("A = Circle((0, 0), 2)");
		int styleTab =
				properties().getTabTitles().indexOf(getLocalization().getMenu("Properties.Style"));
		PropertyView.ExpandableList style =
				(PropertyView.ExpandableList) properties().getPageContents(styleTab).get(0);
		((PropertyView.ColorSelectorRow) style.getItems().get(0)).setCustomColor(C1);

		assertEquals(C1, lookup("A").getObjectColor());
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
