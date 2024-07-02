package org.geogebra.common.properties.impl.collections;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.PropertyValueObserver;
import org.geogebra.common.properties.ValuedProperty;

abstract class AbstractValuedPropertyCollection<T extends ValuedProperty<S>, S> implements
		ValuedProperty<S> {

	private final T[] properties;
	private final Set<PropertyValueObserver> observers = new HashSet<>();
	private boolean frozen = false;
	private S previousValue = null;

	AbstractValuedPropertyCollection(T[] properties) {
		if (properties.length == 0) {
			throw new IllegalArgumentException("Properties must have at least a single property");
		}
		this.properties = properties;
	}

	@Override
	public String getName() {
		return getFirstProperty().getName();
	}

	@Override
	public String getRawName() {
		return getFirstProperty().getRawName();
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
	public void addValueObserver(PropertyValueObserver observer) {
		observers.add(observer);
	}

	@Override
	public void removeValueObserver(PropertyValueObserver observer) {
		observers.remove(observer);
	}

	private void notifyObservers(Consumer<PropertyValueObserver> observerConsumer) {
		observers.forEach(observerConsumer);
	}

	private void callProperty(Consumer<T> propertyConsumer) {
		Arrays.asList(properties).forEach(propertyConsumer);
	}

	@Override
	public S getValue() {
		return getFirstProperty().getValue();
	}

	@Override
	public void setValue(S value) {
		if (isFrozen()) {
			return;
		}
		callProperty(property -> property.setValue(value));
		notifyObservers(observer -> observer.onDidSetValue(this));
	}

	@Override
	public void beginSetValue() {
		if (isFrozen()) {
			return;
		}
		callProperty(ValuedProperty::beginSetValue);
		notifyObservers(observer -> observer.onBeginSetValue(this));
	}

	@Override
	public void endSetValue() {
		if (isFrozen()) {
			return;
		}
		callProperty(ValuedProperty::endSetValue);
		notifyObservers(observer -> observer.onEndSetValue(this));
	}

	@Override
	public boolean isFrozen() {
		return frozen;
	}

	@Override
	public void setFrozen(boolean frozen) {
		this.frozen = frozen;
	}

	@Override
	public void freezeValue(S fixedValue) {
		previousValue = getValue();
		setValue(fixedValue);
		setFrozen(true);
	}

	@Override
	public void unfreezeValue() {
		setFrozen(false);
		setValue(previousValue);
	}
}
