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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import org.geogebra.common.gui.view.algebra.DialogType;
import org.geogebra.desktop.main.AppD;

public class InputDialogOpenURL extends InputDialogD {

	/**
	 * @param app application
	 */
	public InputDialogOpenURL(AppD app) {
		super(app.getFrame(), false, app.getLocalization());
		this.app = app;

		setInitString("https://");

		// check if there's a string starting http:// already on the clipboard
		// (quite likely!!)
		String clipboardString = app.getStringFromClipboard();
		if (clipboardString != null && (clipboardString.startsWith("http://")
				|| clipboardString.startsWith("https://")
				|| clipboardString.startsWith("www"))) {
			setInitString(clipboardString);
		}

		createGUI(loc.getMenu("OpenWebpage"), loc.getMenu("EnterAppletAddress"),
				false, DEFAULT_COLUMNS, 1, false, true, false, false,
				DialogType.TextArea);
		optionPane.add(inputPanel, BorderLayout.CENTER);
		centerOnScreen();

		inputPanel.selectText();
	}

	@Override
	public void setLabels(String title) {
		wrappedDialog.setTitle(title);

		btOK.setText(loc.getMenu("Open"));
		btCancel.setText(loc.getMenu("Cancel"));
	}

	/**
	 * Handles button clicks for dialog.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		try {
			if (source == btOK || source == inputPanel.getTextComponent()) {
				setVisible(!processInput());
			} else if (source == btApply) {
				processInput();
				// app.setDefaultCursor();
			} else if (source == btCancel) {
				setVisible(false);
			}
		} catch (Exception ex) {
			// do nothing on uninitializedValue
			ex.printStackTrace();
			setVisible(false);
			app.setDefaultCursor();
		}
	}

	private boolean processInput() {
		return app.getGuiManager().loadURL(inputPanel.getText(), true);
	}

}
