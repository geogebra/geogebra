package org.geogebra.common.properties.impl.objects.collection;

import org.geogebra.common.properties.IconsEnumerableProperty;
import org.geogebra.common.properties.PropertyResource;

/**
 * Handles a collection of IconsEnumerableProperty objects as a single IconsEnumerableProperty.
 */
public class IconsEnumerablePropertyCollection<T extends IconsEnumerableProperty>
		extends EnumerablePropertyCollection<T> implements IconsEnumerableProperty {

	/**
	 * @param properties properties to handle
	 */
	public IconsEnumerablePropertyCollection(T[] properties) {
		super(properties);
	}

	@Override
	public PropertyResource[] getIcons() {
		return getFirstProperty().getIcons();
	}
}
