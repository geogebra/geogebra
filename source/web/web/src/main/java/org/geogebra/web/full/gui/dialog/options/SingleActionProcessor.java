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

package org.geogebra.web.full.gui.dialog.options;

import org.geogebra.common.euclidian.event.KeyEvent;
import org.geogebra.common.euclidian.event.KeyHandler;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.gwtproject.event.dom.client.BlurEvent;
import org.gwtproject.event.dom.client.BlurHandler;
import org.gwtproject.event.dom.client.FocusEvent;
import org.gwtproject.event.dom.client.FocusHandler;
import org.gwtproject.user.client.Command;

class SingleActionProcessor
		implements FocusHandler, BlurHandler, KeyHandler {
	boolean processed = false;
	Command command;

	public SingleActionProcessor(Command callback) {
		command = callback;
	}

	@Override
	public void onFocus(FocusEvent evt) {
		processed = false;
	}

	@Override
	public void onBlur(BlurEvent evt) {
		if (!processed) {
			processed = true;
			command.execute();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.isEnterKey()) {
			if (!processed) {
				processed = true;
				command.execute();
			}
		}
	}

	protected void handleEvents(AutoCompleteTextFieldW textField) {
		textField.addBlurHandler(this);
		textField.addFocusHandler(this);
		textField.addKeyHandler(this);
	}

}