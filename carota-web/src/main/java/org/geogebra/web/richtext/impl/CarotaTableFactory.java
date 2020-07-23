package org.geogebra.web.richtext.impl;

import com.google.gwt.dom.client.Element;

import jsinterop.annotations.JsType;

@JsType(isNative = true)
public class CarotaTableFactory {
	public native CarotaTable create(Element element);
}
