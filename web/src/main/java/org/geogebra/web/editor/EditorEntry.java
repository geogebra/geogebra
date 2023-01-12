package org.geogebra.web.editor;

import org.geogebra.keyboard.web.TabbedKeyboard;
import org.geogebra.web.html5.GeoGebraGlobal;
import org.geogebra.web.html5.gui.GeoGebraFrameW;
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
import com.himamis.retex.editor.web.MathFieldW;

import elemental2.dom.DomGlobal;

public class EditorEntry implements EntryPoint {

	@Override
	public void onModuleLoad() {
		new StyleInjector(GWT.getModuleBaseURL())
				.inject("css", "editor");
		GeoGebraGlobal.setRenderGGBElement((el, callback) -> {
			EditorListener listener = new EditorListener();

			EditorStubFragments.load();
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
		GeoGebraFrameW.renderGGBElementReady();
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
