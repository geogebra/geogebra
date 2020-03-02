package org.geogebra.web.full.gui.dialog;

import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;

/**
 * Input handlers of a text field.
 * @author laszlo
 */
public class MediaInputKeyHandler implements KeyPressHandler {

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
		field.getTextBox().addKeyPressHandler(this);
		addNativeInputHandler(input, field.getInputElement());
	}

	private native void addNativeInputHandler(ProcessInput input, Element elem) /*-{
		elem.addEventListener("input", function () {
			input.@org.geogebra.web.full.gui.dialog.ProcessInput::onInput()();
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

}
