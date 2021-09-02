package org.geogebra.gwtutil;

import elemental2.dom.Document;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class XMLSerializer {
	public native String serializeToString(Document doc);
}
