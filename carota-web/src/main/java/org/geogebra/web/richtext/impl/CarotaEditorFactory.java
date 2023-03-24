package org.geogebra.web.richtext.impl;

import org.gwtproject.dom.client.Element;

import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = "carota")
public class CarotaEditorFactory {
	public native CarotaDocument create(Element div);
}
