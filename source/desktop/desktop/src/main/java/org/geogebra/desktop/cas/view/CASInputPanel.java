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

package org.geogebra.desktop.cas.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.geogebra.desktop.gui.inputfield.AutoCompleteTextFieldD;
import org.geogebra.desktop.gui.inputfield.KeyNavigation;
import org.geogebra.desktop.main.AppD;

/**
 * This panel is for the input.
 */
public class CASInputPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private AutoCompleteTextFieldD inputArea;

	private AppD app;

	/**
	 * @param app
	 *            application
	 */
	public CASInputPanel(AppD app) {
		this.app = app;

		setBackground(Color.white);

		setLayout(new BorderLayout(0, 0));

		// use autocomplete text field from input bar
		// but ignore Escape, Up, Down keys
		inputArea = new AutoCompleteTextFieldD(1, app, KeyNavigation.IGNORE,
				true);
		inputArea.setCASInput(true);
		inputArea.setAutoComplete(true);
		inputArea.setShowSymbolTableIcon(true);
		inputArea.setBorder(BorderFactory.createEmptyBorder());
		add(inputArea, BorderLayout.CENTER);
	}

	/**
	 * @param inValue
	 *            input text
	 */
	public void setInput(String inValue) {
		inputArea.setText(inValue);
	}

	/**
	 * @return input text
	 */
	public String getInput() {
		return inputArea.getText();
	}

	/**
	 * @return input component
	 */
	public AutoCompleteTextFieldD getInputArea() {
		return inputArea;
	}

	/**
	 * 
	 * @return true if the InputArea has been set focused successfully, false
	 *         otherwise
	 */
	public boolean setInputAreaFocused() {
		return inputArea.requestFocusInWindow();
	}

	@Override
	final public void setFont(Font ft) {
		super.setFont(ft);

		if (inputArea != null) {
			inputArea.setFont(ft);
		}

	}

	/**
	 * @param col
	 *            color
	 */
	public void setCommentColor(Color col) {
		if (col != null) {
			inputArea.setForeground(col);
		}
	}

	/**
	 * Updates autocomplete dictionary
	 */
	public void setLabels() {
		inputArea.setDictionary(true);
		inputArea.setLabels();
	}

	/**
	 * Update the orientation
	 */
	public void setOrientation() {
		app.setComponentOrientation(this);
	}

}
