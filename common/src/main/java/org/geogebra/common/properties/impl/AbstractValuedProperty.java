package org.geogebra.common.properties.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.PropertiesRegistry;
import org.geogebra.common.properties.PropertyValueObserver;
import org.geogebra.common.properties.ValuedProperty;

/**
 * A base class for implementing value setting and getting of a property with listeners.
 */
public abstract class AbstractValuedProperty<S> extends AbstractProperty
		implements ValuedProperty<S> {

	private S previousValue = null;

	private final Set<PropertyValueObserver> observers = new HashSet<>();

	/**
	 * Constructs an abstract property.
	 * @param localization this is used to localize the name
	 * @param name the name to be localized
	 */
	public AbstractValuedProperty(Localization localization, String name) {
		super(localization, name);
	}

	/**
	 * Constructs a new property.
	 *
	 * @param localization Used for localizing the property's name.
	 * @param name The property's name.
	 * @param propertiesRegistry A {@link PropertiesRegistry} (may be null). If a registry is
	 * passed in, the newly created property will register itself with the registry.
	 */
	public AbstractValuedProperty(Localization localization, String name,
			PropertiesRegistry propertiesRegistry) {
		super(localization, name, propertiesRegistry);
	}

	@Override
	public void addValueObserver(PropertyValueObserver observer) {
		observers.add(observer);
	}

	@Override
	public void removeValueObserver(PropertyValueObserver observer) {
		observers.remove(observer);
	}

	@Override
	public final void setValue(S value) {
		if (isFrozen()) {
			return;
		}
		doSetValue(value);
		notifyObservers(observer -> observer.onDidSetValue(this));
	}

	@Override
	public final void beginSetValue() {
		notifyObservers(observer -> observer.onBeginSetValue(this));
	}

	@Override
	public final void endSetValue() {
		notifyObservers(observer -> observer.onEndSetValue(this));
	}

	/**
	 * Implement this to set the value of the property.
	 * @param value property value
	 */
	protected abstract void doSetValue(S value);

	private void notifyObservers(Consumer<PropertyValueObserver> eventCall) {
		observers.forEach(eventCall);
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
