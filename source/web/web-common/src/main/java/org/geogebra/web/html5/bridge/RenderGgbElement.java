/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
