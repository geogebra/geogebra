package org.geogebra.common.properties.impl.objects.collection;

import org.geogebra.common.properties.Property;

abstract class AbstractPropertyCollection<T extends Property, S> implements Property {

	T[] properties;

	AbstractPropertyCollection(T[] properties) {
		this.properties = properties;
	}

	abstract S defaultValue();

	abstract void setPropertyValue(T property, S value);

	abstract S getPropertyValue(T property);

	@Override
	public String getName() {
		return getFirstProperty().getName();
	}

	T getFirstProperty() {
		return properties[0];
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

	S reduceValue() {
		S value = getPropertyValue(getFirstProperty());
		for (T property : properties) {
			if (!value.equals(getPropertyValue(property))) {
				return defaultValue();
			}
		}
		return value;
	}
}
