package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.gui.InputHandler;
import org.geogebra.web.full.gui.components.ComponentInputDialog;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.DialogData;

public class NumberInputDialog extends ComponentInputDialog {

	/**
	 * dialog constructor
	 * @param app - see {@link AppW}
	 * @param dialogData - contains trans keys for title and buttons
	 * @param autoHide - if the dialog should be closed on click outside
	 * @param hasScrim - background should be greyed out
	 * @param inputHandler - input handler
	 * @param labelText - label of input text field
	 * @param initText - initial text of the field
	 */
	public NumberInputDialog(AppW app, DialogData dialogData,
			boolean autoHide, boolean hasScrim, InputHandler inputHandler,
			String labelText, String initText) {
		super(app, dialogData, autoHide, hasScrim, inputHandler, labelText, initText
		);
	}

	@Override
	public void processInput() {
		processInputHandler(getInputText(), ok -> setVisible(!ok));
	}
}