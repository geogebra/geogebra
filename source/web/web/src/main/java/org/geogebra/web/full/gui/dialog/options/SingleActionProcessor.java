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