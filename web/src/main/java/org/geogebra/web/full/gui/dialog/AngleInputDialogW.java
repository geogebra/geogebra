/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package org.geogebra.web.full.gui.dialog;

import java.util.Arrays;

import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.full.gui.components.ComponentInputDialog;
import org.geogebra.web.full.gui.components.radiobutton.RadioButtonData;
import org.geogebra.web.full.gui.components.radiobutton.RadioButtonPanel;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.DialogData;

import com.himamis.retex.editor.share.util.Unicode;

public class AngleInputDialogW extends ComponentInputDialog {

	protected RadioButtonPanel<Boolean> clockWiseRadioButtonPanel;

	/**
	 * Input Dialog for a GeoAngle object.
	 */
	public AngleInputDialogW(AppW app, String message, DialogData data,
			String initString, InputHandler handler, boolean modal) {
		super(app, data, false, false, handler,
				app.getLocalization().getMenu(message), initString
		);
		addStyleName("angleInputDialog");
		super.setModal(modal);
		setInputHandler(handler);
		extendGUI();
	}

	private void extendGUI() {
		Localization loc = app.getLocalization();
		RadioButtonData<Boolean> counterClockwise =
				new RadioButtonData<>("counterClockwise", false);
		RadioButtonData<Boolean> clockwise = new RadioButtonData<>("clockwise", true);
		clockWiseRadioButtonPanel = new RadioButtonPanel<>(loc,
				Arrays.asList(counterClockwise, clockwise), false, null);
		addDialogContent(clockWiseRadioButtonPanel);
		getTextComponent().setFocus(true);

		getTextComponent().addInsertHandler(t -> insertDegreeSymbolIfNeeded());
		getTextComponent().addKeyUpHandler(e -> {
			// return unless digit typed (instead of !Character.isDigit)
			if (e.getNativeKeyCode() < 48
					|| (e.getNativeKeyCode() > 57 && e.getNativeKeyCode() < 96)
					|| e.getNativeKeyCode() > 105) {
				return;
			}
			insertDegreeSymbolIfNeeded();
		});
	}

	public boolean isClockWise() {
		return clockWiseRadioButtonPanel.getValue();
	}

	@Override
	public void processInput() {
		String inputTextWithSign = getInputText();

		// negative orientation ?
		if (isClockWise()) {
			inputTextWithSign = "-(" + inputTextWithSign + ")";
		}

		getInputHandler().processInput(inputTextWithSign, this,
				ok -> hide());
	}

	/*
	 * auto-insert degree symbol when appropriate
	 */
	private void insertDegreeSymbolIfNeeded() {
		AutoCompleteTextFieldW tc = getTextComponent();
		String text = tc.getText();

		// if text already contains degree symbol or variable
		for (int i = 0; i < text.length(); i++) {
			if (!StringUtil.isDigit(text.charAt(i))) {
				return;
			}
		}

		int caretPos = tc.getCaretPosition();
		tc.setText(tc.getText() + Unicode.DEGREE_STRING);
		tc.setCaretPosition(caretPos);
	}
}
