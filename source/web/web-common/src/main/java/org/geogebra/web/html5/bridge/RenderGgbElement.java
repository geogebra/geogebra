package org.geogebra.web.html5.bridge;

import org.geogebra.gwtutil.JsConsumer;

import elemental2.core.Function;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "window")
public class RenderGgbElement {

	@JsProperty(name = "renderGGBElement")
	public static native void setRenderGGBElement(RenderGgbElementFunction callback);

	@JsProperty(name = "renderGGBElementReady")
	public static native Function getRenderGGBElementReady();

	/** Function rendering an app in an element.*/
	@JsFunction
	public interface RenderGgbElementFunction {
		/**
		 * @param el element or object containing parameters
		 * @param callback called when app created, receives the API object
		 */
		void render(Object el, JsConsumer<Object> callback);
	}

	/**
	 * callback when renderGGBElement is ready
	 */
	@JsOverlay
	public static void renderGGBElementReady() {
		Function renderGGBElementReady = getRenderGGBElementReady();
		if (renderGGBElementReady != null) {
			renderGGBElementReady.call();
		}
	}
}
