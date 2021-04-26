package org.geogebra.web.html5.export;

import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.resources.JavaScriptInjector;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "window")
public final class ExportLoader {

	private ExportLoader() {
		// utility class
	}

	@JsProperty(name = "C2S")
	public static native Object getCanvas2Svg();

	/**
	 *
	 * @return true if canvas2pdf is already loaded
	 */
	@JsProperty(name = "canvas2pdf")
	public static native Object getCanvas2Pdf();

	/**
	 * @return whether canvas2svg was loaded
	 */
	@JsOverlay
	public static boolean ensureCanvas2SvgLoaded() {
		if (getCanvas2Svg() == null) {
			JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE.canvas2Svg());
		}
		if (getCanvas2Svg() == null) {
			Log.debug("canvas2SVG failed to load");
			return false;
		}
		return true;
	}

}
