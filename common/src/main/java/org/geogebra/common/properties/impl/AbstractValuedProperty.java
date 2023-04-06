package org.geogebra.common.properties.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.PropertyValueObserver;
import org.geogebra.common.properties.ValuedProperty;

/**
 * A base class for implementing value setting and getting of a property with listeners.
 */
public abstract class AbstractValuedProperty<S> extends AbstractProperty
		implements ValuedProperty<S> {

	private final Set<PropertyValueObserver> observers = new HashSet<>();

	/**
	 * Constructs an abstract property.
	 * @param localization this is used to localize the name
	 * @param name the name to be localized
	 */
	public AbstractValuedProperty(Localization localization, String name) {
		super(localization, name);
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
		doSetValue(value);
		notifyObservers(observer -> observer.onChange(this));
	}

	@Override
	public final void startChangingValue() {
		willStartChangingValue();
		notifyObservers(observer -> observer.onStartChanging(this));
	}

	@Override
	public final void endChangingValue() {
		didEndChangingValue();
		notifyObservers(observer -> observer.onEndChanging(this));
	}

	/**
	 * Implement this to set the value of the property.
	 * @param value property value
	 */
	protected abstract void doSetValue(S value);

	/**
	 * Callback to when the property value starts changing.
	 * For example, here you can start caching values to
	 * {@link AbstractValuedProperty#doSetValue(Object)} and apply them only in
	 * {@link AbstractValuedProperty#didEndChangingValue()}.
	 * For more information, see {@link ValuedProperty#startChangingValue()}.
	 */
	protected void willStartChangingValue() {
		// Subclasses might override
	}

	/**
	 * Callback to when the property value stops changing.
	 * It is not enforced that this method is called
	 * after {@link AbstractValuedProperty#willStartChangingValue()}.
	 */
	protected void didEndChangingValue() {
		// Subclasses might override
	}

	private void notifyObservers(Consumer<PropertyValueObserver> eventCall) {
		observers.forEach(eventCall);
	}
}
