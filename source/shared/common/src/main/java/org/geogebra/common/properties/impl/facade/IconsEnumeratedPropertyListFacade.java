package org.geogebra.common.properties.impl.facade;

import java.util.List;

import javax.annotation.CheckForNull;

import org.geogebra.common.properties.IconsEnumeratedProperty;
import org.geogebra.common.properties.PropertyResource;

/**
 * Handles a collection of IconsEnumeratedProperty objects as a single IconsEnumeratedProperty.
 */
public class IconsEnumeratedPropertyListFacade<T extends IconsEnumeratedProperty<V>, V>
		extends EnumeratedPropertyListFacade<T, V> implements IconsEnumeratedProperty<V> {

	/**
	 * @param properties properties to handle
	 */
	public IconsEnumeratedPropertyListFacade(List<T> properties) {
		super(properties);
	}

	@Override
	public PropertyResource[] getValueIcons() {
		return getFirstProperty().getValueIcons();
	}

	@Override
	public @CheckForNull String[] getToolTipLabels() {
		return null;
	}
}
