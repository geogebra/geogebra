package org.geogebra.web.editor;

import com.himamis.retex.editor.share.event.MathFieldListener;
import com.himamis.retex.editor.share.serializer.TeXSerializer;
import com.himamis.retex.editor.web.MathFieldW;

import elemental2.core.Function;
import elemental2.core.JsArray;
import elemental2.dom.DomGlobal;
import jsinterop.base.JsPropertyMap;

public class EditorListener implements MathFieldListener {

	private final JsArray<Function> listeners = JsArray.of();
	private MathFieldW mathField;

	@Override
	public void onEnter() {
		onKeyTyped("\n");
	}

	@Override
	public void onKeyTyped(String key) {
		mathField.scrollParentHorizontally(mathField.asWidget().getParent());
		JsPropertyMap<Object> event = JsPropertyMap.of();
		event.set("0", "editorKeyTyped");
		event.set("type", "editorKeyTyped");
		event.set("key", key);
		event.set("latex", new TeXSerializer().serialize(mathField.getFormula()));
		notifyListeners(event);
	}

	@Override
	public boolean onArrowKeyPressed(int keyCode) {
		mathField.scrollParentHorizontally(mathField.asWidget().getParent());
		return false;
	}

	@Override
	public void onInsertString() {
		// not needed
	}

	@Override
	public boolean onEscape() {
		return false;
	}

	@Override
	public void onTab(boolean shiftDown) {
		// not needed
	}

	public void registerClientListener(Function fn) {
		listeners.push(fn);
	}

	private void notifyListeners(Object o) {
		listeners.forEach((fn, index, ignore) -> fn.call(DomGlobal.window, o));
	}

	public void setMathField(MathFieldW mf) {
		this.mathField = mf;
	}
}
