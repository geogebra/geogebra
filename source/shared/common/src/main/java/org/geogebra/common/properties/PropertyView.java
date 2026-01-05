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

package org.geogebra.common.properties;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.main.settings.SettingListener;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventListener;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.properties.aliases.ActionableIconPropertyCollection;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.aliases.ColorProperty;
import org.geogebra.common.properties.aliases.StringProperty;
import org.geogebra.common.properties.factory.PropertiesArray;
import org.geogebra.common.properties.impl.collections.ActionablePropertyCollection;
import org.geogebra.common.properties.impl.facade.AbstractPropertyListFacade;
import org.geogebra.common.properties.impl.general.RestoreSettingsAction;
import org.geogebra.common.properties.impl.general.SaveSettingsAction;
import org.geogebra.common.properties.impl.graphics.AxisCrossPropertyCollection;
import org.geogebra.common.properties.impl.graphics.AxisDistancePropertyCollection;
import org.geogebra.common.properties.impl.graphics.AxisUnitPropertyCollection;
import org.geogebra.common.properties.impl.graphics.ClippingPropertyCollection;
import org.geogebra.common.properties.impl.graphics.Dimension2DPropertiesCollection;
import org.geogebra.common.properties.impl.graphics.Dimension3DPropertiesCollection;
import org.geogebra.common.properties.impl.graphics.DimensionMinMaxProperty;
import org.geogebra.common.properties.impl.graphics.DimensionRatioProperty;
import org.geogebra.common.properties.impl.graphics.GridAngleProperty;
import org.geogebra.common.properties.impl.graphics.GridDistanceProperty;
import org.geogebra.common.properties.impl.graphics.GridDistancePropertyCollection;
import org.geogebra.common.properties.impl.graphics.GridFixedDistanceProperty;
import org.geogebra.common.properties.impl.graphics.LabelStylePropertyCollection;
import org.geogebra.common.properties.impl.graphics.NavigationBarPropertiesCollection;
import org.geogebra.common.properties.impl.graphics.SettingsDependentProperty;
import org.geogebra.common.properties.impl.objects.AbsoluteScreenPositionPropertyCollection;
import org.geogebra.common.properties.impl.objects.AlgebraViewVisibilityPropertyCollection;
import org.geogebra.common.properties.impl.objects.GeoElementDependentProperty;
import org.geogebra.common.properties.impl.objects.LocationPropertyCollection;
import org.geogebra.common.properties.util.StringPropertyWithSuggestions;

import com.google.j2objc.annotations.Weak;

/**
 * Represents a widget/view in the settings view.
 * <p>
 * Each {@code PropertyView} corresponds to a specific type of UI element. They provide getters to
 * read the UI's state, setters to handle user interactions, and delegates to notify the UI layer
 * when a view's configuration or visibility has been modified.
 * </p>
 */
public abstract class PropertyView {
	protected @Weak @CheckForNull ConfigurationUpdateDelegate configurationUpdateDelegate;
	protected @Weak @CheckForNull VisibilityUpdateDelegate visibilityUpdateDelegate;
	// Prevents overriding the visibility delegate when it relies solely on the parent's visibility.
	protected boolean disableVisibilityUpdateDelegateSetter = false;

	/**
	 * Delegate interface for receiving notifications about configuration updates.
	 * <p>
	 * The configuration defines how the view appears. Depending on the view, it may include text,
	 * color, selection, whether the view is enabled, and more.
	 * </p>
	 */
	public interface ConfigurationUpdateDelegate {
		/**
		 * Called when the configuration of a {@code PropertyView} has been updated.
		 */
		void configurationUpdated();
	}

	/**
	 * Delegate interface for receiving notifications about visibility updates.
	 */
	public interface VisibilityUpdateDelegate {
		/**
		 * Called when the visibility of a {@code PropertyView} has been updated.
		 */
		void visibilityUpdated();
	}

	/**
	 * Assigns the delegate to receive configuration update notifications.
	 * @param configurationUpdateDelegate the delegate or {@code null} to remove it
	 */
	public final void setConfigurationUpdateDelegate(
			@CheckForNull ConfigurationUpdateDelegate configurationUpdateDelegate) {
		this.configurationUpdateDelegate = configurationUpdateDelegate;
	}

	/**
	 * Assigns the delegate to receive visibility update notifications.
	 * @param visibilityUpdateDelegate the delegate or {@code null} to remove it
	 */
	public final void setVisibilityUpdateDelegate(
			@CheckForNull VisibilityUpdateDelegate visibilityUpdateDelegate) {
		if (disableVisibilityUpdateDelegateSetter) {
			return;
		}
		this.visibilityUpdateDelegate = visibilityUpdateDelegate;
	}

