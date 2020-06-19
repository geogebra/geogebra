package org.geogebra.common.properties.impl.objects.collection;

import org.geogebra.common.properties.Property;

abstract class AbstractTypedPropertyCollection<T extends Property, S> implements Property {

	T[] properties;

	AbstractTypedPropertyCollection(T[] properties) {
		this.properties = properties;
	}

	abstract void setPropertyValue(T property, S value);

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
}
