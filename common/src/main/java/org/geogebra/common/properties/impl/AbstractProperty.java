package org.geogebra.common.properties.impl;

import java.util.HashSet;
import java.util.Set;

import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.PropertyObserver;
import org.geogebra.common.properties.ValuedProperty;

/**
 * Helper class for implementing the localized name of a property.
 */
public abstract class AbstractProperty<S> implements ValuedProperty<S> {

	private Set<PropertyObserver> observers = new HashSet<>();

	private Localization localization;
	private String name;

	/**
	 * Constructs an abstract property.
	 * @param localization this is used to localize the name
	 * @param name the name to be localized
	 */
	public AbstractProperty(Localization localization, String name) {
		this.localization = localization;
		this.name = name;
	}

	@Override
	public String getName() {
		return localization.getMenu(name);
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	/**
	 * Returns the localization of the class.
	 * @return localization used
	 */
	protected Localization getLocalization() {
		return localization;
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

	protected abstract void doSetValue(S value);

	private void notifyObservers() {
		for (PropertyObserver observer : observers) {
			observer.onChange(this);
		}
	}
}
