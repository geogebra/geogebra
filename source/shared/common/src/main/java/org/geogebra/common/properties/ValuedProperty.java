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

package org.geogebra.common.properties;

/**
 * A property that holds a value and hosts value observers.
 * @param <V> the type of the value
 */
public interface ValuedProperty<V> extends Property {

	/**
	 * Gets the property value.
	 *
	 * @return property value
	 */
	V getValue();

	/**
	 * Sets the property value.
	 *
	 * @param value value
	 */
	void setValue(V value);

	/**
	 * Marks this property as setting value. Call this method for properties whose values
	 * might be set multiple times, for some period. After the quick successive changes end,
	 * call {@link ValuedProperty#endSetValue()}.
	 * <p>
	 * For example, when using a {@link RangeProperty} and displaying it as a slider,
	 * adjusting the slider produces a large amount of {@link ValuedProperty#setValue(Object)} calls.
	 * In order to optimize this, some properties (or value observers) might decide to delay
	 * the handling of setValue after {@link ValuedProperty#endSetValue()} has been called.
	 */
	void beginSetValue();

	/**
	 * Marks this property as setting value has ended. For every call of this method,
	 * there must be a preceding {@link ValuedProperty#beginSetValue()} call, otherwise
	 * the functionality is undefined.
	 */
	void endSetValue();

	/**
	 * Adds a property value observer. Adding an observer that is
	 * already registered with this property has no effect.
	 *
	 * @param observer value observer
	 */
	void addValueObserver(PropertyValueObserver observer);

	/**
	 * Removes a property value observer. Removing an observer that is
	 * not registered with this property has no effect.
	 * 
	 * @param observer value observer
	 */
	void removeValueObserver(PropertyValueObserver observer);
}
