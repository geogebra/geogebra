package org.geogebra.desktop.gui.dialog;

import javax.swing.JCheckBox;

import org.geogebra.common.gui.dialog.handler.NumberChangeSignInputHandler;
import org.geogebra.common.gui.view.algebra.DialogType;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.desktop.main.AppD;

/**
 * InputDialog with checkbox to change sign
 * 
 * @author mathieu
 *
 */
public class NumberChangeSignInputDialog extends InputDialogD {

	private boolean changingSign;

	/**
	 * 
	 * @param app
	 * @param message
	 * @param title
	 * @param initString
	 * @param handler
	 * @param changingSign
	 *            says if the sign has to be changed
	 * @param checkBoxText
	 *            text for sign change checkbox
	 * @param basis
	 *            TODO
	 */
	public NumberChangeSignInputDialog(AppD app, String message, String title,
			String initString, NumberChangeSignInputHandler handler,
			boolean changingSign, String checkBoxText) {
		super(app, message, title, initString, false, handler, true, false,
				null, new JCheckBox(checkBoxText, true), DialogType.TextArea);

		this.changingSign = changingSign;
	}

	@Override
	protected void processInputHandler(String inputText,
			AsyncOperation<Boolean> callback) {
		((NumberChangeSignInputHandler) getInputHandler()).processInput(inputText,
				changingSign && checkBox.isSelected(), this, callback);
	}

	@Override
	protected void loadBtPanel(boolean showApply) {
		btPanel.add(checkBox);
		super.loadBtPanel(showApply);
	}

	@Override
	public void handleDialogVisibilityChange(boolean isVisible) {
		// do nothing
	}
}