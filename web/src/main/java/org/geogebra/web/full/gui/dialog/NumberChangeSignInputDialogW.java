package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.gui.dialog.handler.NumberChangeSignInputHandler;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.ComponentInputDialog;
import org.geogebra.web.shared.components.DialogData;

import com.google.gwt.user.client.ui.CheckBox;

/**
 * Dialog for one number and changing sign
 */
public class NumberChangeSignInputDialogW extends ComponentInputDialog {
	private boolean changingSign;
	private CheckBox checkBox;

	/**
	 * 
	 * @param app application
	 * @param message message
	 * @param data dialog data
	 * @param initString initial content
	 * @param handler input handler
	 * @param changingSign
	 *            says if the sign has to be changed
	 * @param checkBoxText
	 *            label for checkbox
	 */
	public NumberChangeSignInputDialogW(AppW app, String message, DialogData data,
			String initString, NumberChangeSignInputHandler handler,
			boolean changingSign, String checkBoxText) {
		super(app, data, false, false, handler, message, initString,
				1, -1, false);
		this.checkBox = new CheckBox(checkBoxText, true);
		this.changingSign = changingSign;
	}

	@Override
	protected void processInputHandler(String inputText,
			AsyncOperation<Boolean> callback) {
		((NumberChangeSignInputHandler) getInputHandler()).processInput(
				inputText, changingSign && checkBox.getValue(), this, callback);
	}
}