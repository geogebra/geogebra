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

package org.geogebra.web.editor;

import org.geogebra.editor.share.event.MathFieldListener;
import org.geogebra.editor.share.serializer.TeXSerializer;
import org.geogebra.editor.web.MathFieldW;
import org.geogebra.gwtutil.ExceptionUnwrapper;
import org.gwtproject.user.client.ui.Widget;

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

	/**
	 * Register client listener.
	 * @param fn client listener
	 */
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
