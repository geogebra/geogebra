package org.geogebra.web.editor;

import org.geogebra.editor.web.JlmEditorLib;
import org.geogebra.gwtutil.JsConsumer;
import org.geogebra.web.html5.bridge.RenderGgbElement;
import org.geogebra.web.resources.StyleInjector;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.himamis.retex.renderer.web.CreateLibrary;
import com.himamis.retex.renderer.web.FactoryProviderGWT;
import com.himamis.retex.renderer.web.JlmApi;
import com.himamis.retex.renderer.web.font.opentype.Opentype;

import elemental2.core.Function;
import elemental2.dom.DomGlobal;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

public class EditorEntry implements EntryPoint {

	@Override
	public void onModuleLoad() {
		FactoryProviderGWT.ensureLoaded();
		Function onReady = (Function) Js.asPropertyMap(DomGlobal.window)
				.nestedGet("editor.onReady");
		initFontAndCss();

		EditorKeyboard keyboard = new EditorKeyboard();
		if (onReady != null) { // loaded as module => no globals
			onReady.call(DomGlobal.window, "editor", new RenderEditor(keyboard));
			JlmApi jlmApi = new JlmApi(new JlmEditorLib());
			JsConsumer<JsPropertyMap<?>> draw = jlmApi::drawLatex;
			onReady.call(DomGlobal.window, "formula", draw);
		} else { // fallback
			initJlmLibrary();
			RenderGgbElement.setRenderGGBElement(new RenderEditor(keyboard));
			RenderGgbElement.renderGGBElementReady();
		}
	}

	private void initJlmLibrary() {
		CreateLibrary.exportLibrary(new JlmApi(new JlmEditorLib()));
	}

	private void initFontAndCss() {
		String baseUrl = getBaseUrl();
		new StyleInjector(baseUrl)
				.inject("css", "editor");
		Opentype.setFontBaseUrl(baseUrl);
	}

	private String getBaseUrl() {
		elemental2.dom.Element script = DomGlobal.document
				.querySelector("[src$=\"editor.nocache.js\"]");

		if (script != null && !isSuperDev()) {
			String baseUrl = script.getAttribute("src");
			return baseUrl.substring(0, baseUrl.lastIndexOf("/") + 1);
		}

		return GWT.getModuleBaseURL();
	}

	private boolean isSuperDev() {
		return Js.asPropertyMap(DomGlobal.window).has("__gwt_sdm");
	}
}