	/**
	 * Checks whether the view is visible.
	 * @return {@code true} if the view is visible, {@code false} otherwise
	 */
	public boolean isVisible() {
		return true;
	}

	/**
	 * Detaches this view from its delegates and listeners, removing all references to allow
	 * proper garbage collection. Should be called when the view is no longer needed.
	 */
	public void detach() {
		visibilityUpdateDelegate = null;
		configurationUpdateDelegate = null;
	}

	public abstract static class PropertyBackedView<T extends Property> extends PropertyView
			implements PropertyValueObserver<Object>, SettingListener, EventListener {
		protected final @Nonnull T property;
		private boolean previousAvailability;
		private @CheckForNull List<GeoElement> dependentGeoElements;

		protected PropertyBackedView(@Nonnull T property) {
			this.property = property;
			this.previousAvailability = property.isAvailable();
			if (property instanceof SettingsDependentProperty) {
				((SettingsDependentProperty) property).getSettings().addListener(this);
			}
			if (property instanceof AbstractPropertyListFacade<?>) {
				List<?> properties = ((AbstractPropertyListFacade<?>) property)
						.getPropertyList();
				dependentGeoElements = properties
						.stream()
						.filter(p -> p instanceof GeoElementDependentProperty)
						.map(p -> (GeoElementDependentProperty) p)
						.map(GeoElementDependentProperty::getGeoElement)
						.collect(Collectors.toList());
				dependentGeoElements.forEach(element -> element.getApp()
						.getEventDispatcher().addEventListener(this));
			}
			if (property instanceof ValuedProperty) {
				((ValuedProperty<?>) property).addValueObserver(this);
			}
			if (property instanceof LabelStylePropertyCollection) {
				Arrays.stream(((LabelStylePropertyCollection) property).getProperties())
						.forEach(p -> ((BooleanProperty) p).addValueObserver(this));
			}
		}

		protected void onDependentGeoElementUpdated() {
			// Do nothing by default
		}

		@Override
		public void detach() {
			super.detach();
			if (property instanceof SettingsDependentProperty) {
				((SettingsDependentProperty) property).getSettings().removeListener(this);
			}
			if (dependentGeoElements != null) {
				dependentGeoElements.forEach(element -> element.getApp()
						.getEventDispatcher().removeEventListener(this));
				dependentGeoElements = null;
			}
			if (property instanceof ValuedProperty) {
				((ValuedProperty<?>) property).removeValueObserver(this);
			}
			if (property instanceof LabelStylePropertyCollection) {
				Arrays.stream(((LabelStylePropertyCollection) property).getProperties())
						.forEach(p -> ((BooleanProperty) p).removeValueObserver(this));
			}
		}

		/**
		 * @return the backing property's name (e.g., for diagnostics).
		 */
		public final String getPropertyName() {
			return property.getName();
		}

		/**
		 * Checks whether the view is enabled.
		 * @return {@code true} if the view is enabled, {@code false} otherwise
		 */
		public final boolean isEnabled() {
			return property.isEnabled();
		}

		@Override
		public final boolean isVisible() {
			return property.isAvailable();
		}

		@Override
		public void settingsChanged(AbstractSettings settings) {
			notifyUpdateDelegates();
		}

		@Override
		public void onDidSetValue(ValuedProperty<Object> property) {
			notifyUpdateDelegates();
		}

		@Override
		public void sendEvent(Event evt) {
			if (dependentGeoElements != null && dependentGeoElements.contains(evt.target)) {
				if (evt.type == EventType.UPDATE || evt.type == EventType.UPDATE_STYLE) {
					onDependentGeoElementUpdated();
					notifyUpdateDelegates();
				}
			}
		}

		private void notifyUpdateDelegates() {
			boolean visibilityChanged = property.isAvailable() != previousAvailability;
			previousAvailability = property.isAvailable();
			if (visibilityUpdateDelegate != null && visibilityChanged) {
				visibilityUpdateDelegate.visibilityUpdated();
			}

			if (configurationUpdateDelegate != null) {
				configurationUpdateDelegate.configurationUpdated();
			}
		}
	}

	/**
	 * Representation of a checkbox with an optional label.
	 */
	public static final class Checkbox extends PropertyBackedView<BooleanProperty> {
		Checkbox(BooleanProperty booleanProperty) {
			super(booleanProperty);
		}

