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

package org.geogebra.common.plugin;

import java.util.function.Consumer;

public abstract class JsObjectWrapper {

	/**
	 * Apply given setter if the value of given property is not null
	 * @param key property name
	 * @param setter property setter
	 * @param <T> property type
	 */
	public <T> void ifPropertySet(String key, Consumer<T> setter) {
		Object raw = getValue(key);
		if (raw != null) {
			setter.accept((T) raw);
		}
	}

	protected abstract Object getValue(String key);

	protected abstract JsObjectWrapper wrap(Object nativeObject);

	/**
	 * Pass int property to the consumer if it exists
	 * @param key property name
	 * @param consumer consumer
	 */
	public void ifIntPropertySet(String key, Consumer<Integer> consumer) {
		ifPropertySet(key, consumer);
	}

	/**
	 * Pass object property to the consumer if it exists
	 * @param key property name
	 * @param consumer consumer
	 */
	public void ifObjectPropertySet(String key, Consumer<JsObjectWrapper> consumer) {
		ifPropertySet(key, obj -> consumer.accept(wrap(obj)));
	}

	/**
	 * Set object property.
	 * @param property property name
	 * @param value property value
	 */
	public abstract void setProperty(String property, Object value);

	/**
	 * Set object property.
	 * @param property property name
	 * @param value property value
	 */
	public abstract void setProperty(String property, int value);

	/**
	 * @return wrapped object
	 */
	public abstract Object getNativeObject();
}
