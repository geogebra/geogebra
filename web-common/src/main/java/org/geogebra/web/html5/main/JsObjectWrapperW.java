package org.geogebra.web.html5.main;

import java.util.function.Consumer;

import org.geogebra.common.plugin.JsObjectWrapper;

import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

public class JsObjectWrapperW extends JsObjectWrapper {
	private final JsPropertyMap<Object> options;

	public JsObjectWrapperW(Object options) {
		this.options = Js.asPropertyMap(options);
	}

	@Override
	protected Object getValue(String key) {
		return options.get(key);
	}

	@Override
	public void ifIntPropertySet(String key, Consumer<Integer> consumer) {
		ifPropertySet(key, obj -> consumer.accept(Js.coerceToInt(obj)));
	}

	@Override
	public void setProperty(String property, Object value) {
		options.set(property, value);
	}

	@Override
	public void setProperty(String property, int value) {
		options.set(property, value);
	}

	@Override
	public Object getNativeObject() {
		return options;
	}

	@Override
	public JsObjectWrapper wrap(Object nativeObject) {
		return new JsObjectWrapperW(nativeObject);
	}
}
