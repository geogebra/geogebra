package org.geogebra.common.properties.impl.objects.collection;

import java.util.HashSet;
import java.util.Set;

import org.geogebra.common.properties.ObservableProperty;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.PropertyObserver;

abstract class AbstractTypedPropertyCollection<T extends Property, S> implements
		ObservableProperty {

	private Set<PropertyObserver> observers = new HashSet<>();

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

	protected void setProperties(S value) {
		doSetProperties(value);
		notifyObservers();
	}

	protected void doSetProperties(S value) {
		for (T element : properties) {
			setPropertyValue(element, value);
		}
	}

}
