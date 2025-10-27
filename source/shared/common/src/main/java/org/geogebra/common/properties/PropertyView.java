package org.geogebra.common.properties;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.properties.aliases.ActionableIconPropertyCollection;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.aliases.ColorProperty;
import org.geogebra.common.properties.aliases.StringProperty;
import org.geogebra.common.properties.impl.graphics.AxisCrossPropertyCollection;
import org.geogebra.common.properties.impl.graphics.AxisDistancePropertyCollection;
import org.geogebra.common.properties.impl.graphics.AxisUnitPropertyCollection;
import org.geogebra.common.properties.impl.graphics.ClippingPropertyCollection;
import org.geogebra.common.properties.impl.graphics.GridAngleProperty;
import org.geogebra.common.properties.impl.graphics.GridDistanceProperty;
import org.geogebra.common.properties.impl.graphics.GridDistancePropertyCollection;
import org.geogebra.common.properties.impl.graphics.GridFixedDistanceProperty;
import org.geogebra.common.properties.impl.graphics.LabelStylePropertyCollection;
import org.geogebra.common.properties.impl.graphics.SettingsDependentProperty;
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
	public void setVisibilityUpdateDelegate(
			@CheckForNull VisibilityUpdateDelegate visibilityUpdateDelegate) {
		this.visibilityUpdateDelegate = visibilityUpdateDelegate;
	}

	/**
	 * Checks whether the view is visible.
	 * @return {@code true} if the view is visible, {@code false} otherwise
	 */
	public boolean isVisible() {
		return true;
	}

	private abstract static class PropertyBackedView<T extends Property> extends PropertyView {
		protected final @Nonnull T property;
		private boolean previousAvailability;

		protected PropertyBackedView(@Nonnull T property) {
			this.property = property;
			this.previousAvailability = property.isAvailable();
			if (property instanceof SettingsDependentProperty) {
				((SettingsDependentProperty) property).getSettings()
						.addListener(settings -> onSettingsUpdated());
			}
			if (property instanceof ValuedProperty) {
				((ValuedProperty<?>) property)
						.addValueObserver(valuedProperty -> onValueUpdated());
			}
			if (property instanceof LabelStylePropertyCollection) {
				Arrays.stream(((LabelStylePropertyCollection) property).getProperties())
						.forEach(p -> ((BooleanProperty) p)
								.addValueObserver(valuedProperty -> onValueUpdated()));
			}
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

		private void onSettingsUpdated() {
			boolean visibilityChanged = property.isAvailable() != previousAvailability;
			previousAvailability = property.isAvailable();
			if (visibilityUpdateDelegate != null && visibilityChanged) {
				visibilityUpdateDelegate.visibilityUpdated();
			}
			if (configurationUpdateDelegate != null) {
				configurationUpdateDelegate.configurationUpdated();
			}
		}

		private void onValueUpdated() {
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
	 * @apiNote The class implements {@link VisibilityUpdateDelegate} to correctly set up visibility
	 * notifications internally, ensuring that {@link PropertyView#setVisibilityUpdateDelegate}
	 * works with the optional visibility delegate in the parent.
	 * This setup properly notifies the parent whenever its child's visibility is updated.
	 */
	public static final class ComboBox extends PropertyBackedView<StringPropertyWithSuggestions>
			implements VisibilityUpdateDelegate {
		private String value;
		private String errorMessage;
		private @Weak @CheckForNull VisibilityUpdateDelegate comboBoxVisibilityUpdateDelegate;
		private @Weak @CheckForNull VisibilityUpdateDelegate parentVisibilityUpdateDelegate;

		ComboBox(StringPropertyWithSuggestions stringPropertyWithSuggestions,
				@CheckForNull VisibilityUpdateDelegate parentVisibilityUpdateDelegate) {
			super(stringPropertyWithSuggestions);
			this.value = stringPropertyWithSuggestions.getValue() != null
					? stringPropertyWithSuggestions.getValue() : "";
			this.errorMessage = null;
			this.parentVisibilityUpdateDelegate = parentVisibilityUpdateDelegate;
			if (parentVisibilityUpdateDelegate != null) {
				super.setVisibilityUpdateDelegate(this);
			}
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

			if (errorMessage == null) {
				property.setValue(value);
			} else if (valueShouldUpdate || errorMessageShouldUpdate) {
				if (configurationUpdateDelegate != null) {
					configurationUpdateDelegate.configurationUpdated();
				}
			}
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

		/**
		 * @return the error message for invalid inputs or {@code null} if there is no error
		 */
		public @CheckForNull String getErrorMessage() {
			return errorMessage;
		}

		@Override
		public void setVisibilityUpdateDelegate(
				@CheckForNull VisibilityUpdateDelegate visibilityUpdateDelegate) {
			// Set the delegate to notify visibility change specifically for this combo box
			this.comboBoxVisibilityUpdateDelegate = visibilityUpdateDelegate;
			// Set the delegate to notify visibility change for this combo box and its parent
			super.setVisibilityUpdateDelegate(this);
		}

		@Override
		public void visibilityUpdated() {
			// Notify objects listening to this view specifically
			if (comboBoxVisibilityUpdateDelegate != null) {
				comboBoxVisibilityUpdateDelegate.visibilityUpdated();
			}
			// Notify objects listening to the parent
			if (parentVisibilityUpdateDelegate != null) {
				parentVisibilityUpdateDelegate.visibilityUpdated();
			}
		}
	}

	/**
	 * Representation of two combo boxes placed side by side, each taking up half the space.
	 * @implNote The class implements {@link VisibilityUpdateDelegate} to correctly set up visibility
	 * notifications internally, ensuring that {@link PropertyView#setVisibilityUpdateDelegate}
	 * works independently from the same delegate set to its children.
	 */
	public static final class ComboBoxRow extends PropertyView implements VisibilityUpdateDelegate {
		private final ComboBox leadingComboBox;
		private final ComboBox trailingComboBox;

		ComboBoxRow(StringPropertyWithSuggestions leadingComboBoxProperty,
				StringPropertyWithSuggestions trailingComboBoxProperty) {
			this.leadingComboBox = new ComboBox(leadingComboBoxProperty, this);
			this.trailingComboBox = new ComboBox(trailingComboBoxProperty, this);
		}

		/**
		 * @return the first combo box
		 */
		public @Nonnull ComboBox getLeadingComboBox() {
			return leadingComboBox;
		}

		/**
		 * @return the second combo box
		 */
		public @Nonnull ComboBox getTrailingComboBox() {
			return trailingComboBox;
		}

		@Override
		public boolean isVisible() {
			return leadingComboBox.isVisible() && trailingComboBox.isVisible();
		}

		@Override
		public void visibilityUpdated() {
			if (visibilityUpdateDelegate != null) {
				visibilityUpdateDelegate.visibilityUpdated();
			}
		}
	}

	/**
	 * Representation of an input text field with a label and an optional error message.
	 */
	public static final class TextField extends PropertyBackedView<StringProperty> {
		private String text;
		private String errorMessage;

		TextField(StringProperty stringProperty) {
			super(stringProperty);
			text = stringProperty.getValue();
			errorMessage = null;
		}

		/**
		 * @return the label
		 */
		public @Nonnull String getLabel() {
			return property.getName();
		}

		/**
		 * @return the current input, or {@code null} if there is no input
		 */
		public @CheckForNull String getText() {
			return text;
		}

		/**
		 * Sets the input field's text.
		 * @param newText the new text
		 */
		public void setText(@Nonnull String newText) {
			boolean textShouldUpdate = !Objects.equals(text, newText);
			boolean errorMessageShouldUpdate = !Objects.equals(errorMessage,
					property.validateValue(newText));

			text = newText;
			errorMessage = property.validateValue(newText);

			if (errorMessage == null) {
				property.setValue(text);
			} else if (textShouldUpdate || errorMessageShouldUpdate) {
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
	}

	/**
	 * Representation of a row of icons to select from, with one selected icon and a label above.
	 */
	public static final class SingleSelectionIconRow
			extends PropertyBackedView<IconsEnumeratedProperty<?>> {
		SingleSelectionIconRow(IconsEnumeratedProperty<?> iconsEnumeratedProperty) {
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

		ExpandableList(PropertyCollection<?> propertyCollection) {
			this.propertyCollection = propertyCollection;
			this.propertyViews = Arrays.stream(propertyCollection.getProperties())
					.map(PropertyView::of)
					.filter(Objects::nonNull)
					.collect(Collectors.toList());
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
	 * Factory method that returns the appropriate {@code PropertyView}
	 * for the given {@link Property}.
	 * @param property the property for which to create the view
	 * @return the {@code PropertyView} matching the given {@code Property},
	 * or {@code null} if the given {@code Property} is not supported
	 */
	static @CheckForNull PropertyView of(Property property) {
		if (property instanceof BooleanProperty) {
			return new Checkbox((BooleanProperty) property);
		} else if (property instanceof NamedEnumeratedProperty) {
			return new Dropdown((NamedEnumeratedProperty<?>) property);
		} else if (property instanceof StringPropertyWithSuggestions) {
			return new ComboBox((StringPropertyWithSuggestions) property, null);
		} else if (property instanceof StringProperty) {
			return new TextField((StringProperty) property);
		} else if (property instanceof IconsEnumeratedProperty) {
			return new SingleSelectionIconRow((IconsEnumeratedProperty<?>) property);
		} else if (property instanceof AxisDistancePropertyCollection
				|| property instanceof AxisCrossPropertyCollection
				|| property instanceof AxisUnitPropertyCollection) {
			PropertyCollection<?> propertyCollection = (PropertyCollection<?>) property;
			List<PropertyView> propertyViews = Arrays.stream(propertyCollection.getProperties())
					.map(PropertyView::of)
					.filter(Objects::nonNull)
					.collect(Collectors.toList());
			return new RelatedPropertyViewCollection(null, propertyViews, 0);
		} else if (property instanceof ClippingPropertyCollection) {
			ClippingPropertyCollection clippingPropertyCollection =
					(ClippingPropertyCollection) property;
			List<PropertyView> propertyViews = Arrays
					.stream(clippingPropertyCollection.getProperties())
					.map(PropertyView::of)
					.filter(Objects::nonNull)
					.collect(Collectors.toList());
			return new RelatedPropertyViewCollection(
					clippingPropertyCollection.getName(), propertyViews, 4);
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
					new ComboBoxRow(gridDistancePropertyX, gridDistancePropertyY),
					new ComboBoxRow(gridDistancePropertyR, gridAngleProperty)), 0);
		} else if (property instanceof PropertyCollection) {
			return new ExpandableList((PropertyCollection<?>) property);
		} else {
			return null;
		}
	}
}
