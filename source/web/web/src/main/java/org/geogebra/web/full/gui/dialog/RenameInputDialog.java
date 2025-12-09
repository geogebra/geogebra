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

package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.gui.InputHandler;
import org.geogebra.web.full.gui.components.ComponentInputDialog;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.DialogData;

public class RenameInputDialog extends ComponentInputDialog {

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
	public RenameInputDialog(AppW app,
			DialogData dialogData, boolean autoHide,
			boolean hasScrim, InputHandler inputHandler,
			String labelText, String initText) {
		super(app, dialogData, autoHide, hasScrim, inputHandler, labelText, initText);
	}

	@Override
	public void processInput() {
		getInputHandler().processInput(getInputText(), this,
				ok -> {
					if (ok) {
						hide();
					}
		});
	}
}