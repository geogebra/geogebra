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
