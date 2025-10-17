package org.geogebra.common.properties;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.geogebra.common.properties.factory.PropertiesArray;

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
}
