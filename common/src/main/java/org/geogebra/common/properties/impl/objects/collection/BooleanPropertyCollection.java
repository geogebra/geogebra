package org.geogebra.common.properties.impl.objects.collection;

import org.geogebra.common.properties.BooleanProperty;

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

	@Override
	public boolean getValue() {
		return getFirstProperty().getValue();
	}

	@Override
	void setPropertyValue(T property, Boolean value) {
		property.setValue(value);
	}

	@Override
	public void setValue(boolean value) {
		setProperties(value);
	}
}
