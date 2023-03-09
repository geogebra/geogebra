package org.geogebra.common.properties.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.PropertyObserver;
import org.geogebra.common.properties.ValuedProperty;

/**
 * Helper class for implementing value setting and getting of a property with listeners.
 */
public abstract class AbstractValuedProperty<S> extends AbstractProperty
		implements ValuedProperty<S> {

	private final Set<PropertyObserver> observers = new HashSet<>();

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
		notifyObservers(observer -> observer.onChange(this));
	}

	@Override
	public final void startChange() {
		doStartChange();
		notifyObservers(observer -> observer.onStartChange(this));
	}

	@Override
	public final void endChange() {
		doEndChange();
		notifyObservers(observer -> observer.onEndChange(this));
	}

	/**
	 * Implement this to set the value of the property.
	 * @param value property value
	 */
	protected abstract void doSetValue(S value);

	/**
	 * Callback to when property starts changing.
	 */
	protected void doStartChange() {
	}

	/**
	 * Callback to when property ends changing.
	 */
	protected void doEndChange() {
	}

	private void notifyObservers(Consumer<PropertyObserver> eventCall) {
		observers.forEach(eventCall);
	}
}
