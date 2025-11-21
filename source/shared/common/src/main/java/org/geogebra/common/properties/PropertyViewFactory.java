package org.geogebra.common.properties;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.undo.UndoManager;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.common.properties.factory.PropertiesArray;
import org.geogebra.common.properties.impl.undo.UndoSavingPropertyObserver;
import org.geogebra.common.properties.util.PropertyArrayValueObserving;

/**
 * Factory class for creating {@link PropertyView}s.
 */
public class PropertyViewFactory {
	/**
	 * Converts a {@link PropertiesArray} into a list of {@code PropertyView}s.
	 * @param propertiesArray the {@code PropertiesArray} to convert
	 * @return the list of {@code PropertyView}s
	 */
	public static @Nonnull List<PropertyView> propertyViewListOf(
			@Nonnull PropertiesArray propertiesArray) {
		List<PropertyView> propertyViewList = Arrays.stream(propertiesArray.getProperties())
				.map(PropertyView::of)
				.filter(Objects::nonNull)
				.collect(Collectors.toList());

		// Set ordinal position of expandable lists
		for (int i = 0; i < propertyViewList.size(); i++) {
			PropertyView propertyView = propertyViewList.get(i);
			if (!(propertyView instanceof PropertyView.ExpandableList)) {
				continue;
			}
			PropertyView.ExpandableList expandableList = (PropertyView.ExpandableList) propertyView;

			boolean previousIsExpandableList = i > 0
					&& propertyViewList.get(i - 1) instanceof PropertyView.ExpandableList;
			boolean nextIsExpandableList = i < propertyViewList.size() - 1
					&& propertyViewList.get(i + 1) instanceof PropertyView.ExpandableList;

			if (previousIsExpandableList && !nextIsExpandableList) {
				expandableList.ordinalPosition = PropertyView.ExpandableList.OrdinalPosition.Last;
			} else if (!previousIsExpandableList && nextIsExpandableList) {
				expandableList.ordinalPosition = PropertyView.ExpandableList.OrdinalPosition.First;
			} else if (previousIsExpandableList) {
				expandableList.ordinalPosition =
						PropertyView.ExpandableList.OrdinalPosition.InBetween;
			} else {
				expandableList.ordinalPosition = PropertyView.ExpandableList.OrdinalPosition.Alone;
			}
		}

		return propertyViewList;
	}

	/**
	 * Constructs the {@link Property}s for the settings of the given objects, connects the
	 * {@link UndoManager}, and transforms them into a {@code PropertyView} to be displayed.
	 * @param geoElements the objects for which to construct the settings
	 * @param algebraProcessor the algebra processor
	 * @param localization the localization
	 * @param geoElementPropertiesFactory the factory to be used to create the {@code Property}s
	 * @param undoManager the undo manager to connect to
	 * @return the {@code PropertyView} containing the settings for the given objects
	 */
	public static @Nonnull PropertyView.TabbedPageSelector propertyViewOfObjectSettings(
			@Nonnull List<GeoElement> geoElements,
			@Nonnull AlgebraProcessor algebraProcessor,
			@Nonnull Localization localization,
			@Nonnull GeoElementPropertiesFactory geoElementPropertiesFactory,
			@Nonnull UndoManager undoManager) {
		List<PropertiesArray> propertiesArrayList = geoElementPropertiesFactory
				.createStructuredProperties(algebraProcessor, localization, geoElements);
		UndoSavingPropertyObserver undoSavingPropertyObserver =
				new UndoSavingPropertyObserver(undoManager);
		propertiesArrayList.forEach(propertiesArray -> PropertyArrayValueObserving
				.addObserver(propertiesArray, undoSavingPropertyObserver));
		return new PropertyView.TabbedPageSelector(
				localization.getMenu(geoElements.get(0).getTypeString()), propertiesArrayList, 0);
	}

	/**
	 * Constructs the {@link Property}s for the settings of the app
	 * and transforms them into a {@code PropertyView} to be displayed.
	 * @param app the current app for which to create the settings
	 * @param propertiesRegistry the {@link PropertiesRegistry}
	 * to be used for registering the newly constructed properties
	 * @param objectPropertiesAreShown whether the properties of an object are shown,
	 * determining the initially selected tab index of the app settings
	 * (see: <a href="https://geogebra-jira.atlassian.net/browse/APPS-7052">APPS-7052</a>)
	 * @return the {@code PropertyView} containing the app settings
	 */
	public static @Nonnull PropertyView.TabbedPageSelector propertyViewOfAppSettings(
			@Nonnull App app,
			@Nonnull PropertiesRegistry propertiesRegistry,
			boolean objectPropertiesAreShown) {
		List<PropertiesArray> propertyArrayList = app.getConfig().createPropertiesFactory()
				.createProperties(app, app.getLocalization(), propertiesRegistry);
		int initialSelectedTabIndex = calculateInitialSelectedTabIndex(
				propertyArrayList, objectPropertiesAreShown);
		return new PropertyView.TabbedPageSelector(app.getLocalization().getMenu("Settings"),
				propertyArrayList, initialSelectedTabIndex);
	}

	private static int calculateInitialSelectedTabIndex(
			List<PropertiesArray> propertiesArrayList, boolean objectPropertiesWereShown) {
		if (!objectPropertiesWereShown) {
			return 0;
		}
		for (int index = 0; index < propertiesArrayList.size(); index++) {
			String name = propertiesArrayList.get(index).getRawName();
			if ("DrawingPad".equals(name) || "GraphicsView3D".equals(name)) {
				return index;
			}
		}
		return 0;
	}
}
