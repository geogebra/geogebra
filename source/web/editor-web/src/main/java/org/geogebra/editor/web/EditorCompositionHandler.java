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

package org.geogebra.editor.web;

import org.geogebra.editor.share.event.KeyEvent;
import org.geogebra.editor.share.tree.Korean;
import org.geogebra.editor.share.util.JavaKeyCodes;
import org.gwtproject.event.dom.client.KeyCodeEvent;
import org.gwtproject.event.dom.client.KeyDownEvent;
import org.gwtproject.event.dom.client.KeyDownHandler;
import org.gwtproject.event.dom.client.KeyUpEvent;
import org.gwtproject.event.dom.client.KeyUpHandler;

import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

/**
 * Composition handler for math input
 * 
 * @author Zbynek
 */
final class EditorCompositionHandler
		implements CompositionHandler, CompositionEndHandler, KeyDownHandler, KeyUpHandler {

	private MathFieldW editor;
	private boolean insertOnEnd = false;

	boolean backspace = false;

	/**
	 * @param editor
	 *            equation editor
	 */
	EditorCompositionHandler(MathFieldW editor) {
		this.editor = editor;
	}

	void attachTo(MyTextArea inputTextArea) {
		inputTextArea.addCompositionUpdateHandler(this);
		inputTextArea.addCompositionEndHandler(this);
		inputTextArea.addKeyDownHandler(this);
		inputTextArea.addKeyUpHandler(this);
	}

	@Override
	public void onCompositionUpdate(CompositionUpdateEvent event) {
		if (backspace) {
			return;
		}

		// this works fine for Korean as the editor has support for
		// combining Korean characters
		// but for eg Japanese probably will need to hook into
		// compositionstart & compositionend events as well

		// in Chrome typing fast gives \u3137\uB450
		// instead of \u3137\u315C
		// so flatten the result and send just the last character
		String data = Korean.flattenKorean(event.getData());
		// ^: fix for swedish
		if (!"^".equals(data) && data.length() > 0) {
			char inputChar = data.charAt(data.length() - 1);
			char lastChar = Korean.unmergeDoubleCharacterForEditor(inputChar);
			if (Korean.isCompatibilityChar(lastChar)
					|| Korean.isSingleKoreanChar(lastChar)) {
				editor.insertString("" + lastChar);
			} else {
				insertOnEnd = true;
			}

		}
	}

	@Override
	public void onCompositionEnd(CompositionEndEvent event) {
		if (insertOnEnd) {
			// inserted string should only depend on `compositionend`
			// in Safari the data in `cmpositionupdate` is just Latin chars
			editor.insertString(event.getData());
			editor.getInternal().notifyAndUpdate(event.getData());
		}
	}

	private <T> boolean composingBackspace(KeyCodeEvent<T> event) {
		JsPropertyMap<Object> nativeEvent = Js.asPropertyMap(event.getNativeEvent());
		return Js.isTruthy(nativeEvent.get("isComposing"))
			&& "Backspace".equals(nativeEvent.get("code"));
	}

	@Override
	public void onKeyDown(KeyDownEvent event) {
		if (composingBackspace(event)) {
			editor.getKeyListener().onKeyPressed(new KeyEvent(JavaKeyCodes.VK_BACK_SPACE,
					KeyEvent.KeyboardType.EXTERNAL));
			backspace = true;
		}
	}

	@Override
	public void onKeyUp(KeyUpEvent event) {
		if (composingBackspace(event)) {
			editor.clearState();
			backspace = false;
		}
	}
}