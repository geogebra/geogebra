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

package org.geogebra.common.euclidian;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.QuickStyleBarModel.Button;
import org.geogebra.common.gui.stylebar.StylebarPositioner;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.PropertyView;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class QuickStyleBarModelTest extends BaseAppTestSetup {

	private QuickStyleBarModel model;
	private QuickStyleBarModel.Delegate delegate;

	@BeforeEach
	void setUp() {
		setupApp(SuiteSubApp.GRAPHING);
		model = new QuickStyleBarModel(getApp(), new GeoElementPropertiesFactory(),
				getLocalization(), new StylebarPositioner(getApp()));
		delegate = Mockito.mock(QuickStyleBarModel.Delegate.class);
		model.setDelegate(delegate);
	}

	@Test
	void showCreatesButtonsForPoint() {
		model.show(List.of(evaluateGeoElement("(1,2)")));
		List<Button> expectedButtons = List.of(
				new Button.Color(),
				new Button.PointStyle(PropertyResource.ICON_POINT_STYLE_DOT)
		);
		assertEquals(expectedButtons, model.getButtons().get());
	}

	@Test
	void showCreatesButtonsForFunction() {
		model.show(List.of(evaluateGeoElement("f(x)=x")));
		List<Button> expectedButtons = List.of(
				new Button.Color(),
				new Button.LineStyle(PropertyResource.ICON_LINE_TYPE_FULL),
				new Button.Fixing(true)
		);
		assertEquals(expectedButtons, model.getButtons().get());
	}

	@Test
	void showCreatesOpacityButtonForImage() {
		model.show(List.of(new GeoImage(getKernel().getConstruction())));
		List<Button> expectedButtons = List.of(new Button.Opacity());
		assertEquals(expectedButtons, model.getButtons().get());
	}

	@Test
	void hideClearsStateAndClosesSubmenu() {
		model.show(List.of(evaluateGeoElement("(1,2)")));
		model.onButtonPressed(new Button.Color());
		model.hide();
		assertNull(model.getButtons().get());
		assertSubmenuIsEmpty();
	}

	@Test
	void pressingColorButtonOpensSubmenuWithColorRow() {
		model.show(List.of(evaluateGeoElement("(1,2)")));
		model.onButtonPressed(new Button.Color());

		List<PropertyView> propertyViews = model.getSubmenuItems().get();
		assertNotNull(propertyViews);
		assertEquals(0, model.getSelectedButtonIndex().get());
		assertEquals(1, propertyViews.size());
		assertInstanceOf(PropertyView.ColorSelectorRow.class, propertyViews.get(0));
	}

	@Test
	void pressingTheColorButtonTwiceClosesTheSubmenu() {
		model.show(List.of(evaluateGeoElement("(1,2)")));
		model.onButtonPressed(new Button.Color());
		model.onButtonPressed(new Button.Color());
		assertSubmenuIsEmpty();
	}

	@Test
	void pressingButtonWhenHiddenDoesNothing() {
		model.onButtonPressed(new Button.Color());
		assertNull(model.getSubmenuItems().get());
	}

	@Test
	void settingNonRangeSubmenuPropertyClosesSubmenu() {
		model.show(List.of(evaluateGeoElement("(1,2)")));
		model.onButtonPressed(new Button.Color());
		PropertyView.ColorSelectorRow colorSelectorRow =
				(PropertyView.ColorSelectorRow) model.getSubmenuItems().get().get(0);
		colorSelectorRow.setCustomColor(GColor.WHITE);
		assertSubmenuIsEmpty();
	}

	@Test
	void settingRangeSubmenuPropertyKeepsSubmenuOpen() {
		model.show(List.of(evaluateGeoElement("f(x)=x")));
		model.onButtonPressed(new Button.LineStyle(PropertyResource.ICON_LINE_TYPE_FULL));
		PropertyView.Slider thicknessSlider = model.getSubmenuItems().get().stream()
				.filter(propertyView -> propertyView instanceof PropertyView.Slider)
				.map(propertyView -> (PropertyView.Slider) propertyView)
				.findFirst().orElseThrow();
		thicknessSlider.setValue(7);
		assertNotNull(model.getSubmenuItems().get());
	}

	@Test
	void pressingFixingButtonTogglesLockedWithoutOpeningSubmenu() {
		GeoElement function = evaluateGeoElement("f(x)=x");
		model.show(List.of(function));
		boolean initiallyLocked = function.isLocked();
		assertEquals(function.isLocked(), findButton(Button.Fixing.class).isFixed());

		model.onButtonPressed(findButton(Button.Fixing.class));
		assertEquals(!initiallyLocked, function.isLocked());
		assertNull(model.getSubmenuItems().get());
		assertEquals(function.isLocked(), findButton(Button.Fixing.class).isFixed());
		model.onButtonPressed(findButton(Button.Fixing.class));
		assertEquals(initiallyLocked, function.isLocked());
	}

	@Test
	void morePressedEmitsOpenObjectSettingsAndClosesSubmenu() {
		List<GeoElement> elements = List.of(evaluateGeoElement("(1,2)"));
		model.show(elements);
		model.onButtonPressed(new Button.Color());
		model.onMorePressed();
		verify(delegate).openObjectSettings(elements);
		assertNull(model.getSubmenuItems().get());
		assertNotNull(model.getButtons().get());
	}

	@Test
	void deletePressedDeletesSelectedElementsAndHides() {
		GeoElement point = evaluateGeoElement("(1,2)", GeoElement.class);
		getApp().getSelectionManager().addSelectedGeo(point);
		model.show(List.of(point));
		model.onDeletePressed();
		verify(delegate).closeObjectSettings();
		assertNull(model.getButtons().get());
		assertNull(lookup(point.getLabelSimple()));
	}

	@Test
	void staleSubmenuPropertyObserverIsRemovedOnHide() {
		model.show(List.of(evaluateGeoElement("(1,2)")));
		model.onButtonPressed(new Button.Color());
		PropertyView.ColorSelectorRow staleColorSelectorRow =
				(PropertyView.ColorSelectorRow) model.getSubmenuItems().get().get(0);
		model.hide();
		model.show(List.of(evaluateGeoElement("(1,2)")));
		model.onButtonPressed(new Button.Color());
		// A value change on the previous submenu's property must not close the new submenu
		staleColorSelectorRow.setCustomColor(GColor.WHITE);
		assertNotNull(model.getSubmenuItems().get());
	}

	private <T extends Button> T findButton(Class<T> buttonType) {
		return model.getButtons().get().stream()
				.filter(buttonType::isInstance)
				.map(buttonType::cast)
				.findFirst().orElseThrow();
	}

	private void assertSubmenuIsEmpty() {
		assertNull(model.getSubmenuItems().get());
		assertNull(model.getSelectedButtonIndex().get());
	}
}