		/**
		 * @return the label for the checkbox
		 */
		public @Nonnull String getLabel() {
			return property.getName();
		}

		/**
		 * @return {@code true} if the checkbox is selected, {@code false} otherwise.
		 */
		public boolean isSelected() {
			return property.getValue();
		}

		/**
		 * Sets whether the checkbox is selected or not.
		 * @param selected {@code true} to select, {@code false} to deselect the checkbox
		 */
		public void setSelected(boolean selected) {
			property.setValue(selected);
		}
	}

	/**
	 * Representation of a dropdown menu with a label and a list of possible items.
	 */
	public static final class Dropdown extends PropertyBackedView<NamedEnumeratedProperty<?>> {
		Dropdown(NamedEnumeratedProperty<?> namedEnumeratedProperty) {
			super(namedEnumeratedProperty);
		}

		/**
		 * @return the label of the dropdown
		 */
		public @Nonnull String getLabel() {
			return property.getName();
		}

		/**
		 * @return the list of possible items
		 */
		public @Nonnull List<String> getItems() {
			return List.of(property.getValueNames());
		}

		/**
		 * @return the index of the currently selected item, or {@code null} if none is selected
		 */
		public @CheckForNull Integer getSelectedItemIndex() {
			int index = property.getIndex();
			return index != -1 ? index : null;
		}

		/**
		 * Sets the index of the selected item.
		 * @param newIndex the new index of the selected item
		 */
		public void setSelectedItemIndex(int newIndex) {
			property.setIndex(newIndex);
		}
	}

	/**
	 * Representation of a combo box with a label, a dropdown for suggestions, an input field for
	 * custom values, displaying the current value and an optional error message.
	 */
	public static final class ComboBox
			extends ValidatablePropertyBackedView<StringPropertyWithSuggestions> {
		ComboBox(StringPropertyWithSuggestions stringPropertyWithSuggestions) {
			super(stringPropertyWithSuggestions);
		}

		/**
		 * @return the label
		 */
		public @Nonnull String getLabel() {
			return property.getName();
		}

		/**
		 * @return the suggested items for the dropdown menu
		 */
		public @Nonnull List<String> getItems() {
			return property.getSuggestions();
		}
	}

	/**
	 * Representation of a horizontal view with two inner views,
	 * each occupying half the total width.
	 * @implNote The class implements {@link VisibilityUpdateDelegate} because it manages the
	 * visibility of its child views, ensuring that either this class and all of its children are
	 * visible or none are. Therefore, setting the visibility update delegate with
	 * {@link PropertyView#setVisibilityUpdateDelegate} on its children will have no effect.
	 */
	public static final class HorizontalSplitView extends PropertyView
			implements VisibilityUpdateDelegate {
		private final PropertyView leadingPropertyView;
		private final PropertyView trailingPropertyView;

		HorizontalSplitView(PropertyView leadingPropertyView, PropertyView trailingPropertyView) {
			this.leadingPropertyView = leadingPropertyView;
			this.trailingPropertyView = trailingPropertyView;

			leadingPropertyView.setVisibilityUpdateDelegate(this);
			trailingPropertyView.setVisibilityUpdateDelegate(this);
			// Prevent overriding visibility delegates, as they depend solely on this view.
			leadingPropertyView.disableVisibilityUpdateDelegateSetter = true;
			trailingPropertyView.disableVisibilityUpdateDelegateSetter = true;
		}

		/**
		 * @return the first property view
		 */
		public @Nonnull PropertyView getLeadingPropertyView() {
			return leadingPropertyView;
		}

		/**
		 * @return the second property view
		 */
		public @Nonnull PropertyView getTrailingPropertyView() {
			return trailingPropertyView;
		}

		@Override
		public boolean isVisible() {
			return leadingPropertyView.isVisible() && trailingPropertyView.isVisible();
		}

		@Override
		public void visibilityUpdated() {
			if (visibilityUpdateDelegate != null) {
				visibilityUpdateDelegate.visibilityUpdated();
			}
		}

		@Override
		public void detach() {
			super.detach();
			leadingPropertyView.detach();
			trailingPropertyView.detach();
		}
	}

