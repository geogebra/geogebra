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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
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

public class QuickStyleBarModelTest extends BaseAppTestSetup {

	private QuickStyleBarModel model;
	private QuickStyleBarModel.Delegate delegate;

	@BeforeEach
	public void setUp() {
		setupApp(SuiteSubApp.GRAPHING);
		model = new QuickStyleBarModel(getApp(), new GeoElementPropertiesFactory(),
				getLocalization(), new StylebarPositioner(getApp()));
		delegate = Mockito.mock(QuickStyleBarModel.Delegate.class);
		model.setDelegate(delegate);
	}

	@Test
	public void showCreatesButtonsForPoint() {
		model.show(List.of(evaluateGeoElement("(1,2)")));
		List<Button> expectedButtons = List.of(
				new Button.Color(),
				new Button.PointStyle(PropertyResource.ICON_POINT_STYLE_DOT)
		);
		assertButtonEqual(expectedButtons);
	}

	@Test
	public void showCreatesButtonsForFunction() {
		model.show(List.of(evaluateGeoElement("f(x)=x")));
		List<Button> expectedButtons = List.of(
				new Button.Color(),
				new Button.LineStyle(PropertyResource.ICON_LINE_TYPE_FULL),
				new Button.Fixing(true)
		);
		assertButtonEqual(expectedButtons);
	}

	@Test
	public void showCreatesOpacityButtonForImage() {
		model.show(List.of(new GeoImage(getKernel().getConstruction())));
		List<Button> expectedButtons = List.of(new Button.Opacity());
		assertButtonEqual(expectedButtons);
	}

	@Test
	public void hideClearsStateAndClosesSubmenu() {
		model.show(List.of(evaluateGeoElement("(1,2)")));
		model.onButtonPressed(new Button.Color());
		model.hide();
		assertButtonEqual(null);
		assertSubmenuIsEmpty();
	}

	@Test
	public void hideWhenAlreadyHiddenDoesNotNotify() {
		model.hide();
		verify(delegate, never()).onButtonsChanged(anyList());
		verify(delegate, never()).onSubmenuItemsChanged(anyList());
		verify(delegate, never()).onSelectedButtonChanged(anyInt());
	}

	@Test
	public void pressingColorButtonOpensSubmenuWithColorRow() {
		model.show(List.of(evaluateGeoElement("(1,2)")));
		model.onButtonPressed(new Button.Color());

		List<PropertyView> propertyViews = model.getSubmenuItems();
		assertNotNull(propertyViews);
		assertEquals(0, model.getSelectedButtonIndex());
		verify(delegate).onSelectedButtonChanged(0);
		verify(delegate).onSubmenuItemsChanged(propertyViews);
		assertEquals(1, propertyViews.size());
		assertInstanceOf(PropertyView.ColorSelectorRow.class, propertyViews.get(0));
	}
	
	@Test
	public void pressingTheColorButtonTwiceClosesTheSubmenu() {
		model.show(List.of(evaluateGeoElement("(1,2)")));
		model.onButtonPressed(new Button.Color());
		model.onButtonPressed(new Button.Color());
		assertSubmenuIsEmpty();
	}

	@Test
	public void pressingButtonWhenHiddenDoesNothing() {
		model.onButtonPressed(new Button.Color());
		assertNull(model.getSubmenuItems());
		verify(delegate, never()).onSubmenuItemsChanged(anyList());
	}

	@Test
	public void settingNonRangeSubmenuPropertyClosesSubmenu() {
		model.show(List.of(evaluateGeoElement("(1,2)")));
		model.onButtonPressed(new Button.Color());
		PropertyView.ColorSelectorRow colorSelectorRow =
				(PropertyView.ColorSelectorRow) model.getSubmenuItems().get(0);
		colorSelectorRow.setCustomColor(GColor.WHITE);
		assertSubmenuIsEmpty();
	}

	@Test
	public void settingRangeSubmenuPropertyKeepsSubmenuOpen() {
		model.show(List.of(evaluateGeoElement("f(x)=x")));
		model.onButtonPressed(new Button.LineStyle(PropertyResource.ICON_LINE_TYPE_FULL));
		PropertyView.Slider thicknessSlider = model.getSubmenuItems().stream()
				.filter(propertyView -> propertyView instanceof PropertyView.Slider)
				.map(propertyView -> (PropertyView.Slider) propertyView)
				.findFirst().orElseThrow();
		thicknessSlider.setValue(7);
		assertNotNull(model.getSubmenuItems());
	}

	@Test
	public void pressingFixingButtonTogglesLockedWithoutOpeningSubmenu() {
		GeoElement function = evaluateGeoElement("f(x)=x");
		model.show(List.of(function));
		boolean initiallyLocked = function.isLocked();
		assertEquals(function.isLocked(), findButton(Button.Fixing.class).isFixed());

		model.onButtonPressed(findButton(Button.Fixing.class));
		assertEquals(!initiallyLocked, function.isLocked());
		assertNull(model.getSubmenuItems());
		assertEquals(function.isLocked(), findButton(Button.Fixing.class).isFixed());
		model.onButtonPressed(findButton(Button.Fixing.class));
		assertEquals(initiallyLocked, function.isLocked());
	}

	@Test
	public void morePressedEmitsOpenObjectSettingsAndClosesSubmenu() {
		model.show(List.of(evaluateGeoElement("(1,2)")));
		model.onButtonPressed(new Button.Color());
		model.onMorePressed();
		verify(delegate).openObjectSettings();
		assertNull(model.getSubmenuItems());
		assertNotNull(model.getButtons());
	}

	@Test
	public void deletePressedDeletesSelectedElementsAndHides() {
		GeoElement point = evaluateGeoElement("(1,2)", GeoElement.class);
		getApp().getSelectionManager().addSelectedGeo(point);
		model.show(List.of(point));
		model.onDeletePressed();
		verify(delegate).closeObjectSettings();
		assertNull(model.getButtons());
		assertNull(lookup(point.getLabelSimple()));
	}

	@Test
	public void staleSubmenuPropertyObserverIsRemovedOnHide() {
		model.show(List.of(evaluateGeoElement("(1,2)")));
		model.onButtonPressed(new Button.Color());
		PropertyView.ColorSelectorRow staleColorSelectorRow =
				(PropertyView.ColorSelectorRow) model.getSubmenuItems().get(0);
		model.hide();
		model.show(List.of(evaluateGeoElement("(1,2)")));
		model.onButtonPressed(new Button.Color());
		// A value change on the previous submenu's property must not close the new submenu
		staleColorSelectorRow.setCustomColor(GColor.WHITE);
		assertNotNull(model.getSubmenuItems());
	}

	private <T extends Button> T findButton(Class<T> buttonType) {
		return model.getButtons().stream()
				.filter(buttonType::isInstance)
				.map(buttonType::cast)
				.findFirst().orElseThrow();
	}

	private void assertButtonEqual(List<Button> buttons) {
		assertEquals(buttons, model.getButtons());
		verify(delegate).onButtonsChanged(buttons);
	}

	private void assertSubmenuIsEmpty() {
		assertNull(model.getSubmenuItems());
		verify(delegate).onSubmenuItemsChanged(null);
	}
}
