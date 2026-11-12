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

package org.geogebra.web.full.util.keyboard;

import org.geogebra.gwtutil.JsObject;
import org.geogebra.keyboard.web.KeyboardListener;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.gwtproject.dom.client.BrowserEvents;

import elemental2.dom.KeyboardEvent;
import elemental2.dom.KeyboardEventInit;

/**
 * Connector for keyboard and input boxes
 */
public final class AutocompleteProcessing implements KeyboardListener {

	private AutoCompleteTextFieldW field;

	/**
	 * Connector for keyboartd and input boxes
	 *
	 * @param field
	 *            input box
	 */
	public AutocompleteProcessing(AutoCompleteTextFieldW field) {
		this.field = field;
	}

	@Override
	public void setFocus(boolean focus) {
		if (field == null) {
			return;
		}

		field.setFocus(focus);
	}

	@Override
	public void onEnter() {
		field.getTextField().onBrowserEvent(new KeyboardEvent(BrowserEvents.KEYDOWN, initEnter()));
		field
				.getTextField()
				.onBrowserEvent(new KeyboardEvent(BrowserEvents.KEYPRESS, initEnterPress()));
		field.getTextField().onBrowserEvent(new KeyboardEvent(BrowserEvents.KEYUP, initEnter()));
	}

	private KeyboardEventInit initEnter() {
		KeyboardEventInit keyDownInit = KeyboardEventInit.create();
		keyDownInit.setCode("Enter");
		JsObject.of(keyDownInit).set("keyCode", ENTER);
		return keyDownInit;
	}

	private KeyboardEventInit initEnterPress() {
		KeyboardEventInit keyDownInit = KeyboardEventInit.create();
		JsObject.of(keyDownInit).set("charCode", ENTER);
		return keyDownInit;
	}

	@Override
	public void onBackSpace() {
		field.onBackSpace();
	}

	@Override
	public boolean isSVCell() {
		return field.getStyleName().indexOf("SpreadsheetEditorCell") >= 0;
	}

	@Override
	public void insertString(String text) {
		field.insertString(text);

		if ("nroot".equals(text)) {
			field.insertString("()");
			onArrow(ArrowType.left);
		}
	}

	@Override
	public void onArrow(ArrowType type) {
		if (type == ArrowType.left) {
			field.onArrowLeft();
		} else if (type == ArrowType.right) {
			field.onArrowRight();
		}
	}

	@Override
	public void endEditing() {
		field.endOnscreenKeyboardEditing();
	}

	@Override
	public AutoCompleteTextFieldW getField() {
		return field;
	}

	@Override
	public void onKeyboardClosed() {
		// not needed
	}

	@Override
	public void ansPressed() {
		// not needed
	}

	@Override
	public boolean requestsAns() {
		return false;
	}
}