	/**
	 * {@code PropertyView} responsible for setting, retrieving, and validating the value of a
	 * {@link StringProperty} depending on whether the user is currently editing.
	 */
	private abstract static class ValidatablePropertyBackedView<T extends StringProperty>
			extends PropertyBackedView<T> {
		private String value;
		private String errorMessage;
		private boolean isEditing;

		protected ValidatablePropertyBackedView(@Nonnull T stringProperty) {
			super(stringProperty);
			value = stringProperty.getValue() != null ? stringProperty.getValue() : "";
			errorMessage = null;
			isEditing = false;
		}

		@Override
		protected void onDependentGeoElementUpdated() {
			value = property.getValue() != null ? property.getValue() : "";
			errorMessage = null;
		}

		/**
		 * @return the current value
		 */
		public @Nonnull String getValue() {
			return value;
		}

		/**
		 * Sets the value.
		 * @param newValue the new value
		 */
		public void setValue(@Nonnull String newValue) {
			boolean valueShouldUpdate = !Objects.equals(value, newValue);
			boolean errorMessageShouldUpdate = !Objects.equals(errorMessage,
					property.validateValue(newValue));

			value = newValue;
			errorMessage = property.validateValue(newValue);

			if (errorMessage == null && !isEditing) {
				property.setValue(value);
			} else if (valueShouldUpdate || errorMessageShouldUpdate) {
				if (configurationUpdateDelegate != null) {
					configurationUpdateDelegate.configurationUpdated();
				}
			}
		}

		/**
		 * @return the error message for invalid inputs or {@code null} if there is no error
		 */
		public @CheckForNull String getErrorMessage() {
			return errorMessage;
		}

		/**
		 * Marks the beginning of an editing session.
		 */
		public void startEditing() {
			isEditing = true;
		}

		/**
		 * Marks the end of an editing session.
		 */
		public void stopEditing() {
			isEditing = false;
			setValue(value);
		}
	}

	/**
	 * Representation of an input text field with a label and an optional error message.
	 */
	public static final class TextField extends ValidatablePropertyBackedView<StringProperty> {
		TextField(StringProperty stringProperty) {
			super(stringProperty);
		}

		/**
		 * @return the label
		 */
		public @Nonnull String getLabel() {
			return property.getName();
		}
	}

	/**
	 * Representation of a row of icons to select from, with one selected icon and a label above.
	 */
	public static final class SingleSelectionIconRow
			extends PropertyBackedView<IconsEnumeratedProperty<?>> {
		public SingleSelectionIconRow(IconsEnumeratedProperty<?> iconsEnumeratedProperty) {
			super(iconsEnumeratedProperty);
		}

		/**
		 * @return the label above the icons
		 */
		public @Nonnull String getLabel() {
			return property.getName();
		}

		/**
		 * @return the list of icons to select from
		 */
		public @Nonnull List<PropertyResource> getIcons() {
			return List.of(property.getValueIcons());
		}

		/**
		 * @return the labels of buttons, which is used as tooltip or/and aria-label
		 */
		public @CheckForNull String[] getToolTipLabels() {
			return property.getToolTipLabels();
		}

		/**
		 * @return the index of the currently selected icon, or {@code null} if none is selected
		 */
		public @CheckForNull Integer getSelectedIconIndex() {
			int index = property.getIndex();
			return index != -1 ? index : null;
		}

		/**
		 * Sets the index of the selected icon.
		 * @param newIndex the new index
		 */
		public void setSelectedIconIndex(int newIndex) {
			property.setIndex(newIndex);
		}
	}

	/**
	 * Representation of a row of colors to select from,
	 * with zero or one selected color and a label above.
	 */
	public static final class ColorSelectorRow extends PropertyBackedView<ColorProperty> {
		ColorSelectorRow(ColorProperty colorProperty) {
			super(colorProperty);
		}

		/**
		 * @return the label above the colors
		 */
		public @Nonnull String getLabel() {
			return property.getName();
		}

		/**
		 * @return the list of colors available for selection
		 */
		public @Nonnull List<GColor> getColors() {
			return property.getValues();
		}

		/**
		 * Sets a custom color resulting from custom color chooser.
		 * @param color the new custom color
		 */
		public void setCustomColor(GColor color) {
			property.setValue(color);
		}

		/**
		 * @return the index of the currently selected color, or {@code null} if none is selected
		 */
		public @CheckForNull Integer getSelectedColorIndex() {
			int index = property.getIndex();
			return index != -1 ? index : null;
		}

		/**
		 * Sets the index of the newly selected color.
		 * @param newSelectedColorIndex the new index of the selected color
		 */
		public void setSelectedColorIndex(int newSelectedColorIndex) {
			property.setIndex(newSelectedColorIndex);
		}
	}

