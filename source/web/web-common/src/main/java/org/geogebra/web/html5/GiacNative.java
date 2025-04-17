package org.geogebra.web.html5;

import org.geogebra.gwtutil.JsRunnable;

import elemental2.core.Function;
import elemental2.core.JsArray;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class GiacNative implements JsPropertyMap<Object> {

	public JsRunnable postRun;

	/**
	 * @param name C function name
	 * @param returnType return type
	 * @param argTypes argument types
	 * @return wrapped function
	 */
	public native Function cwrap(String name, String returnType, JsArray<String> argTypes);

	/**
	 * @return cwrap function as object
	 */
	@JsProperty
	public native Function getCwrap();

}
