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

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.gui.view.algebra.DialogType;
import org.geogebra.desktop.main.AppD;

/**
 * Input Dialog for a GeoAngle object with additional option to choose between
 * "clock wise" and "counter clockwise"
 * 
 * @author hohenwarter
 */
public class AngleInputDialogD extends InputDialogD {

	protected JRadioButton rbCounterClockWise;
	protected JRadioButton rbClockWise;

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
						ok -> setVisible(!ok));
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
