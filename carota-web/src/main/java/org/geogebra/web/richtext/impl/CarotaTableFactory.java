package org.geogebra.web.richtext.impl;

import org.gwtproject.dom.client.Element;

import jsinterop.annotations.JsType;

@JsType(isNative = true)
public class CarotaTableFactory {
	public native CarotaTable create(Element element);
}