	/**
	 * Representation of an expandable list with a title, an optional checkbox,
	 * and a dropdown button that expands the item to show other {@code PropertyView}s.
	 */
	public static final class ExpandableList extends PropertyView {
		private final PropertyCollection<?> propertyCollection;
		private final List<PropertyView> propertyViews;
		private final @CheckForNull Checkbox checkbox;
		OrdinalPosition ordinalPosition = OrdinalPosition.Alone;

		/**
		 * Positional value used to determine the relative position of {@code ExpandableList}s
		 * in a sequence.
		 */
		public enum OrdinalPosition {
			First, InBetween, Last, Alone,
		}

		ExpandableList(PropertyCollection<?> propertyCollection, List<PropertyView> propertyViews) {
			this.propertyCollection = propertyCollection;
			this.propertyViews = propertyViews;
			this.checkbox = propertyCollection instanceof PropertyCollectionWithLead
					? new Checkbox(((PropertyCollectionWithLead) propertyCollection).leadProperty)
					: null;
		}

		/**
		 * @return the representation of the checkbox if it should be shown, {@code null} otherwise
		 */
		public @CheckForNull Checkbox getCheckbox() {
			return checkbox;
		}

		/**
		 * @return the title of the expandable list
		 */
		public @Nonnull String getTitle() {
			return propertyCollection.getName();
		}

		/**
		 * @return the relative position compared to other expandable lists in the same sequence
		 */
		public @Nonnull OrdinalPosition getOrdinalPosition() {
			return ordinalPosition;
		}

		/**
		 * @return the list of {@code PropertyView}s displayed when the expandable list is open
		 */
		public @Nonnull List<PropertyView> getItems() {
			return propertyViews;
		}

		@Override
		public void detach() {
			super.detach();
			propertyViews.forEach(PropertyView::detach);
			if (checkbox != null) {
				checkbox.detach();
			}
		}
	}

	/**
	 * Representation of a list of {@code PropertyView}s that are related to each other,
	 * displayed closer together with smaller spacing with an optional title.
	 */
	public static final class RelatedPropertyViewCollection extends PropertyView {
		private final @CheckForNull String title;
		private final List<PropertyView> propertyViews;
		private final int contentSpacing;

		RelatedPropertyViewCollection(@CheckForNull String title,
				@Nonnull List<PropertyView> propertyViews, int contentSpacing) {
			this.title = title;
			this.propertyViews = propertyViews;
			this.contentSpacing = contentSpacing;
		}

		/**
		 * @return the title before the list of {@code PropertyView}s
		 * or {@code null} if there is no title
		 */
		public @CheckForNull String getTitle() {
			return title;
		}

		/**
		 * @return the necessary spacing between the {@code PropertyView}s
		 */
		public int getContentSpacing() {
			return contentSpacing;
		}

		/**
		 * @return the list of related {@code PropertyView}s
		 */
		public @Nonnull List<PropertyView> getPropertyViews() {
			return propertyViews;
		}

		@Override
		public void detach() {
			super.detach();
			propertyViews.forEach(PropertyView::detach);
		}
	}

	/**
	 * Representation of a row of icons that can be toggled independently, with a label above them.
	 */
	public static final class MultiSelectionIconRow
			extends PropertyBackedView<LabelStylePropertyCollection> {
		MultiSelectionIconRow(LabelStylePropertyCollection labelStylePropertyCollection) {
			super(labelStylePropertyCollection);
		}

		/**
		 * @return the label above the icons
		 */
		public @Nonnull String getLabel() {
			return property.getName();
		}

		/**
		 * @return the list of icons to display
		 */
		public @Nonnull List<PropertyResource> getIcons() {
			return Arrays.stream(property.getProperties())
					.map(IconAssociatedProperty::getIcon)
					.collect(Collectors.toList());
		}

		/**
		 * @param index of button
		 * @return the tooltip label of button at position index
		 */
		public @Nonnull String getTooltipLabel(int index) {
			return property.getProperties()[index].getName();
		}

		/**
		 * @return the list of toggle states for each icon, with {@code true}
		 * if selected and {@code false} otherwise for each icon
		 */
		public List<Boolean> areIconsSelected() {
			return Arrays.stream(property.getProperties())
					.map(property -> ((BooleanProperty) property).getValue())
					.collect(Collectors.toList());
		}

		/**
		 * Sets whether the icon at the specified index is selected
		 * @param index the index of the icon to update
		 * @param selected {@code true} to select the icon, {@code false} to deselect
		 */
		public void setIconSelected(int index, boolean selected) {
			((BooleanProperty) property.getProperties()[index]).setValue(selected);
		}
	}

