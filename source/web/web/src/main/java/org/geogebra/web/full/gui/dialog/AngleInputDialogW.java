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

import java.util.Arrays;

import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.StringUtil;
import org.geogebra.editor.share.util.Unicode;
import org.geogebra.web.full.gui.components.ComponentInputDialog;
import org.geogebra.web.full.gui.components.radiobutton.RadioButtonData;
import org.geogebra.web.full.gui.components.radiobutton.RadioButtonPanel;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.DialogData;
import org.gwtproject.core.client.Scheduler;

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

	@Override
	public void show() {
		super.show();
		Scheduler.get().scheduleDeferred(() -> {
			getTextComponent().selectAll();
		});
	}
}
