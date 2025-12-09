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
	 * @param app application
	 * @param message message
	 * @param title title
	 * @param initString initial text
	 * @param handler input handler
	 * @param changingSign
	 *            says if the sign has to be changed
	 * @param checkBoxText
	 *            text for sign change checkbox
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