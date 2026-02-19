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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.MyImage;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.main.settings.SettingListener;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventListener;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.plugin.ScriptType;
import org.geogebra.common.properties.aliases.ActionableIconProperty;
import org.geogebra.common.properties.aliases.ActionableIconPropertyCollection;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.aliases.ColorProperty;
import org.geogebra.common.properties.aliases.ImageProperty;
import org.geogebra.common.properties.aliases.StringProperty;
import org.geogebra.common.properties.factory.PropertiesArray;
import org.geogebra.common.properties.impl.collections.AbstractPropertyCollection;
import org.geogebra.common.properties.impl.collections.ActionablePropertyCollection;
import org.geogebra.common.properties.impl.facade.AbstractPropertyListFacade;
import org.geogebra.common.properties.impl.facade.ImagePropertyListFacade;
import org.geogebra.common.properties.impl.facade.NamedEnumeratedPropertyListFacade;
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
import org.geogebra.common.properties.impl.graphics.NavigationBarPropertiesCollection;
import org.geogebra.common.properties.impl.graphics.SettingsDependentProperty;
import org.geogebra.common.properties.impl.objects.AbsoluteScreenPositionPropertyCollection;
import org.geogebra.common.properties.impl.objects.AlgebraViewVisibilityPropertyCollection;
import org.geogebra.common.properties.impl.objects.AlignmentPropertyCollection;
import org.geogebra.common.properties.impl.objects.BackgroundColorPropertyCollection;
import org.geogebra.common.properties.impl.objects.ButtonIconPropertyCollection;
import org.geogebra.common.properties.impl.objects.ChartSegmentFillCategoryProperty;
import org.geogebra.common.properties.impl.objects.ChartSegmentSelection;
import org.geogebra.common.properties.impl.objects.ChartSegmentSelectionDependentProperty;
import org.geogebra.common.properties.impl.objects.DynamicColorSpaceProperty;
import org.geogebra.common.properties.impl.objects.FillCategoryProperty;
import org.geogebra.common.properties.impl.objects.GeoElementDependentProperty;
import org.geogebra.common.properties.impl.objects.LayoutPropertyCollection;
import org.geogebra.common.properties.impl.objects.LocationPropertyCollection;
import org.geogebra.common.properties.impl.objects.ObjectAllEventsProperty;
import org.geogebra.common.properties.impl.objects.ObjectEventProperty;
import org.geogebra.common.properties.impl.objects.SliderTrackColorPropertyCollection;
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
			implements PropertyValueObserver<Object>, SettingListener, EventListener,
			ChartSegmentSelection.Listener {
		protected final @Nonnull T property;
		private boolean previousAvailability;
		private @CheckForNull List<GeoElement> dependentGeoElements;
		private @CheckForNull List<ChartSegmentSelectionDependentProperty>
				chartSelectionDependentProperties;

		protected PropertyBackedView(@Nonnull T property) {
			this.property = property;
			this.previousAvailability = property.isAvailable();
			if (property instanceof SettingsDependentProperty) {
				((SettingsDependentProperty) property).getSettings().addListener(this);
			}
			if (property instanceof AbstractPropertyListFacade<?>) {
				List<?> properties = ((AbstractPropertyListFacade<?>) property)
						.getPropertyList();
				chartSelectionDependentProperties = properties.stream()
						.filter(p -> p instanceof ChartSegmentSelectionDependentProperty)
						.map(p -> (ChartSegmentSelectionDependentProperty) p)
						.collect(Collectors.toList());
				chartSelectionDependentProperties.forEach(dependentProperty ->
						dependentProperty.getChartSegmentSelection().registerListener(this));
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
			if (chartSelectionDependentProperties != null) {
				chartSelectionDependentProperties.forEach(dependentProperty ->
						dependentProperty.getChartSegmentSelection().unregisterListener(this));
			}
			if (dependentGeoElements != null) {
				dependentGeoElements.forEach(element -> element.getApp()
						.getEventDispatcher().removeEventListener(this));
				dependentGeoElements = null;
			}
			if (property instanceof ValuedProperty) {
				((ValuedProperty<?>) property).removeValueObserver(this);
			}
		}

		@Override
		public void chartSegmentSelectionUpdated() {
			notifyUpdateDelegates();
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
			return property.isEnabled() && !property.isFrozen();
		}

		@Override
		public final boolean isVisible() {
			return property.isAvailable() && !property.isFrozen();
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

		@Override
		public String toString() {
			return property.getName() + " (" + getClass().getSimpleName() + ")";
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

		/**
		 * @return an array of indices where a divider must be inserted
		 * or {@code null} if no dividers should be inserted.
		 */
		public @CheckForNull int[] getGroupDividerIndices() {
			return property.getGroupDividerIndices();
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
	 * Representation of a slider with a label and it's value.
	 */
	public static final class Slider extends PropertyBackedView<RangeProperty<Integer>> {
		Slider(RangeProperty<Integer> rangeProperty) {
			super(rangeProperty);
		}

		/**
		 * @return the label
		 */
		public @Nonnull String getLabel() {
			return property.getName();
		}

		/**
		 * @return the value that can be displayed
		 */
		public @Nonnull String getDisplayValue() {
			String value = String.valueOf(getValue());
			if (property.isValueDisplayedAsPercentage()) {
				return value + "%";
			}
			return value;
		}

		/**
		 * @return the value of the slider
		 */
		public int getValue() {
			Integer value = property.getValue();
			return value != null ? value : 0;
		}

		/**
		 * Sets the value of the slider.
		 * @param newValue the new value of the slider
		 */
		public void setValue(int newValue) {
			property.setValue(newValue);
		}

		/**
		 * @return the minimum value
		 */
		public int getMin() {
			Integer min = property.getMin();
			return min != null ? min : 0;
		}

		/**
		 * @return the maximum value
		 */
		public int getMax() {
			Integer max = property.getMax();
			return max != null ? max : 100;
		}

		/**
		 * @return the step or increment between values
		 */
		public int getStep() {
			Integer step = property.getStep();
			return step != null ? step : 1;
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
		public boolean isVisible() {
			return propertyViews.stream()
					.anyMatch(PropertyView::isVisible);
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
		public boolean isVisible() {
			return propertyViews.stream()
					.anyMatch(PropertyView::isVisible);
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
	public static final class MultiSelectionIconRow extends PropertyView {
		private final PropertyCollection<ToggleableIconProperty> toggleableIconPropertyCollection;
		private final List<ToggleableIcon> toggleableIcons;

		/**
		 * Representation of a single toggleable icon used in {@code MultiSelectionIconRow}.
		 */
		public static final class ToggleableIcon
				extends PropertyBackedView<ToggleableIconProperty> {
			ToggleableIcon(@Nonnull ToggleableIconProperty property) {
				super(property);
			}

			/**
			 * @return the icon to display
			 */
			public @Nonnull PropertyResource getIcon() {
				return property.getIcon();
			}

			/**
			 * @return the tooltip label of the icon
			 */
			public @Nonnull String getTooltipLabel() {
				return property.getName();
			}

			/**
			 * @return {@code true} if the icon is selected and {@code false} otherwise
			 */
			public boolean isSelected() {
				return property.getValue();
			}

			/**
			 * Sets whether the icon is selected
			 * @param selected {@code true} to select the icon, {@code false} to deselect
			 */
			public void setSelected(boolean selected) {
				property.setValue(selected);
			}
		}

		MultiSelectionIconRow(
				PropertyCollection<ToggleableIconProperty> toggleableIconPropertyCollection) {
			this.toggleableIconPropertyCollection = toggleableIconPropertyCollection;
			this.toggleableIcons = Arrays.stream(toggleableIconPropertyCollection.getProperties())
					.map(ToggleableIcon::new).collect(Collectors.toList());
		}

		/**
		 * @return the label above the icons
		 */
		public @Nonnull String getLabel() {
			return toggleableIconPropertyCollection.getName();
		}

		/**
		 * @return the toggleable icons to display
		 */
		public @Nonnull List<ToggleableIcon> getToggleableIcons() {
			return toggleableIcons;
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
	 * Script tab with optional {@link ScriptType} drop-down and a script area.
	 */
	public static final class ScriptTab extends PropertyBackedView<ObjectEventProperty> {
		ScriptTab(ObjectEventProperty objectEventProperty) {
			super(objectEventProperty);
		}

		/**
		 * Enable/disable JS.
		 * @param jsEnabled whether JS is enabled in the app
		 */
		public void setJsEnabled(boolean jsEnabled) {
			property.setJsEnabled(jsEnabled);
		}

		/**
		 * @return true if JS is enabled in the app, false otherwise
		 */
		public boolean isJsEnabled() {
			return property.isJsEnabled();
		}

		/**
		 * Sets the event script text associated with this {@link ObjectEventProperty}.
		 * @param text script source to store
		 */
		public void setScriptText(String text) {
			property.setScriptText(text);
		}

		/**
		 * Returns the script text associated with this {@link ObjectEventProperty}.
		 * @return the event script text
		 */
		public String getScriptText() {
			return property.getScriptText();
		}

		/**
		 * Sets the type of the current {@link ObjectEventProperty}.
		 * @param scriptType {@link ScriptType}
		 */
		public void setScriptType(ScriptType scriptType) {
			property.setScriptType(scriptType);
		}

		/**
		 * Returns the type of the current {@link ObjectEventProperty}.
		 * @return the {@link ScriptType} describing how the script should be interpreted
		 */
		public ScriptType getScriptType() {
			return property.getScriptType();
		}
	}

	/**
	 * List of {@link ScriptTab} to edit script.
	 */
	public static final class ScriptEditor extends PropertyBackedView<ObjectAllEventsProperty> {
		private final List<ScriptTab> scriptTabList;

		ScriptEditor(ObjectAllEventsProperty objectAllEventsProperty) {
			super(objectAllEventsProperty);
			scriptTabList = new ArrayList<>();
			for (ObjectEventProperty objectEventProperty : objectAllEventsProperty.getProps()) {
				if (objectEventProperty.isEnabled()) {
					scriptTabList.add(new ScriptTab(objectEventProperty));
				}
			}
		}

		/**
		 * @return the number of {@link ScriptTab}
		 */
		public int count() {
			return scriptTabList.size();
		}

		/**
		 * @param index of {@link ScriptTab}
		 * @return {@link ScriptTab} of given index
		 */
		public @CheckForNull ScriptTab getScriptTab(int index) {
			if (index > -1 && index < count()) {
				return scriptTabList.get(index);
			}
			return null;
		}
	}

	/**
	 * Editor for all button icon related property: row of icon with default icons,
	 * file chooser for custom icon.
	 */
	public static final class ButtonIconEditor extends PropertyBackedView<BooleanProperty> {
		private final BooleanProperty leadProperty;
		private final IconsEnumeratedProperty<String> iconsEnumeratedProperty;
		private final SingleSelectionIconRow leadingIconButtonRow;
		private final ImagePicker trailingImagePicker;

		/**
		 * Editor for button icon property.
		 * @param buttonIconProperty {@link ButtonIconPropertyCollection}
		 */
		ButtonIconEditor(ButtonIconPropertyCollection buttonIconProperty) {
			super(buttonIconProperty.leadProperty);
			leadProperty = buttonIconProperty.leadProperty;
			iconsEnumeratedProperty = (IconsEnumeratedProperty<String>) buttonIconProperty
					.getProperties()[0];
			leadingIconButtonRow = new PropertyView.SingleSelectionIconRow(
					iconsEnumeratedProperty);
			trailingImagePicker = new PropertyView.ImagePicker((ImageProperty) buttonIconProperty
					.getProperties()[1]);
		}

		/**
		 * @return lead {@link BooleanProperty}
		 */
		public BooleanProperty getLeadProperty() {
			return leadProperty;
		}

		/**
		 * @return {@link SingleSelectionIconRow} of default icons
		 */
		public SingleSelectionIconRow getLeadingIconButtonRow() {
			return leadingIconButtonRow;
		}

		public ImagePicker getTrailingImagePicker() {
			return trailingImagePicker;
		}

		/**
		 * @param fileName file name of icon with extension
		 */
		public void setDefaultIcon(String fileName) {
			iconsEnumeratedProperty.setValue(fileName);
		}

		/**
		 * @return index of selected default icon.
		 */
		public Integer getSelectedIndex() {
			return iconsEnumeratedProperty.getIndex();
		}

		/**
		 * Sets the new selected index.
		 * @param index selected index
		 */
		public void setSelectedIndex(int index) {
			iconsEnumeratedProperty.setIndex(index);
		}

		/**
		 * Returns one of the default icons at given index.
		 * @param index given index
		 * @return icon at index
		 */
		public PropertyResource getIconAt(int index) {
			return iconsEnumeratedProperty.getValueIcons()[index];
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
	 * Representation of a button with an icon, a label, and an action triggered when tapped.
	 */
	public static final class ButtonWithIcon
			extends PropertyBackedView<ActionableIconProperty> {
		ButtonWithIcon(ActionableIconProperty property) {
			super(property);
		}

		/**
		 * Triggers the action associated with this button.
		 */
		public void performAction() {
			property.performAction();
		}

		/**
		 * @return the label of the button
		 */
		public String getLabel() {
			return property.getName();
		}

		/**
		 * @return the icon of the button
		 */
		public PropertyResource getIcon() {
			return property.getIcon();
		}
	}

	/**
	 * Representation of a row of buttons, each with a label, one of which is can be selected.
	 */
	public static final class ConnectedButtonGroup
			extends PropertyBackedView<NamedEnumeratedProperty<?>> {
		ConnectedButtonGroup(@Nonnull NamedEnumeratedProperty<?> property) {
			super(property);
		}

		/**
		 * @return the label of each button
		 */
		public List<String> getButtonLabels() {
			return List.of(property.getValueNames());
		}

		/**
		 * @return the index of the currently selected button, or {@code null} if none is selected
		 */
		public @CheckForNull Integer getSelectedButtonIndex() {
			int index = property.getIndex();
			return index != -1 ? index : null;
		}

		/**
		 * Sets the index of the newly selected button
		 * @param newSelectedButtonIndex the new index of the selected button
		 */
		public void setSelectedButtonIndex(int newSelectedButtonIndex) {
			property.setIndex(newSelectedButtonIndex);
		}
	}

	/**
	 * A row of action buttons, each with a text and an action triggered when tapped.
	 */
	public static final class ActionableButtonRow extends
			PropertyBackedView<ActionablePropertyCollection<?>> {
		private final ActionablePropertyCollection<?> actionablePropertyCollection;

		ActionableButtonRow(ActionablePropertyCollection<?> actionablePropertyCollection) {
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

	public static final class GroupedIconButtonRow extends PropertyView {
		private final AbstractPropertyCollection propertyCollection;
		private final List<SingleSelectionIconRow> iconRowList = new ArrayList<>();

		protected GroupedIconButtonRow(AbstractPropertyCollection propertyCollection) {
			this.propertyCollection = propertyCollection;
			for (Property property : propertyCollection.getProperties()) {
				if (property instanceof IconsEnumeratedProperty<?> iconsEnumeratedProperty) {
					iconRowList.add((SingleSelectionIconRow) of(iconsEnumeratedProperty));
				}
			}
		}

		public String getLabel() {
			return propertyCollection.getName();
		}

		public List<SingleSelectionIconRow> getIconRowList() {
			return iconRowList;
		}
	}

	/**
	 * Representation of an image picker that displays either a "choose from file" button
	 * or a preview of the selected image with its name and actions to change or remove it.
	 */
	public static final class ImagePicker extends PropertyBackedView<ImageProperty> {

		ImagePicker(@Nonnull ImageProperty property) {
			super(property);
		}

		/**
		 * @return the label for the file chooser button.
		 */
		public @Nonnull String getChooseFromFileLabel() {
			return property.getChooseFromFileLabel();
		}

		/**
		 * Sets the file path of the selected image.
		 * @param filePath the path of the selected file
		 */
		public void setImage(@Nonnull MyImage image, @Nonnull String filePath) {
			property.setValue(new ImageProperty.Value(image, filePath));
		}

		/**
		 * Gets the selected image.
		 * @return image or {@code null}
		 */
		public @CheckForNull MyImage getImage() {
			ImageProperty.Value value = property.getValue();
			if (value == null) {
				return null;
			}
			return value.image();
		}

		/** Clears the currently selected image. */
		public void clearImage() {
			property.setValue(null);
		}

		/**
		 * @return the file name extracted from the path, or {@code null} if no image is set
		 */
		public @CheckForNull String getFileName() {
			ImageProperty.Value value = property.getValue();
			if (value == null || value.path().isEmpty()) {
				return null;
			}
			String filePath = value.path();
			int index = value.path().lastIndexOf('/');
			if (index == -1) {
				return filePath;
			}
			return filePath.substring(index + 1);
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
		if (property instanceof BooleanProperty booleanProperty) {
			return new Checkbox(booleanProperty);
		} else if (property instanceof AlignmentPropertyCollection
			|| property instanceof LayoutPropertyCollection) {
			return new GroupedIconButtonRow((AbstractPropertyCollection) property);
		} else if (property instanceof DynamicColorSpaceProperty
				|| (property instanceof NamedEnumeratedPropertyListFacade<?, ?> facade
				&& (facade.getFirstProperty() instanceof DynamicColorSpaceProperty
				|| facade.getFirstProperty() instanceof FillCategoryProperty
				|| facade.getFirstProperty() instanceof ChartSegmentFillCategoryProperty))) {
			return new ConnectedButtonGroup((NamedEnumeratedProperty<?>) property);
		} else if (property instanceof NamedEnumeratedProperty<?> namedEnumeratedProperty) {
			return new Dropdown(namedEnumeratedProperty);
		} else if (property instanceof StringPropertyWithSuggestions stringProperty) {
			return new ComboBox(stringProperty);
		} else if (property instanceof ImagePropertyListFacade imagePropertyListFacade) {
			return new ImagePicker(imagePropertyListFacade);
		} else if (property instanceof StringProperty stringProperty) {
			return new TextField(stringProperty);
		} else if (property instanceof IconsEnumeratedProperty<?> iconsEnumeratedProperty) {
			return new SingleSelectionIconRow(iconsEnumeratedProperty);
		} else if (property instanceof RangeProperty<?>) {
			return new Slider((RangeProperty<Integer>) property);
		} else if (property instanceof AxisDistancePropertyCollection
				|| property instanceof AxisCrossPropertyCollection
				|| property instanceof AxisUnitPropertyCollection
				|| property instanceof SliderTrackColorPropertyCollection) {
			return new RelatedPropertyViewCollection(null,
					propertyViewListOf((PropertyCollection<?>) property), 0);
		} else if (property instanceof NavigationBarPropertiesCollection collection) {
			return new RelatedPropertyViewCollection(null, propertyViewListOf(collection), 16);
		} else if (property instanceof ClippingPropertyCollection
				|| property instanceof LocationPropertyCollection
				|| property instanceof AlgebraViewVisibilityPropertyCollection) {
			return new RelatedPropertyViewCollection(property.getName(),
					propertyViewListOf((PropertyCollection<?>) property), 4);
		} else if (property instanceof PropertyCollection<?> propertyCollection
				&& propertyCollection.getProperties()[0] instanceof ToggleableIconProperty) {
			return new MultiSelectionIconRow((PropertyCollection<ToggleableIconProperty>) property);
		} else if (property instanceof BackgroundColorPropertyCollection collection) {
			return new ExpandableList(collection, List.of(new RelatedPropertyViewCollection(
					null, propertyViewListOf(collection), 8)));
		} else if (property instanceof ButtonIconPropertyCollection buttonIconPropertyCollection) {
			return new ExpandableList(buttonIconPropertyCollection,
					List.of(new ButtonIconEditor(buttonIconPropertyCollection)));
		} else if (property instanceof ActionableIconPropertyCollection actionableIconProperty) {
			return new IconButtonRow(actionableIconProperty);
		} else if (property instanceof ColorProperty colorProperty) {
			return new ColorSelectorRow(colorProperty);
		} else if (property instanceof GridDistancePropertyCollection propertyCollection) {
			GridFixedDistanceProperty gridFixedDistanceProperty =
					(GridFixedDistanceProperty) propertyCollection.getProperties()[0];
			GridDistanceProperty gridDistancePropertyX =
					(GridDistanceProperty) propertyCollection.getProperties()[1];
			GridDistanceProperty gridDistancePropertyY =
					(GridDistanceProperty) propertyCollection.getProperties()[2];
			GridDistanceProperty gridDistancePropertyR =
					(GridDistanceProperty) propertyCollection.getProperties()[3];
			GridAngleProperty gridAngleProperty =
					(GridAngleProperty) propertyCollection.getProperties()[4];
			return new RelatedPropertyViewCollection(null, List.of(
					new Checkbox(gridFixedDistanceProperty),
					new HorizontalSplitView(
							new ComboBox(gridDistancePropertyX),
							new ComboBox(gridDistancePropertyY)),
					new HorizontalSplitView(
							new ComboBox(gridDistancePropertyR),
							new ComboBox(gridAngleProperty))), 0);
		} else if (property instanceof Dimension2DPropertiesCollection propertyCollection) {
			DimensionRatioProperty dimensionRatioProperty =
					(DimensionRatioProperty) propertyCollection.getProperties()[0];
			DimensionMinMaxProperty dimensionPropertyMinX =
					(DimensionMinMaxProperty) propertyCollection.getProperties()[1];
			DimensionMinMaxProperty dimensionPropertyMaxX =
					(DimensionMinMaxProperty) propertyCollection.getProperties()[2];
			DimensionMinMaxProperty dimensionPropertyMinY =
					(DimensionMinMaxProperty) propertyCollection.getProperties()[3];
			DimensionMinMaxProperty dimensionPropertyMaxY =
					(DimensionMinMaxProperty) propertyCollection.getProperties()[4];
			return new ExpandableList(propertyCollection, List.of(
					new DimensionRatioEditor(dimensionRatioProperty),
					new RelatedPropertyViewCollection(property.getName(), List.of(
							new HorizontalSplitView(
									new TextField(dimensionPropertyMinX),
									new TextField(dimensionPropertyMaxX)),
							new HorizontalSplitView(
									new TextField(dimensionPropertyMinY),
									new TextField(dimensionPropertyMaxY))), 10)));
		} else if (property instanceof Dimension3DPropertiesCollection propertyCollection) {
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
		} else if (property instanceof AbsoluteScreenPositionPropertyCollection collection) {
			return new HorizontalSplitView(
					new TextField(collection.getProperties()[0]),
					new TextField(collection.getProperties()[1]));
		} else if (property instanceof ActionablePropertyCollection<?> propertyCollection) {
			return new ActionableButtonRow(propertyCollection);
		} else if (property instanceof ObjectAllEventsProperty objectAllEventsProperty) {
			return new ScriptEditor(objectAllEventsProperty);
		} else if (property instanceof ActionableIconProperty actionableIconProperty) {
			return new ButtonWithIcon(actionableIconProperty);
		} else if (property instanceof PropertyCollection<?> propertyCollection) {
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
