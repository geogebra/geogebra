package org.geogebra.web.html5;

import org.geogebra.web.html5.util.JsRunnable;

import elemental2.core.Function;
import elemental2.core.JsArray;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class GiacNative implements JsPropertyMap<Object> {

	public JsRunnable postRun;

	public native Function cwrap(String name, String returnType, JsArray<String> argTypes);

	@JsProperty
	public native Function getCwrap();

}
