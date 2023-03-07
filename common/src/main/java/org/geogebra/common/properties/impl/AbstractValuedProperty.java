package org.geogebra.common.properties.impl;

import java.util.HashSet;
import java.util.Set;

import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.PropertyObserver;
import org.geogebra.common.properties.ValuedProperty;

/**
 * Helper class for implementing value setting and getting of a property with listeners.
 */
public abstract class AbstractValuedProperty<S> extends AbstractProperty implements ValuedProperty<S> {

	private Set<PropertyObserver> observers = new HashSet<>();

	/**
	 * Constructs an abstract property.
	 * @param localization this is used to localize the name
	 * @param name the name to be localized
	 */
	public AbstractValuedProperty(Localization localization, String name) {
		super(localization, name);
	}

	@Override
	public void addObserver(PropertyObserver observer) {
		observers.add(observer);
	}

	@Override
	public void removeObserver(PropertyObserver observer) {
		observers.remove(observer);
	}

	@Override
	public final void setValue(S value) {
		doSetValue(value);
		notifyObservers();
	}

	/**
	 * Implement this to set the value of the property.
	 * @param value
	 */
	protected abstract void doSetValue(S value);

	private void notifyObservers() {
		for (PropertyObserver observer : observers) {
			observer.onChange(this);
		}
	}
}
