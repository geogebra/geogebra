package org.geogebra.web.html5;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;

import elemental2.core.Function;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;

/**
 * GeoGebra-specific global variables (related to deployggb)
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "window")
public class GeoGebraGlobal {

	public static GiacNative __ggb__giac;
	public static JsPropertyMap<JsPropertyMap<JsPropertyMap<String>>> __GGB__keysVar;

	@JsProperty(name = "renderGGBElement")
	public static native void setRenderGGBElement(RenderGgbElementFunction callback);

	@JsProperty(name = "renderGGBElementReady")
	public static native Function getRenderGGBElementReady();

	@JsProperty(name = "ggbExportFile")
	public static native Function getGgbExportFile();

	@JsProperty(name = "changeMetaTitle")
	public static native Function getChangeMetaTitle();

	@JsProperty
	public static native Function getGgbHeaderResize();

	@JsFunction
	public interface RenderGgbElementFunction {
		void render(Element el, JavaScriptObject callback);
	}
}