	/**
	 * A row of icon buttons, each with an icon, a title, and an action triggered when tapped.
	 */
	public static final class IconButtonRow
			extends PropertyBackedView<ActionableIconPropertyCollection> {
		IconButtonRow(ActionableIconPropertyCollection actionableIconPropertyCollection) {
			super(actionableIconPropertyCollection);
		}

		/**
		 * @return the number of icon buttons
		 */
		public int count() {
			return property.getProperties().length;
		}

		/**
		 * @param index the index of the button to query
		 * @return the icon resource for the given index
		 */
		public @Nonnull PropertyResource getIcon(int index) {
			return property.getProperties()[index].getIcon();
		}

		/**
		 * @param index the index of the button to query
		 * @return the title of the button for the given index
		 */
		public @Nonnull String getTitle(int index) {
			return property.getProperties()[index].getName();
		}

		/**
		 * Triggers the action associated with the button at the given index.
		 * @param index the index of the button to trigger
		 */
		public void selectButton(int index) {
			property.getProperties()[index].performAction();
		}
	}

	/**
	 * Representation of a property-specific view, displaying two text fields separated by a colon
	 * in a row with a trailing lock icon that can be either open or closed, and a label above.
	 */
	public static final class DimensionRatioEditor extends PropertyBackedView<BooleanProperty> {
		private final TextField leadingTextField;
		private final TextField trailingTextField;
		private final DimensionRatioProperty dimensionRatioProperty;

		DimensionRatioEditor(DimensionRatioProperty dimensionRatioProperty) {
			super((BooleanProperty) dimensionRatioProperty.getProperties()[2]);
			this.dimensionRatioProperty = dimensionRatioProperty;
			this.leadingTextField = new PropertyView.TextField(
					(StringProperty) dimensionRatioProperty.getProperties()[0]);
			this.trailingTextField = new PropertyView.TextField(
					(StringProperty) dimensionRatioProperty.getProperties()[1]);
		}

		/**
		 * @return {@code true} if the lock icon is closed, {@code false} otherwise
		 */
		public boolean isLocked() {
			return property.getValue();
		}

		/**
		 * Set the lock to either closed or open.
		 * @param locked {@code true} to set the lock's state to closed, {@code false} to open
		 */
		public void setLocked(boolean locked) {
			property.setValue(locked);
		}

		/**
		 * @return the first text field
		 */
		public TextField getLeadingTextField() {
			return leadingTextField;
		}

		/**
		 * @return the second text field
		 */
		public TextField getTrailingTextField() {
			return trailingTextField;
		}

		/**
		 * @return the label above the text fields and the icon
		 */
		public @Nonnull String getLabel() {
			return dimensionRatioProperty.getName();
		}

		@Override
		public void detach() {
			super.detach();
			leadingTextField.detach();
			trailingTextField.detach();
		}
	}

	/**
	 * Representation of a page selector with a title, a list of tabs,
	 * and a list of {@code PropertyView} groups corresponding to each tab.
	 * Each tab displays its own collection of {@code PropertyView}s.
	 */
	public static final class TabbedPageSelector extends PropertyView {
		private final String title;
		private final List<String> tabTitles;
		private final List<List<PropertyView>> pageContents;
		private int selectedTabIndex;

		TabbedPageSelector(@Nonnull String title,
				@Nonnull List<PropertiesArray> pagePropertyArrays, int initialSelectedTabIndex) {
			this.title = title;
			this.tabTitles = pagePropertyArrays.stream()
					.map(PropertiesArray::getName)
					.collect(Collectors.toList());
			this.pageContents = pagePropertyArrays.stream()
					.map(PropertyViewFactory::propertyViewListOf)
					.collect(Collectors.toList());
			this.selectedTabIndex = initialSelectedTabIndex;
		}

		/**
		 * @return the view's title
		 */
		public @Nonnull String getTitle() {
			return title;
		}

		/**
		 * @return the tab titles
		 */
		public @Nonnull List<String> getTabTitles() {
			return tabTitles;
		}

		/**
		 * @param pageIndex the index of the tab for which to retrieve the page contents
		 * @return list of {@code PropertyView}s for the specified tab
		 */
		public @Nonnull List<PropertyView> getPageContents(int pageIndex) {
			return pageContents.get(pageIndex);
		}

		/**
		 * @return the index of the currently selected tab
		 */
		public int getSelectedTabIndex() {
			return selectedTabIndex;
		}

		/**
		 * Sets the index of the newly selected tab.
		 * @param newSelectedTabIndex the new index of the selected tab
		 */
		public void setSelectedTabIndex(int newSelectedTabIndex) {
			selectedTabIndex = newSelectedTabIndex;
			if (configurationUpdateDelegate != null) {
				configurationUpdateDelegate.configurationUpdated();
			}
		}

