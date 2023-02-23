package org.geogebra.common.properties.impl;

import org.geogebra.common.properties.PropertyObserver;
import org.geogebra.common.properties.ValuedProperty;

/**
 * Decorates a property with an observer.
 * @param <S> value type
 */
public class PropertyObserverDecorator<S> implements ValuedProperty<S> {

	private ValuedProperty<S> property;
	private PropertyObserver observer;

	public PropertyObserverDecorator(ValuedProperty<S> property, PropertyObserver observer) {
		this.property = property;
		this.observer = observer;
	}

	@Override
	public String getName() {
		return property.getName();
	}

	@Override
	public boolean isEnabled() {
		return property.isEnabled();
	}

	@Override
	public S getValue() {
		return property.getValue();
	}

	@Override
	public void setValue(S value) {
		property.setValue(value);
		notifyObserver();
	}

	private void notifyObserver() {
		observer.onChange(property);
	}
}
