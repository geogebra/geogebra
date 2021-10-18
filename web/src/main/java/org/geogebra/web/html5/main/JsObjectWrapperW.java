package org.geogebra.web.html5.main;

import org.geogebra.common.plugin.JsObjectWrapper;

import jsinterop.base.Js;

public class JsObjectWrapperW extends JsObjectWrapper {
	private final Object options;

	public JsObjectWrapperW(Object options) {
		this.options = options;
	}

	@Override
	protected Object getValue(String key) {
		return Js.asPropertyMap(options).get(key);
	}

	@Override
	public JsObjectWrapper wrap(Object nativeObject) {
		return new JsObjectWrapperW(nativeObject);
	}
}
