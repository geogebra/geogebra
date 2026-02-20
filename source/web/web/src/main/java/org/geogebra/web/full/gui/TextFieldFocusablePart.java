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

package org.geogebra.web.full.gui;

import org.geogebra.web.full.gui.view.FocusablePartW;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.util.Dom;

import elemental2.dom.KeyboardEvent;

public class TextFieldFocusablePart extends FocusablePartW {
	/**
	 * Creates a focusable part for the given widget.
	 * @param textField the underlying {@link AutoCompleteTextFieldW} to be focused
	 * @param focusKey stable semantic key identifying this part
	 * @param accessibleLabel the aria label for the widget
	 */
	public TextFieldFocusablePart(AutoCompleteTextFieldW textField, String focusKey,
			String accessibleLabel) {
		super(textField, focusKey, accessibleLabel);
		Dom.addEventListener(textField.getElement(), "keyup", (event) -> {
			KeyboardEvent e = (KeyboardEvent) event;
			if (" ".equals(e.key) || "Enter".equals(e.key)) {
				textField.requestFocus();
				e.preventDefault();
				e.stopPropagation();
			}
		});
	}

	@Override
	public boolean handlesEnterKey() {
		return true;
	}
}
