package org.geogebra.web.full.gui.dialog;

import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;

/**
 * Input handlers of a text field.
 * @author laszlo
 */
public class MediaInputKeyHandler implements KeyPressHandler, KeyUpHandler {

	private final ProcessInput input;

	/**
	 * @param processInput the process, input change code.
	 */
	public MediaInputKeyHandler(ProcessInput processInput) {
		this.input = processInput;
	}

	/**
	 *
	 * @param field to attach the handlers to.
	 */
	void attachTo(AutoCompleteTextFieldW field) {
		field.getTextBox().addKeyUpHandler(this);
		field.getTextBox().addKeyPressHandler(this);
		addNativeInputHandler(field.getInputElement());
	}

	private native void addNativeInputHandler(Element elem) /*-{
		var that = this;
		elem.addEventListener("input", function () {
			that.@org.geogebra.web.full.gui.dialog.MediaInputPanel::onInput()();
		});
	}-*/;

	@Override
	public void onKeyPress(KeyPressEvent event) {
		if (isEnter(event.getCharCode())) {
			input.processInput();
		}
	}

	private boolean isEnter(int key) {
		return key == KeyCodes.KEY_ENTER;
	}

	@Override
	public void onKeyUp(KeyUpEvent event) {
		if (isEnter(event.getNativeEvent().getKeyCode())) {
			return;
		}

		input.onInput();
	}
}
