package org.geogebra.web.editor;

import org.geogebra.keyboard.web.TabbedKeyboard;
import org.geogebra.web.html5.bridge.RenderGgbElement;
import org.geogebra.web.resources.StyleInjector;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.himamis.retex.editor.web.JlmEditorLib;
import com.himamis.retex.editor.web.MathFieldW;
import com.himamis.retex.renderer.web.CreateLibrary;
import com.himamis.retex.renderer.web.JlmApi;
import com.himamis.retex.renderer.web.font.opentype.Opentype;

import elemental2.dom.DomGlobal;
import jsinterop.base.Js;

public class EditorEntry implements EntryPoint {

	@Override
	public void onModuleLoad() {
		elemental2.dom.Element script = DomGlobal.document
				.querySelector("[src$=\"editor.nocache.js\"]");
		String baseUrl = GWT.getModuleBaseURL();
		if (script != null && !isSuperDev()) {
			baseUrl = script.getAttribute("src");
			baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf("/") + 1);
		}
		new StyleInjector(baseUrl)
				.inject("css", "editor");
		Opentype.setFontBaseUrl(baseUrl);
		CreateLibrary.exportLibrary(new JlmApi(new JlmEditorLib()));
		RenderGgbElement.setRenderGGBElement((el, callback) -> {
			EditorListener listener = new EditorListener();

			MathFieldW mf = initMathField(el, listener);
			TabbedKeyboard tabbedKeyboard = initKeyboard(mf, el);
			StyleInjector.onStylesLoaded(tabbedKeyboard::show);
			EditorApi editorApi = new EditorApi(mf, tabbedKeyboard, listener);
			tabbedKeyboard.setListener((visible, field) -> {
				if (!visible) {
					editorApi.closeKeyboard();
				} else {
					editorApi.openKeyboard();
				}
				return false;
			});
			if (callback != null) {
				callback.accept(editorApi);
			}
		});
		RenderGgbElement.renderGGBElementReady();
	}

	private boolean isSuperDev() {
		return Js.asPropertyMap(DomGlobal.window).has("__gwt_sdm");
	}

	private TabbedKeyboard initKeyboard(MathFieldW mf, Element el) {
		EditorKeyboardContext editorKeyboardContext = new EditorKeyboardContext(el);
		TabbedKeyboard tabbedKeyboard = new TabbedKeyboard(editorKeyboardContext, false);
		FlowPanel keyboardWrapper = new FlowPanel();
		keyboardWrapper.setStyleName("GeoGebraFrame");
		keyboardWrapper.add(tabbedKeyboard);
		RootPanel.get().add(keyboardWrapper);
		mf.requestViewFocus();
		tabbedKeyboard.setProcessing(new MathFieldProcessing(mf));
		tabbedKeyboard.clearAndUpdate();
		DomGlobal.window.addEventListener("resize", evt -> tabbedKeyboard.onResize());
		return tabbedKeyboard;
	}

	private MathFieldW initMathField(Element el, EditorListener listener) {
		Canvas canvas = Canvas.createIfSupported();
		FlowPanel wrapper = new FlowPanel();
		wrapper.setWidth("100%");
		wrapper.getElement().getStyle().setOverflow(Overflow.HIDDEN);
		MathFieldW mf = new MathFieldW(null, wrapper, canvas, listener);
		String backgroundColor = el.getAttribute("data-param-editorbackgroundcolor");
		if (!"".equals(backgroundColor)) {
			mf.setBackgroundColor(backgroundColor);
		}
		String foregroundColor = el.getAttribute("data-param-editorforegroundcolor");
		if (!"".equals(foregroundColor)) {
			mf.setForegroundColor(foregroundColor);
		}
		mf.setFontSize(toDouble(el.getAttribute("data-param-fontsize"), 16.0));
		listener.setMathField(mf);
		mf.parse("");
		wrapper.add(mf);
		RootPanel rootPanel = newRoot(el);
		rootPanel.add(wrapper);
		rootPanel.addDomHandler(evt -> mf.requestViewFocus(), ClickEvent.getType());
		return mf;
	}

	private double toDouble(String attribute, double fallback) {
		if (!"".equals(attribute)) {
			try {
				return Double.parseDouble(attribute);
			} catch (NumberFormatException ex) {
				// fallback
			}
		}
		return fallback;
	}

	private RootPanel newRoot(Element el) {
		Element detachedKeyboardParent = DOM.createDiv();
		detachedKeyboardParent.setClassName("GeoGebraFrame editor");
		String uid = DOM.createUniqueId();
		detachedKeyboardParent.setId(uid);
		el.appendChild(detachedKeyboardParent);
		return RootPanel.get(uid);
	}

}