		@Override
		public void detach() {
			super.detach();
			pageContents.forEach(propertyViews -> propertyViews.forEach(PropertyView::detach));
		}
	}

	/**
	 * A row of action buttons, each with a text and an action triggered when tapped.
	 */
	public static final class ActionableButtonRow extends
			PropertyBackedView<ActionablePropertyCollection> {
		private final ActionablePropertyCollection actionablePropertyCollection;

		ActionableButtonRow(ActionablePropertyCollection actionablePropertyCollection) {
			super(actionablePropertyCollection);
			this.actionablePropertyCollection = actionablePropertyCollection;
		}

		/**
		 * @return the number of actionable buttons
		 */
		public int count() {
			return property.getProperties().length;
		}

		/**
		 * Perform action of button with given index.
		 * @param index the index of the button to query
		 */
		public void performAction(int index) {
			actionablePropertyCollection.getProperties()[index].performAction();
		}

		/**
		 * @param index the index of the button to query
		 * @return the label for the given index
		 */
		public @Nonnull String getLabel(int index) {
			return actionablePropertyCollection.getProperties()[index].getName();
		}

		/**
		 * @param index the index of the button to query
		 * @return the style name for the given index
		 */
		public @Nonnull String getStyleName(int index) {
			ActionableProperty actionableProperty =
					actionablePropertyCollection.getProperties()[index];
			if (actionableProperty instanceof SaveSettingsAction) {
				return "dialogContainedButton";
			} else if (actionableProperty instanceof RestoreSettingsAction) {
				return "materialOutlinedButton";
			}
			return "";
		}
	}

