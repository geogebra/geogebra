package org.geogebra.common.plugin;

import java.util.function.Consumer;

public abstract class JsObjectWrapper {

	/**
	 * Apply given setter if the value of given key is not null
	 * @param key property name
	 * @param setter property setter
	 * @param <T> property type
	 */
	public <T> void ifKeyNotNull(String key, Consumer<T> setter) {
		Object raw = getValue(key);
		if (raw != null) {
			setter.accept((T) raw);
		}
	}

	protected abstract Object getValue(String key);

	protected abstract JsObjectWrapper wrap(Object nativeObject);

	public void getObjectOptionValue(String key, Consumer<JsObjectWrapper> consumer) {
		ifKeyNotNull(key, obj -> consumer.accept(wrap(obj)));
	}
}
