package org.geogebra.common.properties.impl.objects.collection;

import org.geogebra.common.properties.aliases.BooleanProperty;

/**
 * Handles a collection of BooleanProperty objects as a single BooleanProperty.
 */
public class BooleanPropertyCollection<T extends BooleanProperty>
		extends AbstractTypedPropertyCollection<T, Boolean> implements BooleanProperty {

	/**
	 * @param properties properties to handle
	 */
	public BooleanPropertyCollection(T[] properties) {
		super(properties);
	}
}
