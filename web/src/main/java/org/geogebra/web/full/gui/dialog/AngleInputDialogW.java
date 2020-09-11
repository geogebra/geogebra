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
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.ComponentInputDialog;
import org.geogebra.web.shared.components.DialogData;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RadioButton;

public class AngleInputDialogW extends ComponentInputDialog {

	protected RadioButton rbCounterClockWise;
	protected RadioButton rbClockWise;

	/**
	 * Input Dialog for a GeoAngle object.
	 */
	public AngleInputDialogW(AppW app, String message, DialogData data,
			String initString, InputHandler handler, boolean modal) {
		super(app, data, true, false, handler,
				app.getLocalization().getMenu(message), initString,
				1, -1, false);
		super.setModal(modal);
		setInputHandler(handler);
		extendGUI();
	}

	private void extendGUI() {
		//setInitString(initString);
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

		/*createGUI(title, message, autoComplete, DEFAULT_COLUMNS, 1,
				true, false);*/

		/*VerticalPanel centerPanel = new VerticalPanel();
		centerPanel.add(messagePanel);
		centerPanel.add(inputPanel);
		centerPanel.add(errorPanel);
		centerPanel.add(rbPanel);
		((VerticalPanel) wrappedPopup.getWidget()).insert(centerPanel, 0);*/

		//wrappedPopup.center();
		addDialogContent(rbPanel);
		getTextComponent().setFocus(true);
	}

	/*public boolean isCounterClockWise() {
		return rbCounterClockWise.getValue();
	}*/

	/*@Override
	protected void actionPerformed(DomEvent<?> e) {
		Object source = e.getSource();

		try {

			if (source == btOK || sourceShouldHandleOK(source)) {
				String inputTextWithSign = getInputText();
				getTextComponent().hideTablePopup();

				// negative orientation ?
				if (rbClockWise.getValue()) {
					inputTextWithSign = "-(" + inputTextWithSign + ")";
				}

				getInputHandler().processInput(inputTextWithSign, this,
						ok -> hide());
			}
		} catch (Exception ex) {
			//afterActionPerformed(false);
			// do nothing on uninitializedValue
		}
	}

	/*void afterActionPerformed(boolean finished) {
		if (finished) {
			wrappedPopup.hide();
			app.getActiveEuclidianView().requestFocusInWindow();
		} else {
			wrappedPopup.show();
		}
	}*/
}