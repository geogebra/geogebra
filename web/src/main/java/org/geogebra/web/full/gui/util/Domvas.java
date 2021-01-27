package org.geogebra.web.full.gui.util;

import org.geogebra.web.html5.util.JsConsumer;

import com.google.gwt.dom.client.Element;

import elemental2.dom.BaseRenderingContext2D;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "window")
public class Domvas {

	protected Domvas() {
		// use Domvas.get() instead, may return null
	}

	@JsProperty(name = "domvas")
	public static native Domvas get();

	public native void toImage(Element el,
			JsConsumer<BaseRenderingContext2D.DrawImageImageUnionType> callback);

}
