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
		if (isFrozen()) {
			return;
		}
		notifyObservers(observer -> observer.onWillSetValue(this));
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
}
