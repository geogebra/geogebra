package org.geogebra.web.editor;

import org.geogebra.gwtutil.JsConsumer;
import org.geogebra.keyboard.web.TabbedKeyboard;
import org.geogebra.web.html5.bridge.RenderGgbElement.RenderGgbElementFunction;
import org.geogebra.web.resources.StyleInjector;
import org.gwtproject.canvas.client.Canvas;
import org.gwtproject.dom.client.Element;
import org.gwtproject.dom.style.shared.Overflow;
import org.gwtproject.event.dom.client.ClickEvent;
import org.gwtproject.user.client.DOM;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.RootPanel;

import com.himamis.retex.editor.web.MathFieldW;

import elemental2.dom.DomGlobal;

public final class RenderEditor implements RenderGgbElementFunction {
	private TabbedKeyboard tabbedKeyboard = null;
	private EditorApi editorApi;
	private MathFieldW mathField;

	@Override
	public void render(Element el, JsConsumer<Object> callback) {
		EditorListener listener = new EditorListener();
		mathField = initMathField(el, listener);
		if (tabbedKeyboard == null) {
			tabbedKeyboard = initKeyboard(el);
			DomGlobal.window.addEventListener("resize", evt -> onResize());
			StyleInjector.onStylesLoaded(tabbedKeyboard::show);
			editorApi = new EditorApi(mathField, tabbedKeyboard, listener);
			tabbedKeyboard.setListener((visible, field) -> {
				if (!visible) {
					editorApi.closeKeyboard();
				} else {
					editorApi.openKeyboard();
				}
				return false;
			});
		}
		if (callback != null) {
			callback.accept(editorApi);
		}
	}

	private void onResize() {
		tabbedKeyboard.onResize();
		mathField.setPixelRatio(DomGlobal.window.devicePixelRatio);
	}

	private TabbedKeyboard initKeyboard(Element el) {
		EditorKeyboardContext editorKeyboardContext = new EditorKeyboardContext(el);
		TabbedKeyboard tabbedKeyboard = new TabbedKeyboard(editorKeyboardContext, false);
		tabbedKeyboard.addStyleName("detached");
		FlowPanel keyboardWrapper = new FlowPanel();
		keyboardWrapper.setStyleName("GeoGebraFrame");
		keyboardWrapper.add(tabbedKeyboard);
		RootPanel.get().add(keyboardWrapper);
		tabbedKeyboard.setProcessing(new MathFieldProcessing(mathField));
		tabbedKeyboard.clearAndUpdate();
		return tabbedKeyboard;
	}

	private MathFieldW initMathField(Element el, EditorListener listener) {
		Canvas canvas = Canvas.createIfSupported();
		FlowPanel wrapper = new FlowPanel();
		wrapper.setWidth("100%");
		wrapper.getElement().getStyle().setOverflow(Overflow.HIDDEN);
		mathField = new MathFieldW(null, wrapper, canvas, listener);
		EditorParams editorParams = new EditorParams(el, mathField);
		listener.setMathField(mathField);
		mathField.parse("");
		wrapper.add(mathField);
		setBackgroundColor(canvas);

		if (!editorParams.isPreventFocus()) {
			mathField.requestViewFocus();
		}

		mathField.setPixelRatio(DomGlobal.window.devicePixelRatio);
		mathField.getInternal().setSyntaxAdapter(new EditorSyntaxAdapter());
		RootPanel rootPanel = newRoot(el);
		rootPanel.add(wrapper);

		rootPanel.addDomHandler(evt -> onFocus(), ClickEvent.getType());
		return mathField;
	}

	private void setBackgroundColor(Canvas canvas) {
		canvas.getElement().getStyle()
				.setBackgroundColor(mathField.getBackgroundColor().getCssColor());
	}

	private void onFocus() {
		mathField.requestViewFocus();
		MathFieldProcessing processing = new MathFieldProcessing(mathField);
		tabbedKeyboard.setProcessing(processing);
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
