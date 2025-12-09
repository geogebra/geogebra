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

package org.geogebra.desktop.gui.inputfield;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.DateFormat;

import javax.swing.JFormattedTextField;

import org.geogebra.common.gui.VirtualKeyboardListener;
import org.geogebra.desktop.gui.GuiManagerD;
import org.geogebra.desktop.gui.virtualkeyboard.VirtualKeyboardD;

public class MyFormattedTextField extends JFormattedTextField
		implements FocusListener, VirtualKeyboardListener {

	private static final long serialVersionUID = 1L;

	GuiManagerD guiManager;

	/**
	 * @param guiManager gui manager
	 * @param format date format
	 */
	public MyFormattedTextField(GuiManagerD guiManager, DateFormat format) {
		super(format);
		this.guiManager = guiManager;
		addFocusListener(this);
	}

	@Override
	public void focusGained(FocusEvent e) {
		guiManager.setCurrentTextfield(this, false);
	}

	@Override
	public void focusLost(FocusEvent e) {
		guiManager.setCurrentTextfield(null,
				!(e.getOppositeComponent() instanceof VirtualKeyboardD));

	}

	@Override
	public void insertString(String text) {

		int start = getSelectionStart();
		int end = getSelectionEnd();
		// clear selection if there is one
		if (start != end) {
			int pos = getCaretPosition();
			String oldText = getText();
			StringBuilder sb = new StringBuilder();
			sb.append(oldText.substring(0, start));
			sb.append(oldText.substring(end));
			setText(sb.toString());
			if (pos < sb.length()) {
				setCaretPosition(pos);
			}
		}

		int pos = getCaretPosition();
		String oldText = getText();
		StringBuilder sb = new StringBuilder();
		sb.append(oldText.substring(0, pos));
		sb.append(text);
		sb.append(oldText.substring(pos));
		setText(sb.toString());

		setCaretPosition(pos + text.length());

	}

}
