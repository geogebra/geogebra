package org.geogebra.web.editor;

import org.geogebra.gwtutil.ExceptionUnwrapper;
import org.gwtproject.user.client.ui.Widget;

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
		scrollOnDemand();
		JsPropertyMap<Object> event = JsPropertyMap.of();
		event.set("0", "editorKeyTyped");
		event.set("type", "editorKeyTyped");
		event.set("key", key);
		event.set("latex", new TeXSerializer().serialize(mathField.getFormula()));
		notifyListeners(event);
	}

	/**
	 * notify listeners about keyboard opening/closing
	 * @param show - true if keyboard shown
	 */
	public void notifyKeyboardVisibilityChange(boolean show) {
		JsPropertyMap<Object> event = JsPropertyMap.of();
		String type = show ? "openKeyboard" : "closeKeyboard";
		event.set("0", type);
		event.set("type", type);
		notifyListeners(event);
	}

	private void scrollOnDemand() {
		Widget parent = mathField.asWidget().getParent();
		mathField.scrollParentHorizontally(parent);
	}

	@Override
	public boolean onArrowKeyPressed(int keyCode) {
		scrollOnDemand();
		return false;
	}

	@Override
	public boolean onEscape() {
		return false;
	}

	@Override
	public boolean onTab(boolean shiftDown) {
		return false;
	}

	public void registerClientListener(Function fn) {
		listeners.push(fn);
	}

	private void notifyListeners(Object o) {
		for (int i = 0; i < listeners.length; i++) {
			try {
				listeners.getAt(i).call(DomGlobal.window, o);
			} catch (Exception e) {
				ExceptionUnwrapper.printErrorMessage(e);
			}
		}
	}

	public void setMathField(MathFieldW mf) {
		this.mathField = mf;
	}
}
