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

import static org.geogebra.common.properties.PropertyViewFactory.propertyViewListOf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.gui.stylebar.StylebarPositioner;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.IconsEnumeratedProperty;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.PropertyValueObserver;
import org.geogebra.common.properties.PropertyView;
import org.geogebra.common.properties.RangeProperty;
import org.geogebra.common.properties.ValuedProperty;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.common.properties.factory.PropertiesArray;
import org.geogebra.common.properties.impl.DefaultColorValues;

import com.google.j2objc.annotations.Weak;

/**
 * Platform-independent model of the quick style bar, the small popup shown next to
 * selected elements in the euclidean view. It maintains the state of the bar and its.
 */
public class QuickStyleBarModel {

	private @Weak @CheckForNull Delegate delegate;

	private final App app;
	private final Localization localization;
	private final StylebarPositioner positioner;
	private final GeoElementPropertiesFactory propertiesFactory;

	private List<GeoElement> elements;
	private BooleanProperty isFixedProperty;
	private @CheckForNull List<Button> buttons;
	private @CheckForNull Integer selectedButtonIndex;
	private PropertiesArray submenuProperties;
	private @CheckForNull List<PropertyView> submenuItems;

	private final PropertyValueObserver<?> hideSubmenuOnValueChange = property -> hideSubmenu();

	/** A button of the quick style bar, opening a submenu or toggling a value when pressed. */
	public sealed interface Button permits Button.Color, Button.LineStyle,
			Button.PointStyle, Button.Opacity, Button.Fixing {
		/** Opens the color submenu. */
		record Color() implements Button {}

		/** Opens the line style submenu, displaying the current line style's {@code icon}. */
		record LineStyle(PropertyResource icon) implements Button {}

		/** Opens the point style submenu, displaying the current point style's {@code icon}. */
		record PointStyle(PropertyResource icon) implements Button {}

		/** Opens the opacity submenu (images only). */
		record Opacity() implements Button {}

		/** Toggles whether the selected elements are fixed. */
		record Fixing(boolean isFixed) implements Button {}
	}

	/** Receives notifications about state changes of the quick style bar. */
	public interface Delegate {
		/** 
		 * Called when the displayed buttons change.
		 * @param buttons the displayed buttons, or {@code null} when the bar is hidden
		 */
		void onButtonsChanged(@CheckForNull List<Button> buttons);

		/** 
		 * Called when the submenu items change. 
		 * @param items the property view items, or {@code null} when the submenu is hidden
		 */
		void onSubmenuItemsChanged(@CheckForNull List<PropertyView> items);

		/** 
		 * Called when the selected button changes.
		 * @param selectedButton selected button index, or {code null} when the submenu is hidden.
		 */
		void onSelectedButtonChanged(@CheckForNull Integer selectedButton);

		/** Called to request opening the object settings. */
		void openObjectSettings();

		/** Called to request closing the object settings. */
		void closeObjectSettings();
	}

	/**
	 * @param app the app
	 * @param propertiesFactory factory used to create the style properties
	 * @param localization localization for property names
	 * @param positioner calculates the popup position on the canvas
	 */
	public QuickStyleBarModel(App app, GeoElementPropertiesFactory propertiesFactory,
			Localization localization, StylebarPositioner positioner) {
		this.app = app;
		this.localization = localization;
		this.positioner = positioner;
		this.propertiesFactory = propertiesFactory;
	}

	/**
	 * @param delegate delegate notified about state changes, or {@code null} to detach
	 */
	public void setDelegate(@CheckForNull Delegate delegate) {
		this.delegate = delegate;
	}

	/**
	 * @return the current state, or {@code null} if the quick style bar is hidden
	 */
	public @CheckForNull List<Button> getButtons() {
		return buttons;
	}

	/**
	 * @return the current submenu state, or {@code null} if the submenu is closed
	 */
	public @CheckForNull List<PropertyView> getSubmenuItems() {
		return submenuItems;
	}

	/**
	 * @return index of the button whose submenu is open, or {@code null} if none is selected
	 */
	public @CheckForNull Integer getSelectedButtonIndex() {
		return selectedButtonIndex;
	}

	/**
	 * Shows the quick style bar for the given elements.
	 * @param elements the selected elements, determining the available buttons
	 */
	public void show(@Nonnull List<GeoElement> elements) {
		this.elements = elements;
		setButtons(createButtons(elements));
	}

	/**
	 * Hides the quick style bar together with its submenu. Does nothing if already hidden.
	 */
	public void hide() {
		setButtons(null);
		elements = null;
		isFixedProperty = null;
		hideSubmenu();
	}

	/**
	 * Deletes the selected elements and hides the quick style bar.
	 */
	public void onDeletePressed() {
		app.deleteSelectedObjects(false);
		if (delegate != null) {
			delegate.closeObjectSettings();
		}
		hide();
	}

	/**
	 * Requests opening the object settings and closes the submenu.
	 */
	public void onMorePressed() {
		if (delegate != null) {
			delegate.openObjectSettings();
		}
		hideSubmenu();
	}

