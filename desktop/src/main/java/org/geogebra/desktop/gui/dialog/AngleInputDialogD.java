/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */
package org.geogebra.desktop.gui.dialog;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.gui.view.algebra.DialogType;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.desktop.main.AppD;

/**
 * Input Dialog for a GeoAngle object with additional option to choose between
 * "clock wise" and "counter clockwise"
 * 
 * @author hohenwarter
 */
public class AngleInputDialogD extends InputDialogD {

	protected JRadioButton rbCounterClockWise, rbClockWise;

	/**
	 * Input Dialog for a GeoAngle object.
	 */
	public AngleInputDialogD(AppD app, String message, String title,
			String initString, boolean autoComplete, InputHandler handler,
			boolean modal) {
		super(app.getFrame(), modal, app.getLocalization());
		this.app = app;
		setInputHandler(handler);
		this.setInitString(initString);

		// create radio buttons for "clockwise" and "counter clockwise"
		ButtonGroup bg = new ButtonGroup();
		rbCounterClockWise = new JRadioButton(loc.getMenu("counterClockwise"));
		rbClockWise = new JRadioButton(loc.getMenu("clockwise"));
		bg.add(rbCounterClockWise);
		bg.add(rbClockWise);
		rbCounterClockWise.setSelected(true);
		JPanel rbPanel = new JPanel(new BorderLayout());
		rbPanel.add(rbCounterClockWise, BorderLayout.NORTH);
		rbPanel.add(rbClockWise, BorderLayout.SOUTH);
		rbPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 0));

		createGUI(title, message, autoComplete, DEFAULT_COLUMNS, 1, true, false,
				false, false, DialogType.GeoGebraEditor);
		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.add(inputPanel, BorderLayout.CENTER);
		centerPanel.add(rbPanel, BorderLayout.SOUTH);
		wrappedDialog.getContentPane().add(centerPanel, BorderLayout.CENTER);
		centerOnScreen();

		app.setComponentOrientation(wrappedDialog);
	}

	public boolean isCounterClockWise() {
		return rbCounterClockWise.isSelected();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		try {

			if (source == btOK || source == inputPanel.getTextComponent()) {
				String inputText = inputPanel.getText();

				// negative orientation ?
				if (rbClockWise.isSelected()) {
					inputText = "-(" + inputText + ")";
				}

				getInputHandler().processInput(inputText, this,
						new AsyncOperation<Boolean>() {

							@Override
							public void callback(Boolean ok) {
								setVisible(!ok);

							}
						});
			} else if (source == btCancel) {

				setVisible(false);
			}
		} catch (Exception ex) {
			// do nothing on uninitializedValue
			setVisible(false);
		}

	}

	@Override
	public void handleDialogVisibilityChange(boolean isVisible) {
		// nothing to do
	}
}
