package org.geogebra.web.html5.util.h5pviewer;

import elemental2.dom.Element;
import elemental2.promise.IThenable.ThenOnFulfilledCallbackFn;
import elemental2.promise.Promise;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;

@JsType(isNative = true, namespace = "H5PStandalone")
final public class H5P {
	@SuppressWarnings("unused")
	public H5P(Element element, String location,
			JsPropertyMap<Object> options, JsPropertyMap<Object> displayOptions) {
		// leave empty
	}

	public native <H5P> Promise<H5P> then(ThenOnFulfilledCallbackFn<H5P, H5P> onFulfilled);
}
