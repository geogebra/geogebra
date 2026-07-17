/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.properties.impl.facade;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;

import org.geogebra.common.properties.PropertyValueObserver;
import org.geogebra.common.properties.ValuedProperty;

abstract class AbstractValuedPropertyListFacade<T extends ValuedProperty<S>, S>
		extends AbstractPropertyListFacade<T>
		implements ValuedProperty<S> {

	private final Set<PropertyValueObserver> observers = new HashSet<>();
	private final BinaryOperator<S> reducer;

	AbstractValuedPropertyListFacade(List<T> properties) {
		super(properties);
		reducer = null;
	}

	AbstractValuedPropertyListFacade(List<T> properties, BinaryOperator<S> reducer) {
		super(properties);
		this.reducer = reducer;
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
		// Iterate through a copy so that observers can remove themselves during a notification
		new ArrayList<>(observers).forEach(observerConsumer);
	}

	private void callProperty(Consumer<T> propertyConsumer) {
		properties.forEach(propertyConsumer);
	}

	@Override
	public S getValue() {
		if (reducer != null) {
			return properties.stream().map(ValuedProperty::getValue).reduce(reducer).orElseThrow();
		}
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