	/**
	 * Factory method that returns the appropriate {@code PropertyView}
	 * for the given {@link Property}.
	 * @param property the property for which to create the view
	 * @return the {@code PropertyView} matching the given {@code Property},
	 * or {@code null} if the given {@code Property} is not supported
	 */
	public static @CheckForNull PropertyView of(Property property) {
		if (property instanceof BooleanProperty) {
			return new Checkbox((BooleanProperty) property);
		} else if (property instanceof NamedEnumeratedProperty) {
			return new Dropdown((NamedEnumeratedProperty<?>) property);
		} else if (property instanceof StringPropertyWithSuggestions) {
			return new ComboBox((StringPropertyWithSuggestions) property);
		} else if (property instanceof StringProperty) {
			return new TextField((StringProperty) property);
		} else if (property instanceof IconsEnumeratedProperty) {
			return new SingleSelectionIconRow((IconsEnumeratedProperty<?>) property);
		} else if (property instanceof AxisDistancePropertyCollection
				|| property instanceof AxisCrossPropertyCollection
				|| property instanceof AxisUnitPropertyCollection) {
			return new RelatedPropertyViewCollection(null,
					propertyViewListOf((PropertyCollection<?>) property), 0);
		} else if (property instanceof NavigationBarPropertiesCollection) {
			return new RelatedPropertyViewCollection(null,
					propertyViewListOf((PropertyCollection<?>) property), 16);
		} else if (property instanceof ClippingPropertyCollection
				|| property instanceof LocationPropertyCollection
				|| property instanceof AlgebraViewVisibilityPropertyCollection) {
			return new RelatedPropertyViewCollection(property.getName(),
					propertyViewListOf((PropertyCollection<?>) property), 4);
		} else if (property instanceof LabelStylePropertyCollection) {
			return new MultiSelectionIconRow((LabelStylePropertyCollection) property);
		} else if (property instanceof ActionableIconPropertyCollection) {
			return new IconButtonRow((ActionableIconPropertyCollection) property);
		} else if (property instanceof ColorProperty) {
			return new ColorSelectorRow((ColorProperty) property);
		} else if (property instanceof GridDistancePropertyCollection) {
			GridDistancePropertyCollection gridDistancePropertyCollection =
					(GridDistancePropertyCollection) property;
			GridFixedDistanceProperty gridFixedDistanceProperty =
					(GridFixedDistanceProperty) gridDistancePropertyCollection.getProperties()[0];
			GridDistanceProperty gridDistancePropertyX =
					(GridDistanceProperty) gridDistancePropertyCollection.getProperties()[1];
			GridDistanceProperty gridDistancePropertyY =
					(GridDistanceProperty) gridDistancePropertyCollection.getProperties()[2];
			GridDistanceProperty gridDistancePropertyR =
					(GridDistanceProperty) gridDistancePropertyCollection.getProperties()[3];
			GridAngleProperty gridAngleProperty =
					(GridAngleProperty) gridDistancePropertyCollection.getProperties()[4];
			return new RelatedPropertyViewCollection(null, List.of(
					new Checkbox(gridFixedDistanceProperty),
					new HorizontalSplitView(
							new ComboBox(gridDistancePropertyX),
							new ComboBox(gridDistancePropertyY)),
					new HorizontalSplitView(
							new ComboBox(gridDistancePropertyR),
							new ComboBox(gridAngleProperty))), 0);
		} else if (property instanceof Dimension2DPropertiesCollection) {
			Dimension2DPropertiesCollection dimension2DPropertiesCollection =
					(Dimension2DPropertiesCollection) property;
			DimensionRatioProperty dimensionRatioProperty =
					(DimensionRatioProperty) dimension2DPropertiesCollection.getProperties()[0];
			DimensionMinMaxProperty dimensionPropertyMinX =
					(DimensionMinMaxProperty) dimension2DPropertiesCollection.getProperties()[1];
			DimensionMinMaxProperty dimensionPropertyMaxX =
					(DimensionMinMaxProperty) dimension2DPropertiesCollection.getProperties()[2];
			DimensionMinMaxProperty dimensionPropertyMinY =
					(DimensionMinMaxProperty) dimension2DPropertiesCollection.getProperties()[3];
			DimensionMinMaxProperty dimensionPropertyMaxY =
					(DimensionMinMaxProperty) dimension2DPropertiesCollection.getProperties()[4];
			return new ExpandableList(dimension2DPropertiesCollection, List.of(
					new DimensionRatioEditor(dimensionRatioProperty),
					new RelatedPropertyViewCollection(property.getName(), List.of(
							new HorizontalSplitView(
									new TextField(dimensionPropertyMinX),
									new TextField(dimensionPropertyMaxX)),
							new HorizontalSplitView(
									new TextField(dimensionPropertyMinY),
									new TextField(dimensionPropertyMaxY))), 10)));
		} else if (property instanceof Dimension3DPropertiesCollection) {
			Dimension3DPropertiesCollection propertyCollection =
					(Dimension3DPropertiesCollection) property;
			DimensionMinMaxProperty dimensionPropertyMinX = propertyCollection.getProperties()[0];
			DimensionMinMaxProperty dimensionPropertyMaxX = propertyCollection.getProperties()[1];
			DimensionMinMaxProperty dimensionPropertyMinY = propertyCollection.getProperties()[2];
			DimensionMinMaxProperty dimensionPropertyMaxY = propertyCollection.getProperties()[3];
			DimensionMinMaxProperty dimensionPropertyMinZ = propertyCollection.getProperties()[4];
			DimensionMinMaxProperty dimensionPropertyMaxZ = propertyCollection.getProperties()[5];
			return new RelatedPropertyViewCollection(property.getName(), List.of(
					new HorizontalSplitView(
							new TextField(dimensionPropertyMinX),
							new TextField(dimensionPropertyMaxX)),
					new HorizontalSplitView(
							new TextField(dimensionPropertyMinY),
							new TextField(dimensionPropertyMaxY)),
					new HorizontalSplitView(
							new TextField(dimensionPropertyMinZ),
							new TextField(dimensionPropertyMaxZ))), 10);
		} else if (property instanceof AbsoluteScreenPositionPropertyCollection) {
			AbsoluteScreenPositionPropertyCollection absoluteScreenPositionPropertyCollection =
					(AbsoluteScreenPositionPropertyCollection) property;
			return new HorizontalSplitView(
					new TextField(absoluteScreenPositionPropertyCollection.getProperties()[0]),
					new TextField(absoluteScreenPositionPropertyCollection.getProperties()[1]));
		} else if (property instanceof ActionablePropertyCollection<?>) {
			ActionablePropertyCollection actionablePropertyCollection =
					(ActionablePropertyCollection) property;
			return new ActionableButtonRow(actionablePropertyCollection);
		} else if (property instanceof PropertyCollection) {
			PropertyCollection<?> propertyCollection = (PropertyCollection<?>) property;
			return new ExpandableList(propertyCollection, propertyViewListOf(propertyCollection));
		} else {
			return null;
		}
	}

	private static List<PropertyView> propertyViewListOf(PropertyCollection<?> propertyCollection) {
		return Arrays.stream(propertyCollection.getProperties())
				.map(PropertyView::of)
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
	}
}
