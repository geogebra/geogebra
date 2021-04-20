package org.geogebra.web.html5.export;

import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.js.ResourcesInjector;
import org.geogebra.web.html5.util.ScriptLoadCallback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ScriptElement;

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
	 * @param callback to be executed when canvas2svg was loaded
	 */
	@JsOverlay
	public static void onCanvas2SvgLoaded(Runnable callback) {
		if (getCanvas2Svg() != null) {
			callback.run();
		} else {
			ScriptElement scriptElement = Document.get().createScriptElement();
			scriptElement.setSrc(GWT.getModuleBaseURL() + "js/canvas2svg.min.js");
			ScriptLoadCallback loadCallback = new ScriptLoadCallback() {
				@Override
				public void onLoad() {
					callback.run();
				}

				@Override
				public void onError() {
					Log.error("Canvas2SVG failed to load");
				}

				@Override
				public void cancel() {
					// only for localization files
				}
			};
			ResourcesInjector.loadJS(scriptElement, loadCallback);
		}
	}

	@JsOverlay
	public static void onCanvas2PdfLoaded(Runnable callback) {
		if (getCanvas2Pdf() != null) {
			callback.run();
		} else {
			ScriptElement scriptElement = Document.get().createScriptElement();
			scriptElement.setSrc(GWT.getModuleBaseURL() + "js/canvas2pdf.min.js");
			ScriptLoadCallback loadCallback = new ScriptLoadCallback() {
				@Override
				public void onLoad() {
					callback.run();
				}

				@Override
				public void onError() {
					Log.error("Canvas2PDF failed to load");
				}

				@Override
				public void cancel() {
					// only for localization files
				}
			};
			ResourcesInjector.loadJS(scriptElement, loadCallback);
		}
	}
}
