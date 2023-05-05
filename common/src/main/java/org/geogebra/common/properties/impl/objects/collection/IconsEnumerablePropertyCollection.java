package org.geogebra.common.properties.impl.objects.collection;

import org.geogebra.common.properties.IconsEnumeratedProperty;
import org.geogebra.common.properties.PropertyResource;

/**
 * Handles a collection of IconsEnumeratedProperty objects as a single IconsEnumeratedProperty.
 */
public class IconsEnumerablePropertyCollection<T extends IconsEnumeratedProperty>
		extends EnumerablePropertyCollection<T> implements IconsEnumeratedProperty {

	/**
	 * @param properties properties to handle
	 */
	public IconsEnumerablePropertyCollection(T[] properties) {
		super(properties);
	}

	@Override
	public PropertyResource[] getValueIcons() {
		return getFirstProperty().getValueIcons();
	}
}
