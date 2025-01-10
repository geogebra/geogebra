package org.geogebra.web.html5.util.pdf;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class PDFPageProxy {
	public native PageViewPort getViewport(Object o);

	public native RenderTask render(JsPropertyMap<Object> renderContext);
}
