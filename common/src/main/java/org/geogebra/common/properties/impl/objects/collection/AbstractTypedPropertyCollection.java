package org.geogebra.common.properties.impl.objects.collection;

import java.util.HashSet;
import java.util.Set;

import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.PropertyObserver;
import org.geogebra.common.properties.ValuedProperty;

abstract class AbstractTypedPropertyCollection<T extends ValuedProperty<S>, S> implements
		ValuedProperty<S> {

	private final T[] properties;
	private final Set<PropertyObserver> observers = new HashSet<>();

	AbstractTypedPropertyCollection(T[] properties) {
		if (properties.length == 0) {
			throw new IllegalArgumentException("Properties must have at least a single property");
		}
		this.properties = properties;
	}

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

	@Override
	public void addObserver(PropertyObserver observer) {
		observers.add(observer);
	}

	@Override
	public void removeObserver(PropertyObserver observer) {
		observers.remove(observer);
	}

	private void notifyObservers() {
		for (PropertyObserver observer : observers) {
			observer.onChange(this);
		}
	}

	@Override
	public S getValue() {
		return getFirstProperty().getValue();
	}

	@Override
	public void setValue(S value) {
		for (T property : properties) {
			property.setValue(value);
		}
		notifyObservers();
	}
}
