/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.ComponentInputDialog;
import org.geogebra.web.shared.components.DialogData;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.himamis.retex.editor.share.util.Unicode;

public class AngleInputDialogW extends ComponentInputDialog {

	protected RadioButton rbCounterClockWise;
	protected RadioButton rbClockWise;

	/**
	 * Input Dialog for a GeoAngle object.
	 */
	public AngleInputDialogW(AppW app, String message, DialogData data,
			String initString, InputHandler handler, boolean modal) {
		super(app, data, false, false, handler,
				app.getLocalization().getMenu(message), initString,
				1, -1, false);
		addStyleName("angleInputDialog");
		super.setModal(modal);
		setInputHandler(handler);
		extendGUI();
	}

	private void extendGUI() {
		Localization loc = app.getLocalization();
		// create radio buttons for "clockwise" and "counter clockwise"
		String id = DOM.createUniqueId();
		rbCounterClockWise = new RadioButton(id,
				loc.getMenu("counterClockwise"));
		rbClockWise = new RadioButton(id, loc.getMenu("clockwise"));
		rbCounterClockWise.setValue(true);

		FlowPanel rbPanel = new FlowPanel();
		rbPanel.setStyleName("DialogRbPanel");
		rbPanel.add(rbCounterClockWise);
		rbPanel.add(rbClockWise);
		addDialogContent(rbPanel);
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

	public boolean isCounterClockWise() {
		return rbCounterClockWise.getValue();
	}

	@Override
	public void processInput() {
		String inputTextWithSign = getInputText();
		getTextComponent().hideTablePopup();

		// negative orientation ?
		if (rbClockWise.getValue()) {
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
