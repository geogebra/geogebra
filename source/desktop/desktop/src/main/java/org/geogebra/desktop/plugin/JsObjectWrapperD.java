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
	public void setProperty(String property, Object value) {
		((NativeObject) options).put(property, value);
	}

	@Override
	public void setProperty(String property, int value) {
		((NativeObject) options).put(property, value);
	}

	@Override
	public Object getNativeObject() {
		return options;
	}

	@Override
	protected Object getValue(String key) {
		return ((NativeObject) options).get(key);
	}

}
