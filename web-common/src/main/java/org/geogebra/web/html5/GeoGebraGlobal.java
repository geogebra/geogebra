package org.geogebra.web.html5;

import org.geogebra.common.util.InjectJsInterop;
import org.geogebra.gwtutil.JsConsumer;

import com.google.gwt.dom.client.Element;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import elemental2.core.Function;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;

/**
 * GeoGebra-specific global variables (related to deployggb)
 */
@SuppressFBWarnings("MS_SHOULD_BE_FINAL")
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "window")
public class GeoGebraGlobal {

	public static GiacNative __ggb__giac;
	public static @InjectJsInterop Function evalGeoGebraCASExternal;
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

	@JsProperty
	public static native JsConsumer<Object> getLoadWorksheet();

	@JsProperty
	public static native Function getSetUnsavedMessage();

	@JsProperty
	public static native JsConsumer<Boolean> getGgbExamMode();

	@JsProperty
	public static native JsConsumer<String> getGgbAppletOnLoad();

	@JsFunction
	public interface RenderGgbElementFunction {
		void render(Element el, JsConsumer<Object> callback);
	}
}
