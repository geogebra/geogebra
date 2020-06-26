package org.geogebra.common.properties.impl.objects.collection;

import org.geogebra.common.properties.Property;

abstract class AbstractTypedPropertyCollection<T extends Property, S> implements Property {

	private final T[] properties;

	AbstractTypedPropertyCollection(T[] properties) {
		if (properties.length == 0) {
			throw new IllegalArgumentException("Properties must have at least a single property");
		}
		this.properties = properties;
	}

	abstract void setPropertyValue(T property, S value);

	@Override
	public String getName() {
		return getFirstProperty().getName();
	}

	protected T getFirstProperty() {
		return properties[0];
	}

	protected T[] getProperties() {
		return properties;
	}

	@Override
	public boolean isEnabled() {
		boolean isEnabled = true;
		for (Property property : properties) {
			isEnabled = isEnabled && property.isEnabled();
		}
		return isEnabled;
	}

	protected void setProperties(S value) {
		for (T element : properties) {
			setPropertyValue(element, value);
		}
	}
}