	/**
	 * Handles a press on one of the quick style bar's buttons. Does nothing if hidden.
	 * @param button the pressed button
	 */
	public void onButtonPressed(Button button) {
		List<Button> currentButtons = buttons;
		if (currentButtons == null) {
			return;
		}
		if (button instanceof Button.Fixing) {
			if (isFixedProperty != null) {
				isFixedProperty.setValue(!isFixedProperty.getValue());
				setButtons(createButtons(elements));
			}
		} else {
			int selectedButtonIndex = currentButtons.indexOf(button);
			if (Objects.equals(this.selectedButtonIndex, selectedButtonIndex)) {
				hideSubmenu();
			} else {
				PropertiesArray array = createPropertiesArray(button);
				if (array != null) {
					showSubmenu(array, selectedButtonIndex);
				}
			}
		}
	}

	/**
	 * Calculates the popup position on the canvas.
	 * @param popupSize size of the popup
	 * @return position on canvas
	 */
	public @CheckForNull GPoint getPositionOnCanvas(GDimension popupSize) {
		return positioner.getPositionOnCanvas(popupSize);
	}

	private void setButtons(List<Button> buttons) {
		if (Objects.equals(this.buttons, buttons)) {
			return;
		}
		this.buttons = buttons;
		if (delegate != null) {
			delegate.onButtonsChanged(buttons);
		}
	}

	private void setSubmenuItems(List<PropertyView> submenuItems) {
		if (this.submenuItems == submenuItems) {
			return;
		}
		this.submenuItems = submenuItems;
		if (delegate != null) {
			delegate.onSubmenuItemsChanged(submenuItems);
		}
	}

	private void setSelectedButtonIndex(@CheckForNull Integer selectedButtonIndex) {
		if (Objects.equals(this.selectedButtonIndex, selectedButtonIndex)) {
			return;
		}
		this.selectedButtonIndex = selectedButtonIndex;
		if (delegate != null) {
			delegate.onSelectedButtonChanged(selectedButtonIndex);
		}
	}

	private void showSubmenu(PropertiesArray array, @Nonnull Integer selectedButtonIndex) {
		submenuProperties = array;
		for (Property property : array.getProperties()) {
			if (property instanceof ValuedProperty<?> valuedProperty
					&& !(property instanceof RangeProperty<?>)) {
				valuedProperty.addValueObserver(hideSubmenuOnValueChange);
			}
		}
		List<PropertyView> propertyViews = propertyViewListOf(array);
		setSubmenuItems(propertyViews);
		setSelectedButtonIndex(selectedButtonIndex);
	}

	private void hideSubmenu() {
		setSubmenuItems(null);
		setSelectedButtonIndex(null);
		
		if (submenuProperties != null) {
			for (Property property : submenuProperties.getProperties()) {
				if (property instanceof ValuedProperty<?> valuedProperty
						&& !(property instanceof RangeProperty<?>)) {
					valuedProperty.removeValueObserver(hideSubmenuOnValueChange);
				}
			}
			submenuProperties = null;
		}
	}

	private PropertiesArray createPropertiesArray(Button button) {
		if (button instanceof Button.Color) {
			return propertiesFactory.createObjectColorProperties(localization, elements,
					DefaultColorValues.BRIGHT_STYLE_BAR);
		} else if (button instanceof Button.LineStyle) {
			return propertiesFactory.createLineStyleProperties(localization, elements);
		} else if (button instanceof Button.PointStyle) {
			return propertiesFactory.createPointStyleProperties(localization, elements);
		} else if (button instanceof Button.Opacity) {
			return propertiesFactory.createOpacityProperties(localization, elements);
		}
		return null;
	}

	private List<Button> createButtons(List<GeoElement> elements) {
		if (elements.isEmpty()) {
			return Collections.emptyList();
		}
		ArrayList<Button> buttons = new ArrayList<>();
		if (propertiesFactory.createObjectColorProperty(localization, elements,
				DefaultColorValues.BRIGHT_STYLE_BAR) != null) {
			buttons.add(new Button.Color());
		}
		if (propertiesFactory.createImageOpacityProperty(localization, elements) != null
				&& elements.stream().allMatch((geo) -> geo instanceof GeoImage)) {
			buttons.add(new Button.Opacity());
		}
		IconsEnumeratedProperty<?> pointStyleProperty =
				propertiesFactory.createPointStyleProperty(localization, elements);
		if (pointStyleProperty != null) {
			buttons.add(new Button.PointStyle(
					pointStyleProperty.getValueIcons()[pointStyleProperty.getIndex()]));
		}
		IconsEnumeratedProperty<?> lineStyleProperty =
				propertiesFactory.createLineStyleProperty(localization, elements);
		if (lineStyleProperty != null) {
			buttons.add(new Button.LineStyle(
					lineStyleProperty.getValueIcons()[lineStyleProperty.getIndex()]));
		}
		isFixedProperty = propertiesFactory.createIsFixedObjectProperty(localization, elements);
		if (isFixedProperty != null
				&& elements.stream().allMatch(QuickStyleBarModel::hasFunctionProperties)) {
			buttons.add(new Button.Fixing(isFixedProperty.getValue()));
		}
		return buttons;
	}

	private static boolean hasFunctionProperties(GeoElement element) {
		if (element instanceof GeoList list) {
			return list.getGeoElementForPropertiesDialog() instanceof GeoFunction;
		} else {
			return element.isFunctionOrEquationFromUser();
		}
	}
}
