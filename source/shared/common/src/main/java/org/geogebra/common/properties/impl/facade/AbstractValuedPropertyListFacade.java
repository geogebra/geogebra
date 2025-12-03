package org.geogebra.common.properties.impl.facade;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.geogebra.common.properties.PropertyValueObserver;
import org.geogebra.common.properties.ValuedProperty;

abstract class AbstractValuedPropertyListFacade<T extends ValuedProperty<S>, S>
		extends AbstractPropertyListFacade<T>
		implements ValuedProperty<S> {

	private final Set<PropertyValueObserver> observers = new HashSet<>();

	AbstractValuedPropertyListFacade(List<T> properties) {
		super(properties);
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
		properties.forEach(propertyConsumer);
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
		notifyObservers(observer -> observer.onWillSetValue(this));
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
}
