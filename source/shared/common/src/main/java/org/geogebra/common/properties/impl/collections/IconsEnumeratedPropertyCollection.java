package org.geogebra.common.properties.impl.collections;

import java.util.List;

import org.geogebra.common.properties.IconsEnumeratedProperty;
import org.geogebra.common.properties.PropertyResource;

/**
 * Handles a collection of IconsEnumeratedProperty objects as a single IconsEnumeratedProperty.
 */
public class IconsEnumeratedPropertyCollection<T extends IconsEnumeratedProperty<V>, V>
		extends EnumeratedPropertyCollection<T, V> implements IconsEnumeratedProperty<V> {

	/**
	 * @param properties properties to handle
	 */
	public IconsEnumeratedPropertyCollection(List<T> properties) {
		super(properties);
	}

	@Override
	public PropertyResource[] getValueIcons() {
		return getFirstProperty().getValueIcons();
	}
}
