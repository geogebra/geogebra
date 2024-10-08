package org.geogebra.web.editor;

import org.geogebra.gwtutil.JsConsumer;
import org.geogebra.gwtutil.NativePointerEvent;
import org.geogebra.web.html5.bridge.AttributeProvider;
import org.geogebra.web.html5.bridge.RenderGgbElement.RenderGgbElementFunction;
import org.geogebra.web.html5.gui.util.Dom;
import org.gwtproject.canvas.client.Canvas;
import org.gwtproject.dom.client.Element;
import org.gwtproject.dom.style.shared.Overflow;
import org.gwtproject.event.dom.client.ClickEvent;
import org.gwtproject.user.client.DOM;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.RootPanel;

import com.himamis.retex.editor.web.MathFieldW;

import elemental2.dom.DomGlobal;
import elemental2.dom.Event;
import elemental2.dom.Node;
import jsinterop.base.Js;

public final class RenderEditor implements RenderGgbElementFunction {
	private final EditorKeyboard editorKeyboard;
	private EditorApi editorApi;

	public RenderEditor(EditorKeyboard editorKeyboard) {
		this.editorKeyboard = editorKeyboard;
	}

	@Override
	public void render(Object element, JsConsumer<Object> callback) {
		AttributeProvider attributes = AttributeProvider.as(element);
		editorKeyboard.create(attributes);
		EditorListener listener = new EditorListener();
		MathFieldW mathField = initMathField(attributes, listener);
		DomGlobal.window.addEventListener("resize", evt -> onResize(mathField));
		editorApi = new EditorApi(mathField, editorKeyboard.getTabbedKeyboard(), listener);
		editorKeyboard.setListener(() -> editorApi.closeKeyboard());
		if (callback != null) {
			callback.accept(editorApi);
		}
	}

	private void onResize(MathFieldW mathField) {
		mathField.setPixelRatio(DomGlobal.window.devicePixelRatio);
	}

	private MathFieldW initMathField(AttributeProvider el, EditorListener listener) {
		Canvas canvas = Canvas.createIfSupported();
		FlowPanel wrapper = new FlowPanel();
		wrapper.setWidth("100%");
		wrapper.getElement().getStyle().setOverflow(Overflow.HIDDEN);
		MathFieldW mathField = new MathFieldW(null, wrapper, canvas, listener);
		if (el.hasAttribute("maxHeight")) {
			mathField.setMaxHeight(Double.parseDouble(el.getAttribute("maxHeight")));
		}
		final EditorParams editorParams = new EditorParams(el, mathField);
		listener.setMathField(mathField);
		mathField.parse("");
		wrapper.add(mathField);

		mathField.setPixelRatio(DomGlobal.window.devicePixelRatio);
		mathField.getInternal().setSyntaxAdapter(new EditorSyntaxAdapter());
		mathField.setAriaLabel("Enter your equation or expression here");
		RootPanel editorPanel = newRoot(el.getElement());

		editorPanel.add(wrapper);
		String cssColor = mathField.getBackgroundColor().getCssColor();
		setBackgroundColor(wrapper.getElement(), cssColor);
		Dom.addEventListener(wrapper.getElement(), "pointerdown",
				evt -> adjustCaret(evt, mathField));

		MathFieldProcessing processing = new MathFieldProcessing(mathField);
		editorPanel.addDomHandler(evt -> onFocus(mathField, processing), ClickEvent.getType());

		if (!editorParams.isPreventFocus()) {
			onFocus(mathField, processing);
		}

		canvas.getElement().setTabIndex(-1);
		return mathField;
	}

	private void adjustCaret(Event evt, MathFieldW mathField) {
		NativePointerEvent ptr = Js.uncheckedCast(evt);
		Node target = Js.uncheckedCast(evt.target);
		if (!"CANVAS".equals(target.nodeName)) {
			mathField.adjustCaret((int) ptr.getOffsetX(), (int) ptr.getOffsetY(), 1);
		}
	}

	private void setBackgroundColor(Element element, String cssColor) {
		element.getStyle().setBackgroundColor(cssColor);
	}

	private void onFocus(MathFieldW mathField, MathFieldProcessing processing) {
		mathField.requestViewFocus();
		editorKeyboard.setProcessing(processing);
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
