package org.geogebra.desktop.plugin;

import org.geogebra.common.plugin.JsObjectWrapper;
import org.mozilla.javascript.NativeObject;

public class JsObjectWrapperD extends JsObjectWrapper {
	private final Object options;

	public JsObjectWrapperD(Object obj) {
		this.options = obj;
	}

	@Override
	public JsObjectWrapper wrap(Object options) {
		return new JsObjectWrapperD(options);
	}

	@Override
	protected Object getValue(String key) {
		return ((NativeObject) options).get(key);
	}

}
