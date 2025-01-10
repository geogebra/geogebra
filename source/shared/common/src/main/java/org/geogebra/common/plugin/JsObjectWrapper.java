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

	public void ifIntPropertySet(String key, Consumer<Integer> consumer) {
		ifPropertySet(key, consumer);
	}

	public void ifObjectPropertySet(String key, Consumer<JsObjectWrapper> consumer) {
		ifPropertySet(key, obj -> consumer.accept(wrap(obj)));
	}

	public abstract void setProperty(String property, Object value);

	public abstract void setProperty(String property, int value);

	public abstract Object getNativeObject();
}
